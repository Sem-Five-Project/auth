package com.edu.tutor_platform.payment.Controller;

import com.edu.tutor_platform.payment.Service.PaymentService;
import com.edu.tutor_platform.payment.dto.PaymentDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/initiate")
    public ResponseEntity<Map<String, Object>> initiatePayment(@RequestBody PaymentDTO request) {
        Map<String, Object> paymentParams = paymentService.initiatePayment(
                request.getStudentId(),
                request.getSessionId(),
                request.getAmount(),
                request.getCurrency(),
                request.getPaymentMethod(),
                request.getPaymentType(),
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPhone(),
                request.getAddress(),
                request.getCity(),
                request.getCountry()
        );
        return ResponseEntity.ok(paymentParams);
    }

    @PostMapping("/callback")
    public ResponseEntity<String> handleCallback(@RequestParam Map<String, String> callbackParams) {
        try {
            paymentService.handlePaymentCallback(callbackParams);
            return ResponseEntity.ok("Payment notification processed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Payment notification processing failed: " + e.getMessage());
        }
    }
}
