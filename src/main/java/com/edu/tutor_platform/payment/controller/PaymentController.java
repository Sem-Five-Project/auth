package com.edu.tutor_platform.payment.controller;

import com.edu.tutor_platform.payment.service.PaymentService;

import jakarta.validation.Valid;

import com.edu.tutor_platform.payment.dto.PaymentRequestDTO;
import com.edu.tutor_platform.payment.dto.PaymentCompleteDTO;
import com.edu.tutor_platform.payment.dto.PaymentCompleteResponse;
import com.edu.tutor_platform.payment.dto.HashResponse;
import com.edu.tutor_platform.payment.dto.PaymentStatusResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.edu.tutor_platform.payment.dto.RefundRequestDTO;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/payment")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"}, allowCredentials = "true")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);


    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Autowired
    private PaymentService paymentService;
    
    @Autowired

    @Value("${payhere.merchant.id}")
    private String merchantId;

      @PostMapping("/bookings/confirm")
public ResponseEntity<PaymentCompleteResponse> confirmPayment(@Valid @RequestBody PaymentCompleteDTO dto) {
    try {
        // Convert slots map to JSON string for SQL (e.g., {"1006":[332,333]})
        String slotsJson = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(dto.getSlots());
        String status = paymentService.completePayment(
            dto.getPaymentId(),
            slotsJson,
            dto.getTutorId(),
            dto.getSubjectId(),
            dto.getLanguageId(),
            dto.getClassTypeId(),
            dto.getStudentId(),
            dto.getPaymentTime(),
            dto.getAmount(),
            dto.getMonth(),
            dto.getYear(),
            dto.getNextMonthSlots()
        );
        boolean success = "BOOKED".equalsIgnoreCase(status);
        String message = success ? "Payment completed and class booked successfully." : "Payment refunded as some slots were not locked.";
        return ResponseEntity.ok(new PaymentCompleteResponse(success, status, message));
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(new PaymentCompleteResponse(false, "ERROR", e.getMessage()));
    }
}


 

     @PostMapping("/hash")
    public ResponseEntity<HashResponse> getPaymentHash(@RequestBody PaymentRequestDTO request) {
    String hash = paymentService.generatePaymentHash(request);
        return ResponseEntity.ok(new HashResponse(merchantId, hash));
    }
    @PostMapping("/initiate")
        public ResponseEntity<?> initiatePayment(@RequestBody PaymentRequestDTO request) {
            try {
                var payment = paymentService.initiatePayment(request);
                Map<String, Object> resp = new HashMap<>();
                resp.put("payment_id", payment.getPaymentId());
                resp.put("order_id", payment.getOrderId());
                resp.put("status", payment.getStatus());
                resp.put("expires_at", payment.getExpiresAt());
                return ResponseEntity.ok(resp);
            } catch (Exception e) {
                Map<String, Object> err = new HashMap<>();
                err.put("message", e.getMessage());
                return ResponseEntity.badRequest().body(err);
            }
        }

        @GetMapping("/validate")
        public ResponseEntity<Map<String, Object>> checkPaymentStatus(@RequestBody Map<String, Object> body) {
            Object idObj = body.get("payment_id");
            boolean pending = false;
            if (idObj instanceof String pid && !pid.isBlank()) {
                pending = paymentService.isPaymentPendingByUuid(pid);
            }
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", pending);
            return ResponseEntity.ok(resp);
        }


        @PostMapping("/refund")
        public ResponseEntity<Map<String, Object>> refund(@RequestBody RefundRequestDTO request) {
            try {
                Map<String, Object> result = paymentService.refundPayment(request);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                Map<String, Object> err = new HashMap<>();
                err.put("success", false);
                err.put("message", e.getMessage());
                Map<String, Object> wrapper = new HashMap<>();
                wrapper.put("process_refund", err);
                return ResponseEntity.badRequest().body(wrapper);
            }
        }

        @GetMapping("/status")
        public ResponseEntity<?> getPaymentStatus(@RequestParam("orderId") String orderId) {
            try {
                PaymentStatusResponse response = paymentService.getPaymentStatusByOrderId(orderId);
                return ResponseEntity.ok(response);
            } catch (IllegalArgumentException ex) {
                Map<String, Object> err = new HashMap<>();
                err.put("message", ex.getMessage());
                return ResponseEntity.badRequest().body(err);
            } catch (RuntimeException ex) {
                Map<String, Object> err = new HashMap<>();
                err.put("message", ex.getMessage());
                return ResponseEntity.status(404).body(err);
            }
        }

    @PostMapping("/payhere/notify")
    public ResponseEntity<String> handlePayHereNotify(@RequestParam Map<String, String> payload) {
        try {
            log.info("Received PayHere notification for order_id: {}", payload.get("order_id"));
            paymentService.handleNotify(payload);
            return ResponseEntity.ok("OK");
        } catch (Exception ex) {
            // Respond 200 OK so PayHere stops retrying, but log the problem for investigation.
            log.error("Error processing PayHere notification: {}", ex.getMessage(), ex);
            return ResponseEntity.ok("OK");
        }
    }



  
}
