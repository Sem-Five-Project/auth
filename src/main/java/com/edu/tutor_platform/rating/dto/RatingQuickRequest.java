package com.edu.tutor_platform.rating.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingQuickRequest {
    // integer rating 1-5
    private Integer ratingValue;
    private Long tutorId;
    // treated as session id (user provided `class_id`) - assuming this is session id
    private Long class_id;
    private String feedback;
}
