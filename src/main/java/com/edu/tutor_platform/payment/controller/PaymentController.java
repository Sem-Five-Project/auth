package com.edu.tutor_platform.payment.controller;

import com.edu.tutor_platform.payment.service.PaymentService;

import jakarta.validation.Valid;

import com.edu.tutor_platform.payment.service.AtomicPaymentBookingService;
import com.edu.tutor_platform.payment.dto.PaymentRequestDTO;
import com.edu.tutor_platform.payment.dto.PaymentConfirmDTO;
import com.edu.tutor_platform.payment.dto.HashResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/payment")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"}, allowCredentials = "true")
public class PaymentController {

    private static final Logger logger = Logger.getLogger(PaymentController.class.getName());

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private AtomicPaymentBookingService atomicPaymentBookingService;

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

        @PostMapping("/atomic/initiate")
    public ResponseEntity<?> initiateAtomicPaymentBooking(@RequestBody PaymentRequestDTO request) {
        logger.info("Initiating atomic payment-booking for slot " + request.getSlotId() + " by student " + request.getStudentId());
        
        try {
            HashResponse response = atomicPaymentBookingService.initiateAtomicPaymentBooking(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.severe("Failed to initiate atomic payment-booking: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body((Object) error);
        }
    }
    
    /**
     * Get transaction status (useful for frontend polling)
     */
    @GetMapping("/atomic/status/{orderId}")
    public ResponseEntity<?> getTransactionStatus(@PathVariable String orderId) {
        try {
            Map<String, Object> status = atomicPaymentBookingService.getTransactionStatus(orderId);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body((Object) error);
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



    @GetMapping("/return")
    public RedirectView handleReturn() {
        logger.info("Handling successful payment return.");
        return new RedirectView("/payment-success");
    }

    @GetMapping("/cancel")
    public RedirectView handleCancel() {
        logger.info("Handling payment cancellation.");
        return new RedirectView("/payment-cancel");
    }
}
