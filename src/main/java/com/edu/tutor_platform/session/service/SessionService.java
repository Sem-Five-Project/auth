package com.edu.tutor_platform.session.service;

import com.edu.tutor_platform.session.entity.Session;
import com.edu.tutor_platform.session.repository.SessionRepository;
import com.edu.tutor_platform.studentprofile.entity.StudentProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;

    public List<StudentProfile> getParticipants(Session session) {
        return sessionRepository.findParticipantsBySessionId(session.getSessionId());
    }
}
