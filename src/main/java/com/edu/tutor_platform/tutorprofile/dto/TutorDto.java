package com.edu.tutor_platform.tutorprofile.dto;

import com.edu.tutor_platform.tutorprofile.entity.TutorProfileStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TutorDto {
    private Long tutorId;
    private String bio;
    private BigDecimal hourlyRate;
    private boolean verified;
    private TutorProfileStatus status;
    private String adminNotes;
}
