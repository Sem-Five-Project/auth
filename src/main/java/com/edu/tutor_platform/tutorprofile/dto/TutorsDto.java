package com.edu.tutor_platform.tutorprofile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TutorsDto {
    private Long tutorId;
    private BigDecimal hourlyRate;
    private boolean verified;
    private String status;
    private String firstName;
    private String lastName;
}
