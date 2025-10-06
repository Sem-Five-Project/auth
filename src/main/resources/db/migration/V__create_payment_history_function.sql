-- Function to get student payment history with tutor and subject details.
-- This encapsulates the complex join logic within the database.
CREATE OR REPLACE FUNCTION public.get_student_payment_history(
    p_student_id bigint,
    p_period text DEFAULT 'all'
)
RETURNS TABLE (
    "amount" numeric,
    "paymentTime" timestamp with time zone,
    "status" text,
    "tutorName" text,
    "subjectName" text
)
LANGUAGE plpgsql
AS $$
BEGIN
    -- Validate period input to prevent SQL injection, though date_trunc is generally safe.
    IF lower(p_period) NOT IN ('all', 'day', 'week', 'month', 'year') THEN
        p_period := 'all';
    END IF;

    RETURN QUERY
    SELECT
        p.amount,
        p.payment_time,
        p.status,
        CONCAT(u.first_name, ' ', u.last_name),
        s.name
    FROM
        public.payment p
    LEFT JOIN public.class c ON p.class_id = c.class_id
    LEFT JOIN public.tutor_profile tp ON c.tutor_id = tp.tutor_id
    LEFT JOIN public.users u ON tp.user_id = u.id
    LEFT JOIN public.subject s ON c.subject_id = s.subject_id
    WHERE
        p.student_id = p_student_id
        AND (lower(p_period) = 'all' OR p.payment_time >= date_trunc(p_period, NOW()))
    ORDER BY
        p.payment_time DESC;
END;
$$;