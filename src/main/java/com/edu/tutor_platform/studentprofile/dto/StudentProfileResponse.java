package com.edu.tutor_platform.studentprofile.dto;

import com.edu.tutor_platform.studentprofile.enums.EducationalLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProfileResponse {
    
    // Student profile data
    private Long studentId;
    private Long userId;
    private String adminNotes;
    private Short status;
    private EducationalLevel educationalLevel;
    private String educationLevel;
    private String membership;
    
    // User data
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String profileImage;
    private String fullName;
    
    // Status helper
    private String statusDescription;
    private Boolean isActive;
}

