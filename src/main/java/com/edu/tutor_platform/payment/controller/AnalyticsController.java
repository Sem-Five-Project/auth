package com.edu.tutor_platform.payment.controller;

import com.edu.tutor_platform.payment.service.PaymentService;
import com.edu.tutor_platform.payment.dto.TutorEarningsAnalyticsDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/analytics")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:3000",
        "https://edimy-front-end.vercel.app" }, allowCredentials = "true")
public class AnalyticsController {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsController.class);

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/tutor/{tutorId}/earnings")
    public ResponseEntity<?> getTutorEarningsAnalytics(@PathVariable("tutorId") Long tutorId) {
        try {
            TutorEarningsAnalyticsDTO analytics = paymentService.getTutorEarningsAnalytics(tutorId);
            return ResponseEntity.ok(analytics);
        } catch (IllegalArgumentException ex) {
            Map<String, Object> err = new HashMap<>();
            err.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(err);
        } catch (Exception ex) {
            log.error("Unexpected error fetching tutor analytics for {}: {}", tutorId, ex.getMessage(), ex);
            Map<String, Object> err = new HashMap<>();
            err.put("message", "Failed to compute analytics");
            return ResponseEntity.status(500).body(err);
        }
    }

    @GetMapping("/tutor/{tutorId}/sessions")
    public ResponseEntity<?> getTutorSessionCounts(@PathVariable("tutorId") Long tutorId) {
        try {
            List<Integer> sessionCounts = paymentService.getTutorSessionCountsByMonth(tutorId);
            return ResponseEntity.ok(sessionCounts);
        } catch (IllegalArgumentException ex) {
            Map<String, Object> err = new HashMap<>();
            err.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(err);
        } catch (Exception ex) {
            log.error("Unexpected error fetching tutor session counts for {}: {}", tutorId, ex.getMessage(), ex);
            Map<String, Object> err = new HashMap<>();
            err.put("message", "Failed to compute session counts");
            return ResponseEntity.status(500).body(err);
        }
    }

}
