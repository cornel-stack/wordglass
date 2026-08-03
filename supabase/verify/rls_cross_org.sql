-- Wordglass — RLS cross-org isolation check.
-- NOT a migration. Paste the whole thing into the Supabase SQL editor and run.
--
-- Proves two things about a member of org A:
--   1. A cannot SELECT org B's scripts.
--   2. A cannot INSERT a script carrying org B's org_id.
-- Plus one positive control (A can read its own script), so a 0 count reads as
-- "blocked by RLS", not "the query is just broken".
--
-- WHY THE ROLE SWITCH IS NON-NEGOTIABLE:
-- The SQL editor connects as a superuser (postgres), and superusers BYPASS RLS
-- entirely. Run these selects as-is and A would see B's rows — proving nothing.
-- So the script drops to the `authenticated` role and forges auth.uid() through
-- request.jwt.claims, which is exactly how PostgREST presents a logged-in user to
-- your policies. Only under that role are the policies actually exercised.
--
-- Runs in one transaction that ROLLBACKs at the end: no test data is left behind
-- and it is safe to re-run.

begin;

-- Fixed UUIDs so nothing has to be captured across role switches.
--   A = 11111111...  owner of org A (aaaaaaaa...)
--   B = 22222222...  owner of org B (bbbbbbbb...)

-- ---------------------------------------------------------------------------
-- Setup as superuser: two users, two orgs, one script each.
-- (If your auth.users has an extra NOT NULL column, add it here — or swap these
-- UUIDs for real ids from Authentication → Users and delete this insert.)
-- ---------------------------------------------------------------------------
insert into auth.users (instance_id, id, aud, role, email)
values
  ('00000000-0000-0000-0000-000000000000',
   '11111111-1111-1111-1111-111111111111',
   'authenticated', 'authenticated', 'a@wordglass.test'),
  ('00000000-0000-0000-0000-000000000000',
   '22222222-2222-2222-2222-222222222222',
   'authenticated', 'authenticated', 'b@wordglass.test');

-- Become a given user for the rest of the transaction: set both claim shapes
-- auth.uid() may read.
create or replace function pg_temp.become(uid uuid)
returns void language plpgsql as $$
begin
  perform set_config('request.jwt.claim.sub', uid::text, true);
  perform set_config('request.jwt.claims',
                     json_build_object('sub', uid, 'role', 'authenticated')::text,
                     true);
end;
$$;

-- Drop out of superuser. From here on, RLS is enforced.
set local role authenticated;

-- As user A: create org A (trigger makes A owner) + a script in it.
select pg_temp.become('11111111-1111-1111-1111-111111111111');
insert into public.orgs (id, name, created_by)
values ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Org A',
        '11111111-1111-1111-1111-111111111111');
insert into public.scripts (id, org_id, title, body, created_by)
values ('a5c41000-0000-0000-0000-000000000001',
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
        'A own script', 'A body',
        '11111111-1111-1111-1111-111111111111');

-- As user B: create org B + a script in it (this is the row A must NOT see).
select pg_temp.become('22222222-2222-2222-2222-222222222222');
insert into public.orgs (id, name, created_by)
values ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Org B',
        '22222222-2222-2222-2222-222222222222');
insert into public.scripts (id, org_id, title, body, created_by)
values ('b5c41000-0000-0000-0000-000000000001',
        'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
        'B secret script', 'B body',
        '22222222-2222-2222-2222-222222222222');

-- ---------------------------------------------------------------------------
-- Switch to user A. Everything below is evaluated under A's RLS context.
-- ---------------------------------------------------------------------------
select pg_temp.become('11111111-1111-1111-1111-111111111111');

-- ASSERTION 1: A cannot select B's scripts.  Also a positive control so a 0
-- can't be mistaken for a broken query.
select check_name, rows_visible, expected,
       case when rows_visible = expected then 'PASS' else 'FAIL' end as result
from (
  values
    ('A selects B''s scripts',
       (select count(*) from public.scripts
        where org_id = 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb'), 0),
    ('A selects own script (control)',
       (select count(*) from public.scripts
        where org_id = 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'), 1)
) as checks(check_name, rows_visible, expected)
order by check_name;

-- ASSERTION 2: A cannot insert a script carrying B's org_id. The WITH CHECK on
-- scripts_insert_writer must reject it (A has no role in org B). We catch the
-- error so the transaction survives to report and roll back cleanly. Look in the
-- Messages pane for this result.
do $$
begin
  insert into public.scripts (org_id, title, body, created_by)
  values ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'A injecting into B', '',
          '11111111-1111-1111-1111-111111111111');
  raise warning 'FAIL: A inserted a script with org B''s org_id';
exception
  when insufficient_privilege or check_violation then
    raise notice 'PASS: A blocked from inserting into org B (%)', sqlerrm;
end;
$$;

-- Back to superuser and undo everything.
reset role;
rollback;
