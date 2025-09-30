package com.edu.tutor_platform.session.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionStatsDto {
    private Long totalSessions;
    private Long upcomingSessions;
    private Long cancelledSessions;
    private Long completedSessions;
}
