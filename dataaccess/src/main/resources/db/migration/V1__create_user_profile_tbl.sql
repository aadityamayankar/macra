CREATE OR REPLACE FUNCTION create_user_profile_tbl()
RETURNS void AS
$$
BEGIN
    CREATE TABLE IF NOT EXISTS user_profile (
        id BIGINT NOT NULL generated always as identity PRIMARY KEY,
        name VARCHAR(255) NOT NULL,
        email VARCHAR(255) NOT NULL UNIQUE,
        modified_at TIMESTAMPTZ,
        created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
        miscflags BIGINT NOT NULL DEFAULT 0
    );
    CREATE INDEX IF NOT EXISTS idx_user_profile_email ON user_profile(email);
END;
$$
LANGUAGE plpgsql;
SELECT create_user_profile_tbl();
DROP FUNCTION create_user_profile_tbl();