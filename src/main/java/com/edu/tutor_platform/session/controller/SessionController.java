package com.edu.tutor_platform.session.controller;


import com.edu.tutor_platform.session.dto.SessionDto;
import com.edu.tutor_platform.session.entity.Session;
import com.edu.tutor_platform.session.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<Session>> getAllSessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Session> sessions = sessionService.getSessions(page, size);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/ongoing")
    public ResponseEntity<List<Session>> getOngoingSessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Session> sessions = sessionService.getOngoingSessions(page, size);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SessionDto> getSessionById(@PathVariable Long id) {
        SessionDto sessionDto = sessionService.getSessionById(id);
        return ResponseEntity.ok(sessionDto);

    }





}
