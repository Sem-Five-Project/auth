
//package com.edu.tutor_platform.payment.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import com.edu.tutor_platform.payment.dto.PaymentRequestDTO;
//import com.edu.tutor_platform.payment.dto.PaymentNotificationDTO;
//import com.edu.tutor_platform.payment.entity.Payment;
//import com.edu.tutor_platform.payment.repository.PaymentRepository;
//import com.edu.tutor_platform.payment.util.HashUtil;
//import com.edu.tutor_platform.tutorprofile.entity.TutorProfile;
//import com.edu.tutor_platform.tutorprofile.repository.TutorProfileRepository;
//import java.util.Map;
//import java.util.Optional;
//import java.util.logging.Logger;
//
//@Service
//public class PaymentService {
//
//    private static final Logger logger = Logger.getLogger(PaymentService.class.getName());
//
//    @Autowired
//    private HashUtil hashUtil;
//    @Autowired
//    private PaymentRepository paymentRepository;
//    @Autowired
//    private TutorProfileRepository tutorProfileRepository;
//    @Autowired
//    private AtomicPaymentBookingService atomicPaymentBookingService;
//
//    @Value("${payhere.merchant.id}")
//    private String merchantId;
//
//    public String generatePaymentHash(PaymentRequestDTO request) {
//        return hashUtil.generateHash(merchantId, request.getOrderId(), request.getAmount(), request.getCurrency());
//    }
//
//    public void handlePaymentNotification(PaymentNotificationDTO notification) {
//        if (!hashUtil.verifyNotification(notification, merchantId)) {
//            throw new RuntimeException("Invalid signature");
//        }
//
//        if ("2".equals(notification.getStatus_code())) {  // Payment Success
//            try {
//                // Check if this is an atomic payment-booking transaction
//                Optional<Payment> existingPayment = paymentRepository.findByOrderId(notification.getOrder_id());
//
//                if (existingPayment.isPresent()) {
//                    // This is an atomic transaction - complete it
//                    atomicPaymentBookingService.completeAtomicPaymentBooking(notification.getOrder_id());
//                    logger.info("Atomic payment-booking completed for order: " + notification.getOrder_id());
//                } else {
//                    // Legacy payment handling (non-atomic)
//                    Payment payment = new Payment();
//                    payment.setOrderId(notification.getOrder_id());
//                    payment.setAmount(Double.parseDouble(notification.getPayhere_amount()));
//                    payment.setCurrency(notification.getPayhere_currency());
//                    payment.setStatus("SUCCESS");
//
//                    // Set IDs from custom form data
//                    try {
//                        if (notification.getStudent_id() != null && !notification.getStudent_id().isEmpty()) {
//                            payment.setStudentId(Long.parseLong(notification.getStudent_id()));
//                            logger.info("Set student ID: " + notification.getStudent_id());
//                        }
//                        if (notification.getTutor_id() != null && !notification.getTutor_id().isEmpty()) {
//                            payment.setTutorId(Long.parseLong(notification.getTutor_id()));
//                            logger.info("Set tutor ID: " + notification.getTutor_id());
//                        }
//                        if (notification.getClass_id() != null && !notification.getClass_id().isEmpty()) {
//                            payment.setClassId(Long.parseLong(notification.getClass_id()));
//                            logger.info("Set class ID: " + notification.getClass_id());
//                        }
//                    } catch (NumberFormatException e) {
//                        logger.warning("Invalid ID format in notification: " + e.getMessage());
//                    }
//
//                    paymentRepository.save(payment);
//                    logger.info("Legacy payment saved successfully for Order ID: " + notification.getOrder_id());
//                }
//            } catch (Exception e) {
//                logger.severe("Error processing successful payment: " + e.getMessage());
//                throw new RuntimeException("Failed to process successful payment: " + e.getMessage());
//            }
//        } else {
//            // Payment failed or cancelled
//            String failureReason = "Payment failed with status: " + notification.getStatus_code();
//            logger.warning("Payment failed for order " + notification.getOrder_id() + " - " + failureReason);
//
//            // Check if this is an atomic transaction and cancel it
//            Optional<Payment> existingPayment = paymentRepository.findByOrderId(notification.getOrder_id());
//            if (existingPayment.isPresent()) {
//                atomicPaymentBookingService.cancelAtomicPaymentBooking(notification.getOrder_id(), failureReason);
//            }
//        }
//    }
//
//    // Overloaded method to handle Map<String, String> from webhook
//    public void handlePaymentNotification(Map<String, String> notificationData) {
//        try {
//            // Log received data for debugging
//            logger.info("Received notification data: " + notificationData.toString());
//
//            // Convert Map to PaymentNotificationDTO with null checks
//            PaymentNotificationDTO notification = new PaymentNotificationDTO();
//            notification.setMerchant_id(getFormValue(notificationData, "merchant_id"));
//            notification.setOrder_id(getFormValue(notificationData, "order_id"));
//            notification.setPayhere_amount(getFormValue(notificationData, "payhere_amount"));
//            notification.setPayhere_currency(getFormValue(notificationData, "payhere_currency"));
//            notification.setStatus_code(getFormValue(notificationData, "status_code"));
//            notification.setMd5sig(getFormValue(notificationData, "md5sig"));
//
//            // Extract custom fields
//            notification.setStudent_id(getFormValue(notificationData, "student_id"));
//            notification.setTutor_id(getFormValue(notificationData, "tutor_id"));
//            notification.setClass_id(getFormValue(notificationData, "class_id"));
//            notification.setCard_holder_name(getFormValue(notificationData, "card_holder_name"));
//            notification.setPayment_method(getFormValue(notificationData, "payment_method"));
//
//            // Validate all required fields are present
//            validateNotificationFields(notification);
//
//            // Log notification details
//            logger.info("Processing notification for Order ID: " + notification.getOrder_id() +
//                       ", Amount: " + notification.getPayhere_amount() +
//                       ", Status: " + notification.getStatus_code() +
//                       ", Student ID: " + notification.getStudent_id() +
//                       ", Tutor ID: " + notification.getTutor_id() +
//                       ", Class ID: " + notification.getClass_id());
//
//            // Call the existing method with the DTO
//            handlePaymentNotification(notification);
//        } catch (Exception e) {
//            logger.severe("Failed to process payment notification: " + e.getMessage());
//            throw new RuntimeException("Failed to process payment notification: " + e.getMessage(), e);
//        }
//    }
//
//    private String getFormValue(Map<String, String> data, String key) {
//        // Try exact match first
//        String value = data.get(key);
//        if (value != null && !value.trim().isEmpty()) {
//            return value.trim();
//        }
//
//        // Try case-insensitive match for common PayHere field name variations
//        for (Map.Entry<String, String> entry : data.entrySet()) {
//            if (entry.getKey().equalsIgnoreCase(key)) {
//                return entry.getValue() != null ? entry.getValue().trim() : "";
//            }
//        }
//
//        // Check for common PayHere field name variations
//        if ("md5sig".equalsIgnoreCase(key)) {
//            value = data.get("md5Sig");
//            if (value == null) value = data.get("hash");
//        } else if ("merchant_id".equalsIgnoreCase(key)) {
//            value = data.get("merchantId");
//        } else if ("order_id".equalsIgnoreCase(key)) {
//            value = data.get("orderId");
//        } else if ("status_code".equalsIgnoreCase(key)) {
//            value = data.get("statusCode");
//        }
//
//        return value != null ? value.trim() : "";
//    }
//
//    private void validateNotificationFields(PaymentNotificationDTO notification) {
//        if (isNullOrEmpty(notification.getMerchant_id())) {
//            throw new RuntimeException("Missing required field: merchant_id");
//        }
//        if (isNullOrEmpty(notification.getOrder_id())) {
//            throw new RuntimeException("Missing required field: order_id");
//        }
//        if (isNullOrEmpty(notification.getPayhere_amount())) {
//            throw new RuntimeException("Missing required field: payhere_amount");
//        }
//        if (isNullOrEmpty(notification.getPayhere_currency())) {
//            throw new RuntimeException("Missing required field: payhere_currency");
//        }
//        if (isNullOrEmpty(notification.getStatus_code())) {
//            throw new RuntimeException("Missing required field: status_code");
//        }
//        if (isNullOrEmpty(notification.getMd5sig())) {
//            throw new RuntimeException("Missing required field: md5sig");
//        }
//    }
//
//    private boolean isNullOrEmpty(String value) {
//        return value == null || value.trim().isEmpty();
//    }
//}

