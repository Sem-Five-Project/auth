package com.edu.tutor_platform.session.entity;

import java.io.Serializable;
import java.util.Objects;

public class SessionStudentId implements Serializable {

    private Long sessionId;
    private Long studentId;

    // Constructors
    public SessionStudentId() {}

    public SessionStudentId(Long sessionId, Long studentId) {
        this.sessionId = sessionId;
        this.studentId = studentId;
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

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionStudentId that = (SessionStudentId) o;
        return Objects.equals(sessionId, that.sessionId) && 
               Objects.equals(studentId, that.studentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId, studentId);
    }
}