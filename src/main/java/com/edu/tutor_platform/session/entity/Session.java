package com.edu.tutor_platform.session.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "session")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long sessionId;

    @Column(name = "tutor_id", nullable = false)
    private Long tutorId;

    @Column(name = "starting_time", nullable = false)
    private LocalDateTime startingTime;

    @Column(name = "ending_time", nullable = false)
    private LocalDateTime endingTime;

    @Column(name = "subject", nullable = false, length = 100)
    private String subject;

    @Column(name = "part", length = 100)
    private String part;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "link_for_meeting", length = 255)
    private String linkForMeeting;

    @Column(name = "student_count", nullable = false)
    private Integer studentCount = 0;

    // Constructors
    public Session() {}

    public Session(Long tutorId, LocalDateTime startingTime, LocalDateTime endingTime, 
                   String subject, String part, String status, String linkForMeeting) {
        this.tutorId = tutorId;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.subject = subject;
        this.part = part;
        this.status = status;
        this.linkForMeeting = linkForMeeting;
        this.studentCount = 0;
    }

    // Getters and Setters
    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getTutorId() {
        return tutorId;
    }

    public void setTutorId(Long tutorId) {
        this.tutorId = tutorId;
    }

    public LocalDateTime getStartingTime() {
        return startingTime;
    }

    public void setStartingTime(LocalDateTime startingTime) {
        this.startingTime = startingTime;
    }

    public LocalDateTime getEndingTime() {
        return endingTime;
    }

    public void setEndingTime(LocalDateTime endingTime) {
        this.endingTime = endingTime;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLinkForMeeting() {
        return linkForMeeting;
    }

    public void setLinkForMeeting(String linkForMeeting) {
        this.linkForMeeting = linkForMeeting;
    }

    public Integer getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(Integer studentCount) {
        this.studentCount = studentCount;
    }
}