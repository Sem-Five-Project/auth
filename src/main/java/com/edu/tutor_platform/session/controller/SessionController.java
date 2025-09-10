package com.edu.tutor_platform.session.controller;


import com.edu.tutor_platform.session.dto.SessionDto;
import com.edu.tutor_platform.session.entity.Session;
import com.edu.tutor_platform.session.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sessions")
public class SessionController {
    private SessionService sessionService;

    @Autowired
    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping("")
    public ResponseEntity<List<Session>> getAllSessions() {
        List<Session> sessions = sessionService.getAllSessions();
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/ongoing")
    public ResponseEntity<List<Session>> getOngoingSessions() {
        List<Session> sessions = sessionService.getOngoingSessions();
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SessionDto> getSessionById(@PathVariable Long id) {
        SessionDto sessionDto = sessionService.getSessionById(id);
        return ResponseEntity.ok(sessionDto);


    }



}
