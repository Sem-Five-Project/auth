package com.edu.tutor_platform.studentprofile.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import java.util.List;
import com.edu.tutor_platform.clazz.entity.ClassDoc;

public class StudentClassDto {
    private Long classId;
    private String className;
    private Long tutorId;
    private Long subjectId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String comment;
    private String linkForMeeting;
    private List<ClassDoc> docs;

    public StudentClassDto() {
    }

    public StudentClassDto(Long classId, String className, Long tutorId, Long subjectId, LocalDate date,
            LocalTime startTime, LocalTime endTime, String comment, String linkForMeeting, List<ClassDoc> docs) {
        this.classId = classId;
        this.className = className;
        this.tutorId = tutorId;
        this.subjectId = subjectId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.comment = comment;
        this.linkForMeeting = linkForMeeting;
        this.docs = docs;
    }

    // getters and setters
    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Long getTutorId() {
        return tutorId;
    }

    public void setTutorId(Long tutorId) {
        this.tutorId = tutorId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getLinkForMeeting() {
        return linkForMeeting;
    }

    public void setLinkForMeeting(String linkForMeeting) {
        this.linkForMeeting = linkForMeeting;
    }

    public List<ClassDoc> getDocs() {
        return docs;
    }

    public void setDocs(List<ClassDoc> docs) {
        this.docs = docs;
    }
}
