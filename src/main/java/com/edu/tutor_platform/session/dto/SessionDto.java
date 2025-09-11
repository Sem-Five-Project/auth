package com.edu.tutor_platform.session.dto;

import com.edu.tutor_platform.session.entity.SessionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionDto {
    private Long sessionId;
    private String title;
    private Long tutorId;
//    private String tutorName;
//    private String subject;
    private Long subjectId;
//    private List<Long> studentIds;
    private LocalDateTime createdAt;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
//    private Long totalPayment;
    private Long classId;
//    private String className;
    private SessionStatus status;
    private String moderatorLink;
    private String participantLink;
    private boolean notificationSent;



}
