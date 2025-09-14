CREATE TABLE monthly_class_participants (
    participant_id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    class_id BIGINT NOT NULL,
    payment_id BIGINT NOT NULL,
    month INT NOT NULL,  -- 1 to 12
    year INT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT fk_student FOREIGN KEY (student_id) REFERENCES student_profile(student_id),
    CONSTRAINT fk_class FOREIGN KEY (class_id) REFERENCES monthly_class(monthly_class_id),
    CONSTRAINT fk_payment FOREIGN KEY (payment_id) REFERENCES payment(payment_id),
    UNIQUE (student_id, class_id, month, year) -- avoid duplicates
);