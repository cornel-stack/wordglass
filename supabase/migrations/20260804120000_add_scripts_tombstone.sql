-- Slice 01 — add the scripts tombstone (soft-delete).
--
-- The init migration (20260804000000_init_orgs_members_scripts.sql) is already applied to the
-- linked remote project, so this is a SEPARATE migration rather than an edit to history.
--
-- `deleted_at` null = live; non-null = the row is a tombstone. Deletion is never a `delete from`
-- — it stamps `deleted_at` so the row survives to propagate the deletion when sync lands
-- (handoff §10.1, "removed from all your devices"). Nothing syncs in slice 01; the local Room
-- schema carries the same column from v1 so slice-13 sync is a mapping, not a migration.
--
-- RLS is unchanged: a soft-deleted row is still an org row, so the existing org_role() policies
-- apply. Slice-13 sync filters `deleted_at is null` for live reads.

alter table public.scripts
  add column deleted_at timestamptz;
