package com.edu.tutor_platform.studentprofile.dto;

import lombok.Data;

@Data
public class StudentProfileResponse {
    private Long studentId;
    private String educationLevel;
    private String name;
    private String email;
}

