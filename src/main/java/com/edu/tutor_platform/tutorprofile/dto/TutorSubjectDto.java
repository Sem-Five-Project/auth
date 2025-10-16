package com.edu.tutor_platform.tutorprofile.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TutorSubjectDto {
    private Long id;
    private String subjectName;
    private BigDecimal hourlyRate;
    private String verification;
}

