CREATE OR REPLACE FUNCTION upd_ticket_profile_add_modified_at_default()
RETURNS VOID AS
$$
BEGIN
    ALTER TABLE ticket_profile ALTER COLUMN modified_at SET DEFAULT CURRENT_TIMESTAMP;
END;
$$
LANGUAGE plpgsql;

SELECT upd_ticket_profile_add_modified_at_default();
DROP FUNCTION upd_ticket_profile_add_modified_at_default();