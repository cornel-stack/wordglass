-- Wordglass — first migration
-- Tables: orgs, org_members, scripts. RLS on all three, tenancy keyed on org_id.
-- A solo user is an org of one. RLS is the only place tenancy is enforced — never
-- in application code.
--
-- The recursion trap this migration is built to avoid:
--   An org_members RLS policy that answers "is the current user in this org?" by
--   selecting from org_members will re-trigger RLS on org_members, which selects
--   from org_members, ... Postgres aborts with "infinite recursion detected in
--   policy". The fix is to answer that question inside a SECURITY DEFINER function,
--   which runs as the function owner and bypasses RLS on the tables it touches.

-- ---------------------------------------------------------------------------
-- Extensions
-- ---------------------------------------------------------------------------

create extension if not exists "pgcrypto";  -- gen_random_uuid()

-- ---------------------------------------------------------------------------
-- Tables
-- ---------------------------------------------------------------------------

create table public.orgs (
  id         uuid        primary key default gen_random_uuid(),
  name       text        not null,
  created_by uuid        not null references auth.users (id) on delete restrict,
  created_at timestamptz not null default now()
);

-- A user's role within one org. Composite PK means one role per (org, user).
-- role is constrained to the three defined roles; anything else is rejected.
create table public.org_members (
  org_id     uuid        not null references public.orgs (id)      on delete cascade,
  user_id    uuid        not null references auth.users (id)       on delete cascade,
  role       text        not null check (role in ('owner', 'writer', 'recorder')),
  created_at timestamptz not null default now(),
  primary key (org_id, user_id)
);