// package com.edu.tutor_platform.payment.service;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Service;
// import com.edu.tutor_platform.payment.dto.PaymentRequestDTO;
// import com.edu.tutor_platform.payment.dto.PaymentNotificationDTO;
// import com.edu.tutor_platform.payment.entity.Payment;
// import com.edu.tutor_platform.payment.repository.PaymentRepository;
// import com.edu.tutor_platform.payment.util.HashUtil;
// import com.edu.tutor_platform.tutorprofile.entity.TutorProfile;
// import com.edu.tutor_platform.tutorprofile.repository.TutorProfileRepository;
// import java.util.Map;
// import java.util.Optional;
// import java.util.logging.Logger;

// @Service
// public class PaymentService {
    
//     private static final Logger logger = Logger.getLogger(PaymentService.class.getName());
    
//     @Autowired
//     private HashUtil hashUtil;
//     @Autowired
//     private PaymentRepository paymentRepository;
//     @Autowired
//     private TutorProfileRepository tutorProfileRepository;
//     @Autowired
//     private AtomicPaymentBookingService atomicPaymentBookingService;

//     @Value("${payhere.merchant.id}")
//     private String merchantId;

//     public String generatePaymentHash(PaymentRequestDTO request) {
//         return hashUtil.generateHash(merchantId, request.getOrderId(), request.getAmount(), request.getCurrency());
//     }

