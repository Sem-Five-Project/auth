package com.edu.tutor_platform.tutorprofile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TutorStatsDto {
    private Long totalTutors;
    private Long activeTutors;
    private Long verifiedTutors;
    private Double averageRating;
    private long newTutorsThisMonth;
}
