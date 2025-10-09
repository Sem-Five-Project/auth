package com.edu.tutor_platform.rating.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingResponse {

    private Long ratingId;
    private Long studentId;
    private String studentName;
    private Long tutorId;
    private String tutorName;
    private Long sessionId;
    private String sessionName;
    private BigDecimal ratingValue;
    private String reviewText;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}