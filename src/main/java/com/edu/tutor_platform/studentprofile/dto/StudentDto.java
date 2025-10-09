package com.edu.tutor_platform.studentprofile.dto;


import com.edu.tutor_platform.studentprofile.entity.StudentProfileStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentDto {
    private long studentId;
    private String firstName;
    private String lastName;
    private String email;
    private StudentProfileStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private String educationLevel;
    private String adminNotes;
}
