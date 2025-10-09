package com.edu.tutor_platform.rating.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddRatingRequest {

    @NotNull(message = "Session ID is required")
    private Long sessionId;

    @NotNull(message = "Tutor ID is required")
    private Long tutorId;

    @NotNull(message = "Rating value is required")
    @DecimalMin(value = "1.0", message = "Rating must be at least 1.0")
    @DecimalMax(value = "5.0", message = "Rating must not exceed 5.0")
    private BigDecimal ratingValue;

    private String reviewText;
}