//     public void handlePaymentNotification(PaymentNotificationDTO notification) {
//         if (!hashUtil.verifyNotification(notification, merchantId)) {
//             throw new RuntimeException("Invalid signature");
//         }

//         if ("2".equals(notification.getStatus_code())) {  // Payment Success
//             try {
//                 // Check if this is an atomic payment-booking transaction
//                 Optional<Payment> existingPayment = paymentRepository.findByOrderId(notification.getOrder_id());
                
//                 if (existingPayment.isPresent()) {
//                     // This is an atomic transaction - complete it
//                     atomicPaymentBookingService.completeAtomicPaymentBooking(notification.getOrder_id());
//                     logger.info("Atomic payment-booking completed for order: " + notification.getOrder_id());
//                 } else {
//                     // Legacy payment handling (non-atomic)
//                     Payment payment = new Payment();
//                     payment.setOrderId(notification.getOrder_id());
//                     payment.setAmount(Double.parseDouble(notification.getPayhere_amount()));
//                     payment.setCurrency(notification.getPayhere_currency());
//                     payment.setStatus("SUCCESS");
                    
//                     // Set IDs from custom form data
//                     try {
//                         if (notification.getStudent_id() != null && !notification.getStudent_id().isEmpty()) {
//                             payment.setStudentId(Long.parseLong(notification.getStudent_id()));
//                             logger.info("Set student ID: " + notification.getStudent_id());
//                         }
//                         if (notification.getTutor_id() != null && !notification.getTutor_id().isEmpty()) {
//                             payment.setTutorId(Long.parseLong(notification.getTutor_id()));
//                             logger.info("Set tutor ID: " + notification.getTutor_id());
//                         }
//                         if (notification.getClass_id() != null && !notification.getClass_id().isEmpty()) {
//                             payment.setClassId(Long.parseLong(notification.getClass_id()));
//                             logger.info("Set class ID: " + notification.getClass_id());
//                         }
//                     } catch (NumberFormatException e) {
//                         logger.warning("Invalid ID format in notification: " + e.getMessage());
//                     }
                    
//                     paymentRepository.save(payment);
//                     logger.info("Legacy payment saved successfully for Order ID: " + notification.getOrder_id());
//                 }
//             } catch (Exception e) {
//                 logger.severe("Error processing successful payment: " + e.getMessage());
//                 throw new RuntimeException("Failed to process successful payment: " + e.getMessage());
//             }
//         } else {
//             // Payment failed or cancelled
//             String failureReason = "Payment failed with status: " + notification.getStatus_code();
//             logger.warning("Payment failed for order " + notification.getOrder_id() + " - " + failureReason);
            
//             // Check if this is an atomic transaction and cancel it
//             Optional<Payment> existingPayment = paymentRepository.findByOrderId(notification.getOrder_id());
//             if (existingPayment.isPresent()) {
//                 atomicPaymentBookingService.cancelAtomicPaymentBooking(notification.getOrder_id(), failureReason);
//             }
//         }
//     }

//     // Overloaded method to handle Map<String, String> from webhook
//     public void handlePaymentNotification(Map<String, String> notificationData) {
//         try {
//             // Log received data for debugging
//             logger.info("Received notification data: " + notificationData.toString());
            
