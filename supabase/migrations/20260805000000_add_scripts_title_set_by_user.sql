-- Slice 01 — add scripts.title_set_by_user (editable title, auto-fill until edited).
--
-- The prior migrations are already applied to the linked remote project, so this is a SEPARATE
-- migration rather than an edit to history.
--
-- false = the title still tracks the body's first line (the editor re-derives it on body edits);
-- true = the user has taken the title over and the body must not overwrite it. Carried in the
-- schema so the decoupling survives slice-13 sync: another device must not re-derive a user's
-- chosen title from the body. Mirrors the local Room column `titleSetByUser` (v2), keeping the
-- "sync is a mapping, not a migration" invariant (handoff §10, Addendum 2026-08-05).
--
-- Default false so every existing row keeps deriving from the body, which is what those rows
-- already did — no existing title changes behaviour.
--
-- RLS is unchanged: this is still an org row, so the existing org_role() policies apply.

alter table public.scripts
  add column title_set_by_user boolean not null default false;
