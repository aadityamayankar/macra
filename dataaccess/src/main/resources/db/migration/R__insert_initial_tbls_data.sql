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
    INSERT INTO user_password_info (user_id, password_hash) VALUES (v_opsadmin_user_id, 'opsadmin'); -- @TODO: hash this password
END;
$$
LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION insert_initial_data()
RETURNS void AS
$$
BEGIN
    INSERT INTO role_profile (name, description, value) VALUES ('OPSADMIN', 'Operational System Administrator', 1) ON CONFLICT DO NOTHING;
    INSERT INTO role_profile (name, description, value) VALUES ('USER', 'Regular User', 2) ON CONFLICT DO NOTHING;

    PERFORM insert_opsadmin_user();
END;
$$
LANGUAGE plpgsql;
SELECT insert_initial_data();
DROP FUNCTION insert_initial_data();
DROP FUNCTION insert_opsadmin_user();