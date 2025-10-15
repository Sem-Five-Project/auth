-- Defines function repay_and_reassign_class used for next-month repayments
-- Safe to re-run: drop and create

DROP FUNCTION IF EXISTS repay_and_reassign_class(
    TEXT,
    BIGINT,
    BIGINT,
    TIMESTAMP,
    NUMERIC,
    SMALLINT,
    SMALLINT,
    JSONB,
    BIGINT[]
);

CREATE OR REPLACE FUNCTION repay_and_reassign_class(
    p_payment_id TEXT,
    p_class_id BIGINT,
    p_student_id BIGINT,
    p_payment_time TIMESTAMP,
    p_amount NUMERIC,
    p_month SMALLINT,
    p_year SMALLINT,
    p_slots JSONB,             -- { availability_id: [slot_ids...] }
    p_nextmonthslots BIGINT[]  -- optional next month slots
)
RETURNS TEXT AS $$
DECLARE
    v_all_locked BOOLEAN := TRUE;
    v_slot_id BIGINT;
    avail_key TEXT;
    avail_val JSONB;
BEGIN
    -- 1) Validate slot locks
    FOR avail_key, avail_val IN
        SELECT key, value FROM jsonb_each(p_slots)
    LOOP
        FOR v_slot_id IN SELECT jsonb_array_elements_text(avail_val)::BIGINT
        LOOP
            IF NOT EXISTS (
                SELECT 1 FROM slot_instance
                WHERE slot_id = v_slot_id AND status = 'RESERVED'
            ) THEN
                v_all_locked := FALSE;
                EXIT;
            END IF;
        END LOOP;
        IF NOT v_all_locked THEN
            EXIT;
        END IF;
    END LOOP;

    -- 2) If any slot not locked → refund
    IF NOT v_all_locked THEN
        UPDATE payment
        SET payment_status = 'REFUNDED',
            completed_at = NOW()
        WHERE payment_id = p_payment_id;

        INSERT INTO notification (user_id, message, notification_type, sent_at)
        VALUES (
            (SELECT user_id FROM student_profile WHERE student_id = p_student_id LIMIT 1),
            'Some of your selected slots are no longer available. Payment refunded.',
            'REMINDER'::notification_type_enum,
            NOW()
        );

        RETURN 'REFUND';
    END IF;

    -- 3) Mark payment success
    UPDATE payment
    SET payment_status = 'SUCCESS',
        completed_at = COALESCE(p_payment_time, NOW()),
        amount = p_amount
    WHERE payment_id = p_payment_id;

    -- 4) Assign all locked slots to existing class
    FOR avail_key, avail_val IN
        SELECT key, value FROM jsonb_each(p_slots)
    LOOP
        FOR v_slot_id IN SELECT jsonb_array_elements_text(avail_val)::BIGINT
        LOOP
            UPDATE slot_instance
            SET status = 'RESERVED'
            WHERE slot_id = v_slot_id;

            INSERT INTO class_slots (class_id, slot_id)
            VALUES (p_class_id, v_slot_id)
            ON CONFLICT DO NOTHING;
        END LOOP;
    END LOOP;

    -- 5) Handle next-month slots (optional)
    IF p_nextmonthslots IS NOT NULL THEN
        FOR v_slot_id IN SELECT unnest(p_nextmonthslots)
        LOOP
            UPDATE slot_instance
            SET status = 'RESERVED'
            WHERE slot_id = v_slot_id;

            INSERT INTO class_slots (class_id, slot_id)
            VALUES (p_class_id, v_slot_id)
            ON CONFLICT DO NOTHING;
        END LOOP;
    END IF;

    -- 6) Add payment record to class_payment
    INSERT INTO class_payment (participant_id, month, year)
    VALUES (
        (SELECT participant_id FROM participants WHERE class_id = p_class_id AND student_id = p_student_id LIMIT 1),
        p_month,
        p_year
    )
    ON CONFLICT (participant_id, month, year) DO NOTHING;

    -- 7) Send success notification
    INSERT INTO notification (user_id, message, notification_type, sent_at)
    VALUES (
        (SELECT user_id FROM student_profile WHERE student_id = p_student_id LIMIT 1),
        'Payment for next month was successful. Slots assigned.',
        'REMINDER'::notification_type_enum,
        NOW()
    );

    RETURN 'SUCCESS';
END;
$$ LANGUAGE plpgsql;