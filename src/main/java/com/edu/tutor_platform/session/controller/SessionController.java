package com.edu.tutor_platform.session.controller;


import com.edu.tutor_platform.session.dto.SessionDto;
import com.edu.tutor_platform.session.dto.SessionStatsDto;
import com.edu.tutor_platform.session.dto.SessionsDto;
import com.edu.tutor_platform.session.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.time.LocalDateTime;
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
    public ResponseEntity<Page<SessionsDto>> searchSessions(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long tutorId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toTime,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<SessionsDto> sessions = sessionService.searchSessions(studentId, tutorId, subjectId, status, fromTime, toTime, page, size);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/ongoing")
    public ResponseEntity<Page<SessionsDto>> getOngoingSessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<SessionsDto> sessions = sessionService.getOngoingSessions(page, size);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SessionDto> getSessionById(@PathVariable Long id) {
        SessionDto sessionDto = sessionService.getSessionById(id);
        return ResponseEntity.ok(sessionDto);

    }

    @GetMapping("/stats")
    public ResponseEntity<SessionStatsDto> getSessionStats() {
        SessionStatsDto stats = sessionService.getSessionStats();
        return ResponseEntity.ok(stats);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SessionDto> updateSession(@PathVariable Long id, @RequestBody SessionDto sessionDto) {
        SessionDto updatedSession = sessionService.updateSession(id, sessionDto);
        return ResponseEntity.ok(updatedSession);
    }






}
