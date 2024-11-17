CREATE OR REPLACE FUNCTION insert_opsadmin_user()
RETURNS VOID AS
$$
DECLARE
    v_opsadmin_role_id BIGINT;
    v_opsadmin_user_id BIGINT;
BEGIN
    INSERT INTO user_profile (name, email, miscflags) VALUES ('opsadmin', 'opsadmin@ibento.com', 1<<30) RETURNING id INTO v_opsadmin_user_id;
    SELECT id INTO v_opsadmin_role_id FROM role_profile WHERE value = 1;
    INSERT INTO user_role_assignment (user_id, role_id) VALUES (v_opsadmin_user_id, v_opsadmin_role_id);
    INSERT INTO user_password_info (user_id, password_hash) VALUES (v_opsadmin_user_id, '$2a$10$RJP5kiuGJ.jbqySGjzUqBenGjbBrBjB8h73Je4hfJZObCHreBYNNu'); -- password
END;
$$
LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION insert_default_roles()
RETURNS VOID AS
$$
BEGIN
    INSERT INTO role_profile (name, description, value) VALUES ('OPSADMIN', 'Operational System Administrator', 1) ON CONFLICT DO NOTHING;
    INSERT INTO role_profile (name, description, value) VALUES ('USER', 'Regular User', 2) ON CONFLICT DO NOTHING;
END;
$$
LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION insert_oauth2_client()
RETURNS VOID AS
$$
BEGIN
    INSERT INTO oauth2_client (client_id, client_secret, redirect_uris, scopes) -- secret
    VALUES ('ibento', '$2a$10$RJP5kiuGJ.jbqySGjzUqBenGjbBrBjB8h73Je4hfJZObCHreBYNNu', '{http://localhost:7001/login/oauth2/code/self}', '{read, write, openid}');
END;
$$
LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION insert_initial_cities()
RETURNS VOID AS
$$
BEGIN
    INSERT INTO city_profile (name, country, miscflags) VALUES ('Mumbai', 'India', 0);
    INSERT INTO city_profile (name, country, miscflags) VALUES ('Delhi', 'India', 0);
    INSERT INTO city_profile (name, country, miscflags) VALUES ('Bangalore', 'India', 0);
    INSERT INTO city_profile (name, country, miscflags) VALUES ('Hyderabad', 'India', 0);
    INSERT INTO city_profile (name, country, miscflags) VALUES ('Pune', 'India', 0);
    INSERT INTO city_profile (name, country, miscflags) VALUES ('Jaipur', 'India', 0);
END;
$$
LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION insert_initial_data()
RETURNS VOID AS
$$
BEGIN
    PERFORM insert_default_roles();
    PERFORM insert_oauth2_client();
    PERFORM insert_opsadmin_user();
    PERFORM insert_initial_cities();
END;
$$
LANGUAGE plpgsql;
SELECT insert_initial_data();
DROP FUNCTION insert_initial_data();
DROP FUNCTION insert_default_roles();
DROP FUNCTION insert_oauth2_client();
DROP FUNCTION insert_opsadmin_user();
DROP FUNCTION insert_initial_cities();