package com.edu.tutor_platform.payment.controller;

import com.edu.tutor_platform.payment.service.PaymentService;

import jakarta.validation.Valid;

import com.edu.tutor_platform.payment.dto.PaymentRequestDTO;
import com.edu.tutor_platform.payment.dto.PaymentConfirmDTO;
import com.edu.tutor_platform.payment.dto.HashResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payment")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"}, allowCredentials = "true")
public class PaymentController {


    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Autowired
    private PaymentService paymentService;
    
    @Autowired

    @Value("${payhere.merchant.id}")
    private String merchantId;

      @PostMapping("/bookings/confirm")
public ResponseEntity<String> confirmPayment(@Valid @RequestBody PaymentConfirmDTO dto) {
    paymentService.completePayment(
        dto.getPaymentId(),
        dto.getTutorId(),
        dto.getSlotId(),
        dto.getSubjectId(),
        dto.getLanguageId(),
        dto.getClassTypeId()
    );
    return ResponseEntity.ok("Payment confirmed successfully!");
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



  
}
