CREATE OR REPLACE FUNCTION add_order_profile_tbl()
RETURNS void AS
$$
BEGIN
    CREATE TABLE IF NOT EXISTS order_profile (
        id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        user_id BIGINT NOT NULL REFERENCES user_profile(id),
        event_id BIGINT NOT NULL REFERENCES event_profile(id),
        ticket_id BIGINT NOT NULL REFERENCES ticket_profile(id),
        quantity INT NOT NULL,
        total_amount DECIMAL NOT NULL,
        payment_status TEXT NOT NULL,
        razorpay_order_id TEXT NOT NULL,
        razorpay_payment_id TEXT NOT NULL,
        modified_at TIMESTAMPTZ,
        created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
        miscflags BIGINT NOT NULL DEFAULT 0
    );

    CREATE INDEX IF NOT EXISTS idx_order_profile_user_id ON order_profile(user_id);
    CREATE INDEX IF NOT EXISTS idx_razorpay_order_id_payment_id ON order_profile(razorpay_order_id, razorpay_payment_id);
END;
$$
LANGUAGE plpgsql;

SELECT add_order_profile_tbl();
DROP FUNCTION add_order_profile_tbl;