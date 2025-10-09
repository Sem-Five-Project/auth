package com.edu.tutor_platform.adminprofile.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SessionOverviewDto {
    private String day;
    private long completedSessionCount;
    private long cancelledSessionCount;
}
