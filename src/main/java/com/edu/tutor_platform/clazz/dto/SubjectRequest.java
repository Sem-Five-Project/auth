package com.edu.tutor_platform.clazz.dto;

import lombok.Data;

@Data
public class SubjectRequest {
    private Long subjectId;
    private String subjectName;

    // public SubjectRequest(Long subjectId, String subjectName) {
    //     this.subjectId = subjectId;
    //     this.subjectName = subjectName;
    // }

    // // Getters and setters
    // public Long getSubjectId() { return subjectId; }
    // public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
    // public String getSubjectName() { return subjectName; }
    // public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

}
