# --- !Ups

ALTER TABLE auth.user
  ADD COLUMN signed_up_at TIMESTAMPTZ DEFAULT NOW();

# --- !Downs

ALTER TABLE auth.user
  DROP COLUMN signed_up_at;