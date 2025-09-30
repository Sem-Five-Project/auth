//package com.edu.tutor_platform.payment.controller;
//
//import com.edu.tutor_platform.payment.service.PaymentService;
//import com.edu.tutor_platform.payment.service.AtomicPaymentBookingService;
//import com.edu.tutor_platform.payment.dto.PaymentRequestDTO;
//import com.edu.tutor_platform.payment.dto.HashResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.ResponseEntity;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.view.RedirectView;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.logging.Logger;
//
//@RestController
//@RequestMapping("/payment")
//@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"}, allowCredentials = "true")
//public class PaymentController {
//
//    private static final Logger logger = Logger.getLogger(PaymentController.class.getName());
//
//    @Autowired
//    private PaymentService paymentService;
//
//    @Autowired
//    private AtomicPaymentBookingService atomicPaymentBookingService;
//
//    @Value("${payhere.merchant.id}")
//    private String merchantId;
//
//    /**
//     * NEW: Atomic payment-booking endpoint with slot blocking
//     * Use this for slot-based payments that require atomic booking
//     */
//    @PostMapping("/atomic/initiate")
//    public ResponseEntity<?> initiateAtomicPaymentBooking(@RequestBody PaymentRequestDTO request) {
//        logger.info("Initiating atomic payment-booking for slot " + request.getSlotId() + " by student " + request.getStudentId());
//
//        try {
//            HashResponse response = atomicPaymentBookingService.initiateAtomicPaymentBooking(request);
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            logger.severe("Failed to initiate atomic payment-booking: " + e.getMessage());
//            Map<String, String> error = new HashMap<>();
//            error.put("message", e.getMessage());
//            return ResponseEntity.badRequest().body((Object) error);
//        }
//    }
//
//    /**
//     * Get transaction status (useful for frontend polling)
//     */
//    @GetMapping("/atomic/status/{orderId}")
//    public ResponseEntity<?> getTransactionStatus(@PathVariable String orderId) {
//        try {
//            Map<String, Object> status = atomicPaymentBookingService.getTransactionStatus(orderId);
//            return ResponseEntity.ok(status);
//        } catch (Exception e) {
//            Map<String, String> error = new HashMap<>();
//            error.put("message", e.getMessage());
//            return ResponseEntity.badRequest().body((Object) error);
//        }
//    }
//
//    /**
//     * LEGACY: Original payment hash endpoint (keep for non-slot payments)
//     */
//    @PostMapping("/hash")
//    public ResponseEntity<HashResponse> getPaymentHash(@RequestBody PaymentRequestDTO request) {
//        logger.info("Generating payment hash for order ID: " + request.getOrderId());
//        String hash = paymentService.generatePaymentHash(request);
//        return ResponseEntity.ok(new HashResponse(merchantId, hash));
//    }
//
//    /**
//     * PayHere webhook endpoint
//     * Accepts form data (application/x-www-form-urlencoded)
//     */
//@PostMapping("/notify")
//public ResponseEntity<String> handleNotification(@RequestParam MultiValueMap<String, String> formData) {
//    logger.info("=== PayHere Webhook Received ===");
//    logger.info("Form data keys: " + formData.keySet().toString());
//
//    try {
//        Map<String, String> notificationData = formData.toSingleValueMap();
//
//        // Log all received parameters for debugging
//        logger.info("=== Received PayHere Parameters ===");
//        notificationData.forEach((key, value) -> {
//            // Don't log sensitive data like md5sig in production
//            if (!"md5sig".equalsIgnoreCase(key) && !"hash".equalsIgnoreCase(key)) {
//                logger.info("Param: " + key + " = " + value);
//            } else {
//                logger.info("Param: " + key + " = [REDACTED for security]");
//            }
//        });
//        logger.info("=== End PayHere Parameters ===");
//
//        // Process the notification
//        paymentService.handlePaymentNotification(notificationData);
//
//        logger.info("PayHere notification processed successfully");
//        // PayHere expects "OK" response for successful webhook processing
//        return ResponseEntity.ok("OK");
//
//    } catch (Exception e) {
//        logger.severe("Error processing PayHere notification: " + e.getMessage());
//        e.printStackTrace();
//
//        // PayHere expects "ERROR" response for failed webhook processing
//        return ResponseEntity.status(400).body("ERROR");
//    }
//}
//
//
//    @GetMapping("/return")
//    public RedirectView handleReturn() {
//        logger.info("Handling successful payment return.");
//        return new RedirectView("/payment-success");
//    }
//
//    @GetMapping("/cancel")
//    public RedirectView handleCancel() {
//        logger.info("Handling payment cancellation.");
//        return new RedirectView("/payment-cancel");
//    }
//}
