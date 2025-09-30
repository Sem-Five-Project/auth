package com.edu.tutor_platform.studentprofile.dto;

import com.edu.tutor_platform.studentprofile.entity.StudentProfileStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentDtoForAdmin {
    private Long studentId;
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String userName;
    private String profilePictureUrl;
    private StudentProfileStatus status;
    private Boolean accountLocked;
    private String adminNotes;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private LocalDateTime updatedAt;
    private String educationLevel;
}
