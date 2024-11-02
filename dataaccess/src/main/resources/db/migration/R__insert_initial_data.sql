CREATE OR REPLACE FUNCTION insert_initial_data()
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

    CREATE TABLE IF NOT EXISTS role_profile (
        id BIGINT NOT NULL generated always as identity PRIMARY KEY,
        name VARCHAR(255) NOT NULL,
        value INT NOT NULL,
        description TEXT
    );

    CREATE TABLE IF NOT EXISTS user_role_assignment (
        id BIGINT NOT NULL generated always as identity PRIMARY KEY,
        user_id BIGINT NOT NULL,
        role_id BIGINT NOT NULL,
        FOREIGN KEY (user_id) REFERENCES user_profile(id),
        FOREIGN KEY (role_id) REFERENCES role_profile(id)
    );

    INSERT INTO role_profile (name, description, value) VALUES ('OPSADMIN', 'Operational System Administrator', 1);
    INSERT INTO role_profile (name, description, value) VALUES ('USER', 'Regular User', 2);

END;
$$
LANGUAGE plpgsql;
SELECT insert_initial_data();
DROP FUNCTION insert_initial_data();