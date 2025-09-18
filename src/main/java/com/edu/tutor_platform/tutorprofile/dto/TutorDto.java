package com.edu.tutor_platform.tutorprofile.dto;

import com.edu.tutor_platform.tutorprofile.entity.TutorLanguage;
import com.edu.tutor_platform.tutorprofile.entity.TutorProfileStatus;
import com.edu.tutor_platform.tutorprofile.entity.TutorSubject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TutorDto {
    private Long tutorId;
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String userName;
    private String profilePictureUrl;
    private String bio;
    private BigDecimal hourlyRate;
    private List<TutorSubject> subjects;
    private List<TutorLanguage> statuses;
    private Boolean verified;
    private String status;
    private Boolean accountLocked;
    private String adminNotes;
    private BigDecimal rating;
    private Integer experienceInMonths;
    private BigDecimal classCompletionRate;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private LocalDateTime updatedAt;
}
