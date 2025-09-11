package com.edu.tutor_platform.scheduledtasks.service;

import com.edu.tutor_platform.clazz.service.ClassService;
import com.edu.tutor_platform.session.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SessionCreationService {
    private final SessionService sessionService;
    private final ClassService classService;

    @Autowired
    public SessionCreationService(SessionService sessionService, ClassService classService) {
        this.sessionService = sessionService;
        this.classService = classService;
    }


}
