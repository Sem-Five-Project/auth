package com.edu.tutor_platform.payment.controller;

import com.edu.tutor_platform.payment.service.PaymentService;
import com.edu.tutor_platform.payment.dto.PaymentRequestDTO;
import com.edu.tutor_platform.payment.dto.HashResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private static final Logger logger = Logger.getLogger(PaymentController.class.getName());

    @Autowired
    private PaymentService paymentService;

    @Value("${payhere.merchant.id}")
    private String merchantId;

    @PostMapping("/hash")
    public ResponseEntity<HashResponse> getPaymentHash(@RequestBody PaymentRequestDTO request) {
        logger.info("Generating payment hash for order ID: " + request.getOrderId());
        String hash = paymentService.generatePaymentHash(request);
        return ResponseEntity.ok(new HashResponse(merchantId, hash));
    }

    /**
     * PayHere webhook endpoint
     * Accepts form data (application/x-www-form-urlencoded)
     */
@PostMapping("/notify")
public ResponseEntity<String> handleNotification(@RequestParam MultiValueMap<String, String> formData) {
    logger.info("=== PayHere Webhook Received ===");
    logger.info("Form data keys: " + formData.keySet().toString());
    
    try {
        Map<String, String> notificationData = formData.toSingleValueMap();
        
        // Log all received parameters for debugging
        logger.info("=== Received PayHere Parameters ===");
        notificationData.forEach((key, value) -> {
            // Don't log sensitive data like md5sig in production
            if (!"md5sig".equalsIgnoreCase(key) && !"hash".equalsIgnoreCase(key)) {
                logger.info("Param: " + key + " = " + value);
            } else {
                logger.info("Param: " + key + " = [REDACTED for security]");
            }
        });
        logger.info("=== End PayHere Parameters ===");
        
        // Process the notification
        paymentService.handlePaymentNotification(notificationData);
        
        logger.info("PayHere notification processed successfully");
        // PayHere expects "OK" response for successful webhook processing
        return ResponseEntity.ok("OK");
        
    } catch (Exception e) {
        logger.severe("Error processing PayHere notification: " + e.getMessage());
        e.printStackTrace();
        
        // PayHere expects "ERROR" response for failed webhook processing
        return ResponseEntity.status(400).body("ERROR");
    }
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