//             // Convert Map to PaymentNotificationDTO with null checks
//             PaymentNotificationDTO notification = new PaymentNotificationDTO();
//             notification.setMerchant_id(getFormValue(notificationData, "merchant_id"));
//             notification.setOrder_id(getFormValue(notificationData, "order_id"));
//             notification.setPayhere_amount(getFormValue(notificationData, "payhere_amount"));
//             notification.setPayhere_currency(getFormValue(notificationData, "payhere_currency"));
//             notification.setStatus_code(getFormValue(notificationData, "status_code"));
//             notification.setMd5sig(getFormValue(notificationData, "md5sig"));
            
//             // Extract custom fields
//             notification.setStudent_id(getFormValue(notificationData, "student_id"));
//             notification.setTutor_id(getFormValue(notificationData, "tutor_id"));
//             notification.setClass_id(getFormValue(notificationData, "class_id"));
//             notification.setCard_holder_name(getFormValue(notificationData, "card_holder_name"));
//             notification.setPayment_method(getFormValue(notificationData, "payment_method"));

//             // Validate all required fields are present
//             validateNotificationFields(notification);

//             // Log notification details
//             logger.info("Processing notification for Order ID: " + notification.getOrder_id() +
//                        ", Amount: " + notification.getPayhere_amount() +
//                        ", Status: " + notification.getStatus_code() +
//                        ", Student ID: " + notification.getStudent_id() +
//                        ", Tutor ID: " + notification.getTutor_id() +
//                        ", Class ID: " + notification.getClass_id());

//             // Call the existing method with the DTO
//             handlePaymentNotification(notification);
//         } catch (Exception e) {
//             logger.severe("Failed to process payment notification: " + e.getMessage());
//             throw new RuntimeException("Failed to process payment notification: " + e.getMessage(), e);
//         }
//     }

//     private String getFormValue(Map<String, String> data, String key) {
//         // Try exact match first
//         String value = data.get(key);
//         if (value != null && !value.trim().isEmpty()) {
//             return value.trim();
//         }
        
//         // Try case-insensitive match for common PayHere field name variations
//         for (Map.Entry<String, String> entry : data.entrySet()) {
//             if (entry.getKey().equalsIgnoreCase(key)) {
//                 return entry.getValue() != null ? entry.getValue().trim() : "";
//             }
//         }
        
//         // Check for common PayHere field name variations
//         if ("md5sig".equalsIgnoreCase(key)) {
//             value = data.get("md5Sig");
//             if (value == null) value = data.get("hash");
//         } else if ("merchant_id".equalsIgnoreCase(key)) {
//             value = data.get("merchantId");
//         } else if ("order_id".equalsIgnoreCase(key)) {
//             value = data.get("orderId");
//         } else if ("status_code".equalsIgnoreCase(key)) {
//             value = data.get("statusCode");
//         }
        
//         return value != null ? value.trim() : "";
//     }

//     private void validateNotificationFields(PaymentNotificationDTO notification) {
//         if (isNullOrEmpty(notification.getMerchant_id())) {
//             throw new RuntimeException("Missing required field: merchant_id");
//         }
//         if (isNullOrEmpty(notification.getOrder_id())) {
//             throw new RuntimeException("Missing required field: order_id");
//         }
//         if (isNullOrEmpty(notification.getPayhere_amount())) {
//             throw new RuntimeException("Missing required field: payhere_amount");
//         }
//         if (isNullOrEmpty(notification.getPayhere_currency())) {
//             throw new RuntimeException("Missing required field: payhere_currency");
//         }
//         if (isNullOrEmpty(notification.getStatus_code())) {
//             throw new RuntimeException("Missing required field: status_code");
//         }
//         if (isNullOrEmpty(notification.getMd5sig())) {
//             throw new RuntimeException("Missing required field: md5sig");
//         }
//     }

//     private boolean isNullOrEmpty(String value) {
//         return value == null || value.trim().isEmpty();
//     }
// }
package com.edu.tutor_platform.payment.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Value;
import jakarta.transaction.Transactional;

import java.math.RoundingMode;
import org.apache.commons.codec.digest.DigestUtils;
import java.math.BigDecimal;
import com.edu.tutor_platform.payment.dto.PaymentRequestDTO;
import com.edu.tutor_platform.payment.entity.Payment;
import com.edu.tutor_platform.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;

@Service
public class PaymentService {

    @PersistenceContext
    private EntityManager entityManager;
 @Value("${payhere.merchant.id}")
    private String merchantId;

