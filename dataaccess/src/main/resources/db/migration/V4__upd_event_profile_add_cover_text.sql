CREATE OR REPLACE FUNCTION upd_event_profile_add_cover_bytea()
RETURNS VOID AS
$$
BEGIN
    ALTER TABLE event_profile ADD COLUMN IF NOT EXISTS cover TEXT;
END;
$$
LANGUAGE plpgsql;

SELECT upd_event_profile_add_cover_bytea();
DROP FUNCTION upd_event_profile_add_cover_bytea();