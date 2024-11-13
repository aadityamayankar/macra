CREATE OR REPLACE FUNCTION insert_initial_data()
RETURNS void AS
$$
BEGIN
    CREATE TABLE IF NOT EXISTS user_profile (
        id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        name TEXT NOT NULL,
        email TEXT NOT NULL UNIQUE,
        modified_at TIMESTAMPTZ,
        created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
        miscflags BIGINT NOT NULL DEFAULT 0
    );
    CREATE INDEX IF NOT EXISTS idx_user_profile_email ON user_profile(email);

    CREATE TABLE IF NOT EXISTS role_profile (
        id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        name TEXT NOT NULL,
        value INT NOT NULL,
        modified_at TIMESTAMPTZ,
        created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
        description TEXT,
        miscflags BIGINT NOT NULL DEFAULT 0
    );

    CREATE TABLE IF NOT EXISTS user_role_assignment (
        id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        user_id BIGINT NOT NULL,
        role_id BIGINT NOT NULL,
        modified_at TIMESTAMPTZ,
        created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
        miscflags BIGINT NOT NULL DEFAULT 0,
        FOREIGN KEY (user_id) REFERENCES user_profile(id),
        FOREIGN KEY (role_id) REFERENCES role_profile(id)
    );

    CREATE TABLE IF NOT EXISTS user_password_info (
        id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        user_id BIGINT NOT NULL,
        password_hash TEXT NOT NULL,
        modified_at TIMESTAMPTZ,
        created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
        miscflags BIGINT NOT NULL DEFAULT 0,
        FOREIGN KEY (user_id) REFERENCES user_profile(id)
    );

    CREATE TABLE IF NOT EXISTS oauth2_client (
        id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        client_id TEXT NOT NULL UNIQUE,
        client_secret TEXT NOT NULL,
        redirect_uris TEXT[],
        scopes TEXT[],
        created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
        modified_at TIMESTAMPTZ,
        miscflags BIGINT NOT NULL DEFAULT 0
    );

    CREATE INDEX IF NOT EXISTS idx_oauth2_client_client_id ON oauth2_client(client_id);
    
    CREATE TABLE IF NOT EXISTS city_profile (
        id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        name TEXT NOT NULL,
        country TEXT NOT NULL,
        modified_at TIMESTAMPTZ,
        created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
        miscflags BIGINT NOT NULL DEFAULT 0
    );

    CREATE TABLE IF NOT EXISTS event_profile (
        id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        name TEXT NOT NULL,
        description TEXT,
        start_date TIMESTAMPTZ NOT NULL,
        end_date TIMESTAMPTZ NOT NULL,
        location TEXT,
        city_id BIGINT NOT NULL REFERENCES city_profile(id),
        modified_at TIMESTAMPTZ,
        created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
        miscflags BIGINT NOT NULL DEFAULT 0
    );

END;
$$
LANGUAGE plpgsql;
SELECT insert_initial_data();
DROP FUNCTION insert_initial_data();