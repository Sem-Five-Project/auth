package com.edu.tutor_platform.vidConf.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.edu.tutor_platform.vidConf.service.TokenService;

import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody; // ✅ Correct one
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edu.tutor_platform.vidConf.dto.MeetingRequest;

@RestController
@RequestMapping("/api/meeting")
public class JitsiController {
    private static final Logger logger = LoggerFactory.getLogger(JitsiController.class);

    @Autowired
    private TokenService tokenService;

    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createMeeting(@RequestBody MeetingRequest request) {
        logger.info("Token generation endpoint hit********************* request: {}", request.getUserName());
        String token = tokenService.generateToken(request.getRoomName(), request.getUserName());
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
            return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(response);
    }
}
