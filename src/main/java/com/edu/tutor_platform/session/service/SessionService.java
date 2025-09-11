package com.edu.tutor_platform.session.service;

import com.edu.tutor_platform.session.dto.SessionDto;
import com.edu.tutor_platform.session.entity.Session;
import com.edu.tutor_platform.session.exeption.SessionNotFoundException;
import com.edu.tutor_platform.session.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;



    public List<Session> getAllSessions() {
        return sessionRepository.findAll();
    }

    public List<Session> getOngoingSessions() {
        LocalDateTime now = LocalDateTime.now();
        return sessionRepository.findByStartTimeBeforeAndEndTimeAfter(now, now);
    }


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

    public void updateSessionLinks(Session session, String joinUrl, String startUrl) {
        session.setLinkForMeeting(joinUrl);
        session.setLinkForHost(startUrl);
        sessionRepository.save(session);
    }
}
