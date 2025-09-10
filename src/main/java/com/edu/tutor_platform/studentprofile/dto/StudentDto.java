package com.edu.tutor_platform.studentprofile.dto;

import com.edu.tutor_platform.studentprofile.entity.StudentProfileStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentDto {
    private Long studentId;
    private String firstName;
    private String lastName;
    private String email;
    private StudentProfileStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private String adminNotes;
    private String educationLevel;
}