    @Value("${payhere.merchant-secret}")
    private String merchantSecret;

        @Autowired
        private PaymentRepository paymentRepository;

        // App timezone for generating business timestamps (defaults to Sri Lanka)
        @Value("${app.timezone:Asia/Colombo}")
        private String appTimeZone;
    @Transactional
        public void completePayment(
            String paymentId,
            Long tutorId,
            Long slotId,
            Long subjectId,
            Long languageId,
            Long classTypeId
    ) {
        entityManager.createNativeQuery(
                "CALL complete_payment(CAST(:paymentId AS uuid), :tutorId, :slotId, :subjectId, :languageId, :classTypeId)")
                .setParameter("paymentId", paymentId)
                .setParameter("tutorId", tutorId)
                .setParameter("slotId", slotId)
                .setParameter("subjectId", subjectId)
                .setParameter("languageId", languageId)
                .setParameter("classTypeId", classTypeId)
                .executeUpdate();
    }
            public String generatePaymentHash(String orderId, BigDecimal amount, String currency) {
    // PayHere spec (checkout v1):
    // md5( merchant_id + order_id + amount(2dp) + currency + md5(merchant_secret) ) -> UPPERCASE
    // Ensure exact 2 decimal places with dot separator
    String formattedAmount = amount == null
        ? "0.00"
        : amount.setScale(2, RoundingMode.HALF_UP).toPlainString();

    String md5Secret = DigestUtils.md5Hex(merchantSecret).toUpperCase();
    String hashInput = merchantId + orderId + formattedAmount + currency + md5Secret;
    return DigestUtils.md5Hex(hashInput).toUpperCase();
    }

        // Backward-compatible overload for callers using PaymentRequestDTO (amount as double)
        public String generatePaymentHash(PaymentRequestDTO request) {
                BigDecimal amt = request.getAmount() == 0
                                ? BigDecimal.ZERO
                                : BigDecimal.valueOf(request.getAmount());
                String curr = request.getCurrency() == null ? "LKR" : request.getCurrency();
                return generatePaymentHash(request.getOrderId(), amt, curr);
        }

        /**
         * Create or update a Payment row as PENDING with a 15-minute expiry.
         * If a payment with the same orderId exists, update it; otherwise create new.
         */
        @Transactional
        public Payment initiatePayment(PaymentRequestDTO request) {
                ZoneId zone = ZoneId.of(appTimeZone);
                LocalDateTime now = LocalDateTime.now(zone);
                LocalDateTime expiresAt = now.plusMinutes(15);

                Optional<Payment> existingOpt = Optional.empty();
                if (request.getOrderId() != null && !request.getOrderId().isBlank()) {
                        existingOpt = paymentRepository.findByOrderId(request.getOrderId());
                }

                Payment payment = existingOpt.orElseGet(Payment::new);

                // Only set createdAt on new records
                if (payment.getPaymentId() == null) {
                        payment.setCreatedAt(now);
                }

                payment.setOrderId(request.getOrderId());
                payment.setStudentId(request.getStudentId());
                if (request.getTutorId() != null) payment.setTutorId(request.getTutorId());
                if (request.getSlotId() != null) payment.setSlotId(request.getSlotId());
                if (request.getAvailabilityId() != null) payment.setAvailabilityId(request.getAvailabilityId());
                if (request.getClassId() != null) payment.setClassId(request.getClassId());
                payment.setAmount(request.getAmount());
                payment.setCurrency(request.getCurrency() == null ? "LKR" : request.getCurrency());
                payment.setStatus("PENDING");
                payment.setExpiresAt(expiresAt);
                payment.setPaymentMethod(request.getPaymentMethod() == null ? "PAYHERE" : request.getPaymentMethod());

                return paymentRepository.save(payment);
        }

        /**
         * Returns true if payment status is PENDING, false otherwise.
         */
        @Transactional
        public boolean isPaymentPending(Long paymentId) {
                if (paymentId == null) return false;
                return false; // numeric lookup deprecated; ids are UUID strings now
        }

        /**
         * Check pending status using UUID identifier exposed to clients.
         */
        @Transactional
        public boolean isPaymentPendingByUuid(String paymentId) {
                if (paymentId == null || paymentId.isBlank()) return false;
                return paymentRepository.findById(paymentId)
                                .map(p -> "PENDING".equalsIgnoreCase(p.getStatus()))
                                .orElse(false);
        }
}

