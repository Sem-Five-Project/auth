package com.edu.tutor_platform.session.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "session_student")
@IdClass(SessionStudentId.class)
public class SessionStudent {

    @Id
    @Column(name = "session_id")
    private Long sessionId;

    @Id
    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "payment_id")
    private Long paymentId;

    // Constructors
    public SessionStudent() {}

    public SessionStudent(Long sessionId, Long studentId, Long paymentId) {
        this.sessionId = sessionId;
        this.studentId = studentId;
        this.paymentId = paymentId;
    }

    // Getters and Setters
    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }
}