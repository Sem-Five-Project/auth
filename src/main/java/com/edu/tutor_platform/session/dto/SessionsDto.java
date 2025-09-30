package com.edu.tutor_platform.session.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionsDto {
    private Long sessionId;
    private String title;
    private Long tutorId;
    private Long classId;
    private LocalDateTime startTime;
    private LocalTime duration;
    private String status;

}
