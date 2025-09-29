package com.edu.tutor_platform.tutorsearch.filter.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class TutorFilterResultDTO {
    private Long tutorId;
    private Long userId;
    private String bio;
    private BigDecimal hourlyRate;
    private Double rating;
    private Integer experienceMonths;
    private Boolean verified;
    private List<String> matchedSubjects;
    private BigDecimal matchedMinHourlyRate;
}