create table public.scripts (
  id         uuid        primary key default gen_random_uuid(),
  org_id     uuid        not null references public.orgs (id) on delete cascade,
  title      text        not null default '',
  body       text        not null default '',
  created_by uuid        references auth.users (id) on delete set null,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create index org_members_user_id_idx on public.org_members (user_id);
create index scripts_org_id_idx      on public.scripts (org_id);

-- ---------------------------------------------------------------------------
-- Membership resolver — SECURITY DEFINER, so it does NOT recurse through RLS
-- ---------------------------------------------------------------------------

-- Returns the caller's role in target_org, or NULL if they are not a member.
-- Every policy below is expressed in terms of this one function:
--   membership  = org_role(...) is not null
--   can write   = org_role(...) in ('owner','writer')
--   is owner    = org_role(...) = 'owner'
--
-- SECURITY DEFINER + a pinned search_path is what makes this safe to call from
-- inside an org_members policy: the SELECT here runs with the definer's rights
-- and skips RLS, so it cannot trigger the policy that called it.
create or replace function public.org_role(target_org uuid)
returns text
language sql
security definer
set search_path = public
stable
as $$
  select role
  from public.org_members
  where org_id = target_org
    and user_id = auth.uid();
$$;

-- ---------------------------------------------------------------------------
-- Bootstrap trigger — first member of a new org
-- ---------------------------------------------------------------------------

-- The chicken-and-egg problem: only owners may add members (see policies below),
-- but a brand-new org has no owner yet, so no one is allowed to insert the first
-- one. Rather than punching a hole in the INSERT policy (which would let anyone
-- self-appoint as owner of any org), we make org creation atomically create the
-- creator's owner row. Runs SECURITY DEFINER so it bypasses the org_members
-- INSERT policy. The guard skips service-role/no-auth inserts, which have no
-- auth.uid() to attribute ownership to.
create or replace function public.handle_new_org()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
begin
  if auth.uid() is not null then
    insert into public.org_members (org_id, user_id, role)
    values (new.id, auth.uid(), 'owner');
  end if;
  return new;
end;
$$;

create trigger on_org_created
  after insert on public.orgs
  for each row execute function public.handle_new_org();

-- ---------------------------------------------------------------------------
-- updated_at maintenance for scripts
-- ---------------------------------------------------------------------------

-- Stamps updated_at on every UPDATE so the column can't be set stale or forged by
-- the client. No SECURITY DEFINER needed — it only rewrites a field on the row
-- already being written, and touches no other table.
create or replace function public.set_updated_at()
returns trigger
language plpgsql
as $$
begin
  new.updated_at = now();
  return new;
end;
$$;

create trigger scripts_set_updated_at
  before update on public.scripts
  for each row execute function public.set_updated_at();

-- ---------------------------------------------------------------------------
-- Enable RLS. With RLS on and no policy, the default is deny-all.
-- ---------------------------------------------------------------------------

alter table public.orgs        enable row level security;
alter table public.org_members enable row level security;
alter table public.scripts     enable row level security;

-- Base table grants. RLS narrows these row-by-row; without the grant the
-- authenticated role cannot touch the table at all regardless of policy.
grant select, insert, update, delete
  on public.orgs, public.org_members, public.scripts
  to authenticated;

-- ---------------------------------------------------------------------------
-- Policies: orgs
-- ---------------------------------------------------------------------------

-- SELECT: you can see an org only if you are a member of it.
create policy orgs_select_member
  on public.orgs for select
  to authenticated
  using (public.org_role(id) is not null);

-- INSERT: any authenticated user may create an org. The on_org_created trigger
-- immediately makes them its owner, so "create an org" and "be its owner" are a
-- single step. with check pins created_by to the caller so ownership metadata
-- cannot be forged for someone else.
create policy orgs_insert_authenticated
  on public.orgs for insert
  to authenticated
  with check (created_by = auth.uid());

-- UPDATE: only an owner may rename or otherwise change the org row. Both using
-- (which rows you may target) and with check (what you may leave behind) require
-- ownership.
create policy orgs_update_owner
  on public.orgs for update
  to authenticated
  using (public.org_role(id) = 'owner')
  with check (public.org_role(id) = 'owner');

-- DELETE: only an owner may delete the org. Cascades wipe its members and scripts.
create policy orgs_delete_owner
  on public.orgs for delete
  to authenticated
  using (public.org_role(id) = 'owner');

-- ---------------------------------------------------------------------------
-- Policies: org_members  (every one routed through org_role to avoid recursion)
-- ---------------------------------------------------------------------------

-- SELECT: any member of an org may see the full roster of that org.
create policy org_members_select_member
  on public.org_members for select
  to authenticated
  using (public.org_role(org_id) is not null);

-- INSERT: only an owner may add members to an org. (The very first owner row is
-- created by the on_org_created trigger, not through this policy.)
create policy org_members_insert_owner
  on public.org_members for insert
  to authenticated
  with check (public.org_role(org_id) = 'owner');

-- UPDATE: only an owner may change a member's role. using guards which membership
-- rows are targetable; with check re-asserts ownership of the destination org so a
-- row cannot be moved into an org the caller does not own.
create policy org_members_update_owner
  on public.org_members for update
  to authenticated
  using (public.org_role(org_id) = 'owner')
  with check (public.org_role(org_id) = 'owner');

-- DELETE: an owner may remove anyone; any member may remove themselves (leave the
-- org). This is deliberately the one place a non-owner may delete a membership row.
create policy org_members_delete_owner_or_self
  on public.org_members for delete
  to authenticated
  using (
    public.org_role(org_id) = 'owner'
    or user_id = auth.uid()
  );

-- ---------------------------------------------------------------------------
-- Policies: scripts
-- ---------------------------------------------------------------------------

-- SELECT: any member of the owning org may read its scripts — recorders included,
-- since they read from the script to record.
create policy scripts_select_member
  on public.scripts for select
  to authenticated
  using (public.org_role(org_id) is not null);

-- INSERT: only owners and writers may author scripts. Recorders are read-only here.
-- with check also pins created_by to the caller.
create policy scripts_insert_writer
  on public.scripts for insert
  to authenticated
  with check (
    public.org_role(org_id) in ('owner', 'writer')
    and created_by = auth.uid()
  );

-- UPDATE: only owners and writers may edit a script. using restricts which scripts
-- are editable; with check keeps the row inside an org the caller can still write to.
create policy scripts_update_writer
  on public.scripts for update
  to authenticated
  using (public.org_role(org_id) in ('owner', 'writer'))
  with check (public.org_role(org_id) in ('owner', 'writer'));

-- DELETE: an owner may delete any script in the org; a writer may delete only
-- their own. A writer cannot delete a script another writer authored.
create policy scripts_delete_owner_or_author
  on public.scripts for delete
  to authenticated
  using (
    public.org_role(org_id) = 'owner'
    or created_by = auth.uid()
  );
