package com.edu.tutor_platform.session.service;

import com.edu.tutor_platform.session.dto.SessionDto;
import com.edu.tutor_platform.session.dto.SessionStatsDto;
import com.edu.tutor_platform.session.dto.SessionsDto;
import com.edu.tutor_platform.session.entity.Session;
import com.edu.tutor_platform.session.exeption.SessionNotFoundException;
import com.edu.tutor_platform.session.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;



    public SessionDto getSessionById(Long id) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new SessionNotFoundException("Session not found with id: " + id));

        return SessionDto.builder()
                .sessionId(session.getSessionId())
                .title(session.getSessionName())
                .tutorId(session.getClassEntity().getTutorId())
                .subjectId(session.getClassEntity().getSubjectId())
                .createdAt(session.getCreatedAt())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .classId(session.getClassEntity().getClassId())
                .status(session.getStatus())
                .moderatorLink(session.getLinkForHost())
                .participantLink(session.getLinkForMeeting())
                .notificationSent(session.isNotificationSent())
                .build();
    }

    public List<Session> getSessionsStartingBetween(LocalDateTime now, LocalDateTime remindTime) {
        return sessionRepository.findByStartTimeBetween(now, remindTime);
    }

    public void setNotificationSent(Session session) {
        session.setNotificationSent(true);
        sessionRepository.save(session);
    }
    public Page<SessionsDto> searchSessions(
            Long studentId,
            Long tutorId,
            Long subjectId,
            String status,
            LocalDateTime fromTime,
            LocalDateTime toTime,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Session> sessions = sessionRepository.searchSession(
                studentId,
                tutorId,
                subjectId,
                status,
                fromTime,
                toTime,
                pageable
        );
        return sessions.map(session -> SessionsDto.builder()
                .sessionId(session.getSessionId())
                .title(session.getSessionName())
                .tutorId(session.getClassEntity().getTutorId())
                .classId(session.getClassEntity().getClassId())
                .startTime(session.getStartTime())
                .duration(java.time.LocalTime.ofSecondOfDay(
                        java.time.Duration.between(session.getStartTime(), session.getEndTime()).getSeconds()))
                .status(session.getStatus() != null ? session.getStatus() : null)
                .build());

    }


    public Page<SessionsDto> getOngoingSessions(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("startTime").descending());
        LocalDateTime now = LocalDateTime.now();

        Page<Session> sessions = sessionRepository.findByStartTimeBeforeAndEndTimeAfter(now, now, pageable);

        return sessions.map(session -> SessionsDto.builder()
                .sessionId(session.getSessionId())
                .title(session.getSessionName())
                .tutorId(session.getClassEntity().getTutorId())
                .classId(session.getClassEntity().getClassId())
                .startTime(session.getStartTime())
                .duration(java.time.LocalTime.ofSecondOfDay(
                        java.time.Duration.between(session.getStartTime(), session.getEndTime()).getSeconds()))
                .status(session.getStatus())
                .build());
    }

    public SessionDto updateSession(Long id, SessionDto sessionDto) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new SessionNotFoundException("Session not found with id: " + id));

        session.setSessionName(sessionDto.getTitle() != null ? sessionDto.getTitle() : session.getSessionName());
        session.setStartTime(sessionDto.getStartTime() != null ? sessionDto.getStartTime() : session.getStartTime());
        session.setEndTime(sessionDto.getEndTime() != null ? sessionDto.getEndTime() : session.getEndTime());
        session.setStatus(sessionDto.getStatus() != null ? sessionDto.getStatus() : session.getStatus());

        Session updatedSession = sessionRepository.save(session);

        return SessionDto.builder()
                .sessionId(updatedSession.getSessionId())
                .title(updatedSession.getSessionName())
                .tutorId(updatedSession.getClassEntity().getTutorId())
                .subjectId(updatedSession.getClassEntity().getSubjectId())
                .createdAt(updatedSession.getCreatedAt())
                .startTime(updatedSession.getStartTime())
                .endTime(updatedSession.getEndTime())
                .classId(updatedSession.getClassEntity().getClassId())
                .status(updatedSession.getStatus())
                .moderatorLink(updatedSession.getLinkForHost())
                .participantLink(updatedSession.getLinkForMeeting())
                .notificationSent(updatedSession.isNotificationSent())
                .build();
    }

    public SessionStatsDto getSessionStats() {
        Long totalSessions = sessionRepository.count();
        Long upcomingSessions = sessionRepository.countByStatus("SCHEDULED");
        Long cancelledSessions = sessionRepository.countByStatus("CANCELLED");
        Long completedSessions = sessionRepository.countByStatus("COMPLETED");


        return SessionStatsDto.builder()
                .totalSessions(totalSessions)
                .upcomingSessions(upcomingSessions)
                .cancelledSessions(cancelledSessions)
                .completedSessions(completedSessions)
                .build();


    }
    public Session getSessionEntityById(Long id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new SessionNotFoundException("Session not found with id: " + id));

    }
}
