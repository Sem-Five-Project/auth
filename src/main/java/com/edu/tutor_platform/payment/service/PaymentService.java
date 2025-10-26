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
import java.util.List;
import com.edu.tutor_platform.payment.dto.PaymentCompleteDTO;
import com.edu.tutor_platform.payment.dto.PaymentRequestDTO;
import com.edu.tutor_platform.payment.dto.PaymentRepayDTO;
import com.edu.tutor_platform.payment.entity.Payment;
import com.edu.tutor_platform.payment.dto.PayHereRefundResponse;
import com.edu.tutor_platform.payment.dto.PaymentStatusResponse;
import com.edu.tutor_platform.payment.dto.RefundRequestDTO;
import java.util.Map;
import java.util.Optional;
import com.edu.tutor_platform.booking.repository.BookingRepository;
import com.edu.tutor_platform.booking.entity.Booking;
import com.edu.tutor_platform.payment.repository.PaymentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PaymentService {

        private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

        @PersistenceContext
        private EntityManager entityManager;
        @Value("${payhere.merchant.id}")
        private String merchantId;

        @Value("${payhere.merchant-secret}")
        private String merchantSecret;
        // For debugging/dev environments only. Do not enable in production.
        @Value("${payhere.webhook.skip-signature:false}")
        private boolean skipSignature;

        @Autowired
        private PaymentRepository paymentRepository;
        @Autowired
        private PayHereService payHereService;
        @Autowired
        private BookingRepository bookingRepository;
        @Autowired
        private ObjectMapper objectMapper;

        // App timezone for generating business timestamps (defaults to Sri Lanka)
        @Value("${app.timezone:Asia/Colombo}")
        private String appTimeZone;

        @Transactional
        public String completePayment(
                        String paymentId,
                        String slotsJson, // JSON string for slots mapping
                        Long tutorId,
                        Long subjectId,
                        Long languageId,
                        Long classTypeId,
                        Long studentId,
                        java.time.LocalDateTime paymentTime,
                        java.math.BigDecimal amount,
                        Integer month,
                        Integer year,
                        List<Long> nextMonthSlots) {
                // Build a PostgreSQL BIGINT[] literal from list, e.g., {1,2,3}
                String nextMonthArray = (nextMonthSlots == null || nextMonthSlots.isEmpty())
                                ? "{}"
                                : nextMonthSlots.stream().map(String::valueOf)
                                                .collect(java.util.stream.Collectors.joining(",", "{", "}"));

                Object result = entityManager.createNativeQuery(
                                "SELECT complete_paymentt(:paymentId, CAST(:slotsJson AS jsonb), :tutorId, :subjectId, :languageId, :classTypeId, :studentId, :paymentTime, :amount, CAST(:month AS smallint), CAST(:year AS smallint), CAST(:nextMonthSlots AS BIGINT[]))")
                                .setParameter("paymentId", paymentId)
                                .setParameter("slotsJson", slotsJson)
                                .setParameter("tutorId", tutorId)
                                .setParameter("subjectId", subjectId)
                                .setParameter("languageId", languageId)
                                .setParameter("classTypeId", classTypeId)
                                .setParameter("studentId", studentId)
                                .setParameter("paymentTime", paymentTime)
                                .setParameter("amount", amount)
                                .setParameter("month", month)
                                .setParameter("year", year)
                                .setParameter("nextMonthSlots", nextMonthArray)
                                .getSingleResult();
                return result != null ? result.toString() : null;
        }

        public String generatePaymentHash(String orderId, BigDecimal amount, String currency) {
                // PayHere spec (checkout v1):
                // md5( merchant_id + order_id + amount(2dp) + currency + md5(merchant_secret) )
                // -> UPPERCASE
                // Ensure exact 2 decimal places with dot separator
                String formattedAmount = amount == null
                                ? "0.00"
                                : amount.setScale(2, RoundingMode.HALF_UP).toPlainString();

                String md5Secret = DigestUtils.md5Hex(merchantSecret).toUpperCase();
                String hashInput = merchantId + orderId + formattedAmount + currency + md5Secret;
                return DigestUtils.md5Hex(hashInput).toUpperCase();
        }

        // Backward-compatible overload for callers using PaymentRequestDTO (amount as
        // double)
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
                if (request.getTutorId() != null)
                        payment.setTutorId(request.getTutorId());
                if (request.getSlotId() != null)
                        payment.setSlotId(request.getSlotId());
                if (request.getAvailabilityId() != null)
                        payment.setAvailabilityId(request.getAvailabilityId());
                if (request.getClassId() != null)
                        payment.setClassId(request.getClassId());
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
                if (paymentId == null)
                        return false;
                return false; // numeric lookup deprecated; ids are UUID strings now
        }

        /**
         * Check pending status using UUID identifier exposed to clients.
         */
        @Transactional
        public boolean isPaymentPendingByUuid(String paymentId) {
                if (paymentId == null || paymentId.isBlank())
                        return false;
                return paymentRepository.findById(paymentId)
                                .map(p -> "PENDING".equalsIgnoreCase(p.getStatus()))
                                .orElse(false);
        }

        /**
         * Initiate a refund via PayHere and update local records.
         */
        @Transactional
        public java.util.Map<String, Object> refundPayment(RefundRequestDTO request) {
                if (request == null || request.getPaymentId() == null || request.getPaymentId().isBlank()) {
                        throw new IllegalArgumentException("paymentId is required");
                }

                // Manual refunds should respect the 24-hour rule -> shouldRefund=false
                return callProcessRefund(request.getPaymentId(), false);
        }

        /**
         * Execute the DB function process_refund(p_payment_id, p_should_refund) and
         * wrap the JSON result in the expected array shape:
         * [ { "process_refund": { ...json... } } ]
         */
        @Transactional
        protected java.util.Map<String, Object> callProcessRefund(String paymentId, boolean shouldRefund) {
                Object dbResult = entityManager
                                .createNativeQuery("SELECT process_refund(:p_payment_id, :p_should_refund)")
                                .setParameter("p_payment_id", paymentId)
                                .setParameter("p_should_refund", shouldRefund)
                                .getSingleResult();

                String jsonText = dbResult != null ? dbResult.toString() : "{}";
                try {
                        @SuppressWarnings("unchecked")
                        java.util.Map<String, Object> json = objectMapper.readValue(jsonText, java.util.Map.class);
                        java.util.Map<String, Object> wrapper = new java.util.HashMap<>();
                        wrapper.put("process_refund", json);
                        return wrapper;
                } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                        // If parsing fails, still return raw text in the expected envelope
                        java.util.Map<String, Object> wrapper = new java.util.HashMap<>();
                        wrapper.put("process_refund", java.util.Map.of(
                                        "success", false,
                                        "message", "Invalid JSON from process_refund",
                                        "status", -1,
                                        "raw", jsonText));
                        return wrapper;
                }
        }

        /**
         * Handles the incoming webhook notification from PayHere.
         * Verifies the payment signature and updates payment and booking statuses.
         */
        @Transactional
        public void handleNotify(Map<String, String> payload) {
                // 1. Verify the signature to ensure the request is from PayHere (unless
                // explicitly skipped)
                boolean sigOk = skipSignature || verifySignature(payload);
                if (!sigOk) {
                        // Log enough to debug without leaking secrets
                        log.warn("PayHere signature verification FAILED for order_id={}, status_code={}, amount={}, currency={}",
                                        payload.get("order_id"), payload.get("status_code"),
                                        payload.get("payhere_amount"), payload.get("payhere_currency"));
                        // Important: Controller catches exceptions and returns 200 to stop PayHere
                        // retries.
                        // The exception here explains why DB wasn't updated.
                        throw new SecurityException("Invalid PayHere signature on notification.");
                }

                String statusCode = payload.get("status_code");
                String orderId = payload.get("order_id");

                // Entry diagnostics to confirm webhook payload and custom_2 presence
                try {
                        boolean hasC2 = payload.containsKey("custom_2") || payload.containsKey("custom2");
                        String c2raw = payload.get("custom_2");
                        Integer c2len = c2raw != null ? c2raw.length() : null;
                        String sigSrc = firstNonNull(payload.get("md5sig"), payload.get("md5Sig"), payload.get("hash"));
                        log.info("PayHere notify entry: orderId={}, statusCode={}, hasCustom2={}, custom2Len={}, keys={}",
                                        orderId, statusCode, hasC2, c2len, payload.keySet());
                        if (sigSrc == null && !skipSignature) {
                                log.warn("PayHere notify missing signature fields (md5sig/md5Sig/hash) and skipSignature=false");
                        }
                } catch (Exception ignore) {
                }

                // 2. Check for custom_2 repay flow first; this is a next-month payment that
                // should not run the normal completion flow
                String rawRepay = firstNonNull(payload.get("custom_2"), payload.get("custom2"));
                if (rawRepay != null) {
                        log.info("custom_2 present, length={} preview={}", rawRepay.length(),
                                        rawRepay.length() > 120 ? rawRepay.substring(0, 117) + "..." : rawRepay);
                }
                if (rawRepay != null && !rawRepay.isBlank()) {
                        try {
                                PaymentRepayDTO repay = objectMapper.readValue(rawRepay, PaymentRepayDTO.class);
                                if (repay != null && "repay".equalsIgnoreCase(repay.getType())) {
                                        log.info("Handling custom_2 repay for classId={}, studentId={}, paymentId={}",
                                                        repay.getClassId(), repay.getStudentId(), repay.getPaymentId());
                                        String outcome = executeRepay(repay);
                                        log.info("repay_and_reassign_class outcome={} for paymentId={} orderId={}",
                                                        outcome, repay.getPaymentId(), payload.get("order_id"));

                                        // Update the associated payment record by order_id or fallback to payment_id
                                        // (UUID)
                                        boolean updated = false;
                                        String orderIdForLog = payload.get("order_id");
                                        if (orderIdForLog != null) {
                                                Optional<Payment> byOrder = paymentRepository
                                                                .findByOrderId(orderIdForLog);
                                                if (byOrder.isPresent()) {
                                                        Payment p = byOrder.get();
                                                        if ("SUCCESS".equalsIgnoreCase(outcome)) {
                                                                p.setStatus("SUCCESS");
                                                        } else if ("REFUND".equalsIgnoreCase(outcome)) {
                                                                p.setStatus("REFUNDED");
                                                        }
                                                        applyPayHereDetails(p, payload);
                                                        p.setCompletedAt(LocalDateTime.now(ZoneId.of(appTimeZone)));
                                                        paymentRepository.saveAndFlush(p);
                                                        updated = true;
                                                }
                                        }
                                        if (!updated && repay.getPaymentId() != null) {
                                                paymentRepository.findById(repay.getPaymentId()).ifPresent(p -> {
                                                        if ("SUCCESS".equalsIgnoreCase(outcome)) {
                                                                p.setStatus("SUCCESS");
                                                        } else if ("REFUND".equalsIgnoreCase(outcome)) {
                                                                p.setStatus("REFUNDED");
                                                        }
                                                        applyPayHereDetails(p, payload);
                                                        p.setCompletedAt(LocalDateTime.now(ZoneId.of(appTimeZone)));
                                                        paymentRepository.saveAndFlush(p);
                                                });
                                        }
                                        // For repay flow, we stop here regardless of status_code; DB decided
                                        // SUCCESS/REFUND.
                                        return;
                                }
                        } catch (Exception ex) {
                                log.error("Failed to parse/handle custom_2 repay payload: {}", shorten(rawRepay), ex);
                                // fall through to normal logic
                        }
                }

                // 3. Find the payment record using the order_id
                Payment payment = paymentRepository.findByOrderId(orderId)
                                .orElseThrow(() -> new RuntimeException("Payment not found for order_id: " + orderId));

                // Log incoming notification details
                log.info("PayHere notify received: orderId={}, status={}, paymentId={}", orderId, statusCode,
                                payload.get("payment_id"));

                if ("2".equals(statusCode)) { // Status code "2" means a successful payment
                        handleSuccessfulNotification(payload, payment);
                } else {
                        handleFailedNotification(payment, orderId, statusCode);
                }
        }

        /**
         * Verifies the md5sig from PayHere to authenticate the notification.
         * Formula: md5(merchant_id + order_id + amount + currency + status_code +
         * md5(merchant_secret))
         */
        private boolean verifySignature(Map<String, String> payload) {
                String merchantId = payload.get("merchant_id");
                String orderId = payload.get("order_id");
                String amount = payload.get("payhere_amount");
                String currency = payload.get("payhere_currency");
                String statusCode = payload.get("status_code");
                String receivedSignature = firstNonNull(payload.get("md5sig"), payload.get("md5Sig"),
                                payload.get("hash"));

                if (merchantId == null || orderId == null || amount == null || currency == null || statusCode == null
                                || receivedSignature == null) {
                        return false; // Missing essential fields for verification
                }

                // IMPORTANT: Use the correct merchant secret. We try raw and Base64-decoded
                // forms to aid misconfiguration.
                String rawSecret = normalize(merchantSecret);
                if (rawSecret == null)
                        rawSecret = "";

                String md5SecretRaw = DigestUtils.md5Hex(rawSecret).toUpperCase();
                String inputRaw = merchantId + orderId + amount + currency + statusCode + md5SecretRaw;
                String calcRaw = DigestUtils.md5Hex(inputRaw).toUpperCase();

                if (receivedSignature.equals(calcRaw)) {
                        return true;
                }

                // Fallback: If merchantSecret looks base64-encoded, try decoding once.
                try {
                        if (looksBase64(rawSecret)) {
                                String decoded = new String(java.util.Base64.getDecoder().decode(rawSecret),
                                                java.nio.charset.StandardCharsets.UTF_8).trim();
                                String md5SecretDecoded = DigestUtils.md5Hex(decoded).toUpperCase();
                                String inputDecoded = merchantId + orderId + amount + currency + statusCode
                                                + md5SecretDecoded;
                                String calcDecoded = DigestUtils.md5Hex(inputDecoded).toUpperCase();
                                if (receivedSignature.equals(calcDecoded)) {
                                        log.warn("PayHere signature matched only after Base64-decoding merchant secret. Please fix configuration: set payhere.merchant-secret to the RAW secret string.");
                                        return true;
                                }
                        }
                } catch (IllegalArgumentException ignore) {
                        // Not valid base64; ignore
                }

                // Log a short diff for troubleshooting (do not log full secrets)
                log.debug("Signature mismatch: received={} calcRaw={} (first6)",
                                safePrefix(receivedSignature), safePrefix(calcRaw));
                return false;
        }

        private String safePrefix(String s) {
                if (s == null)
                        return "null";
                return s.length() <= 6 ? s : s.substring(0, 6) + "...";
        }

        private boolean looksBase64(String s) {
                if (s == null || s.isEmpty())
                        return false;
                // Heuristic: base64 charset and often ends with '=' padding
                boolean charsetOk = s.matches("[A-Za-z0-9+/=]+");
                boolean hasPadding = s.endsWith("=") || s.endsWith("==");
                return charsetOk && hasPadding;
        }

        private String normalize(String value) {
                if (value == null)
                        return null;
                String trimmed = value.trim();
                return trimmed.isEmpty() ? null : trimmed;
        }

        private void handleSuccessfulNotification(Map<String, String> payload, Payment payment) {
                String orderId = payment.getOrderId();

                if ("SUCCESS".equalsIgnoreCase(normalize(payment.getStatus()))) {
                        log.info("PayHere notify skipped: payment {} already marked SUCCESS", payment.getPaymentId());
                        return;
                }

                PaymentCompleteDTO confirmDto = extractConfirmPayload(payload)
                                .orElseThrow(() -> new IllegalStateException(
                                                "Missing confirmation payload (custom_1) for order " + orderId));

                String completionStatus = executeCompletion(orderId, payment, confirmDto);

                if ("BOOKED".equalsIgnoreCase(normalize(completionStatus))) {
                        finalizeSuccessfulPayment(payment, payload);
                } else {
                        String reason = completionStatus == null
                                        ? "complete_payment returned null"
                                        : "Slot validation failed (status=" + completionStatus + ")";
                        log.warn("complete_payment reported {} for order {}", completionStatus, orderId);
                        triggerAutomaticRefund(payment, payload, reason);
                }
        }

        private void handleFailedNotification(Payment payment, String orderId, String statusCode) {
                payment.setStatus("FAILED");
                paymentRepository.saveAndFlush(payment);
                updateBooking(orderId, "CANCELLED", false);
                log.warn("PayHere reported failure for order {} with status {}", orderId, statusCode);
        }

        private Optional<PaymentCompleteDTO> extractConfirmPayload(Map<String, String> payload) {
                String raw = firstNonNull(payload.get("custom_1"), payload.get("custom1"));
                if (raw == null || raw.isBlank()) {
                        log.warn("PayHere custom_1 payload missing");
                        return Optional.empty();
                }
                try {
                        return Optional.of(objectMapper.readValue(raw, PaymentCompleteDTO.class));
                } catch (JsonProcessingException e) {
                        log.error("Failed to parse custom_1 confirmation payload: {}", shorten(raw), e);
                        return Optional.empty();
                }
        }

        private String firstNonNull(String... values) {
                if (values == null)
                        return null;
                for (String v : values) {
                        if (v != null && !v.isBlank()) {
                                return v;
                        }
                }
                return null;
        }

        private String shorten(String raw) {
                if (raw == null)
                        return "null";
                return raw.length() <= 120 ? raw : raw.substring(0, 117) + "...";
        }

        /**
         * Execute repay SQL function for next-month payments.
         * It returns 'SUCCESS' or 'REFUND'.
         */
        @Transactional
        protected String executeRepay(PaymentRepayDTO repay) {
                try {
                        if (repay.getSlots() == null) {
                                repay.setSlots(java.util.Collections.emptyMap());
                        }
                        String slotsJson = objectMapper.writeValueAsString(repay.getSlots());

                        // Prepare BIGINT[] literal for next month slots
                        List<Long> nms = repay.getNextMonthSlots();
                        String nextMonthArray = (nms == null || nms.isEmpty())
                                        ? "{}"
                                        : nms.stream().map(String::valueOf)
                                                        .collect(java.util.stream.Collectors.joining(",", "{", "}"));

                        // Parse timestamp, allow null
                        java.time.LocalDateTime payTime = null;
                        if (repay.getPaymentTime() != null && !repay.getPaymentTime().isBlank()) {
                                // Accept ISO with Z; parse as Instant then to LocalDateTime in app zone
                                java.time.OffsetDateTime odt = java.time.OffsetDateTime.parse(repay.getPaymentTime());
                                payTime = odt.atZoneSameInstant(ZoneId.of(appTimeZone)).toLocalDateTime();
                        }

                        // Month/year as short for repository method signature
                        Short mm = repay.getMonth() == null ? null : repay.getMonth().shortValue();
                        Short yy = repay.getYear() == null ? null : repay.getYear().shortValue();

                        String outcome = paymentRepository.callRepayAndReassignClass(
                                        repay.getPaymentId(),
                                        repay.getClassId(),
                                        repay.getStudentId(),
                                        payTime,
                                        repay.getAmount(),
                                        mm,
                                        yy,
                                        slotsJson,
                                        nextMonthArray);
                        System.err.println("repay_and_reassign_class outcome: " + outcome);

                        return outcome;
                } catch (Exception e) {
                        log.error("repay_and_reassign_class execution failed", e);
                        return null;
                }
        }

        private String executeCompletion(String orderId, Payment payment, PaymentCompleteDTO dto) {
                try {
                        if (dto.getSlots() == null || dto.getSlots().isEmpty()) {
                                log.warn("Confirmation payload for order {} missing slots; cannot finalize booking",
                                                orderId);
                                return null;
                        }

                        String slotsJson = objectMapper.writeValueAsString(dto.getSlots());
                        String paymentIdParam = dto.getPaymentId() != null ? dto.getPaymentId()
                                        : payment.getPaymentId();
                        if (paymentIdParam == null) {
                                log.warn("No paymentId present in confirmation payload or record for order {}",
                                                orderId);
                                return null;
                        }

                        String result = completePayment(
                                        paymentIdParam,
                                        slotsJson,
                                        coalesce(dto.getTutorId(), payment.getTutorId()),
                                        dto.getSubjectId(),
                                        dto.getLanguageId(),
                                        dto.getClassTypeId(),
                                        coalesce(dto.getStudentId(), payment.getStudentId()),
                                        dto.getPaymentTime(),
                                        dto.getAmount(),
                                        dto.getMonth(),
                                        dto.getYear(),
                                        dto.getNextMonthSlots());
                        log.info("complete_payment executed for order {} -> {}", orderId, result);
                        return result;
                } catch (Exception ex) {
                        log.error("Error executing complete_payment for order {}", orderId, ex);
                        return null;
                }
        }

        private void finalizeSuccessfulPayment(Payment payment, Map<String, String> payload) {
                applyPayHereDetails(payment, payload);
                payment.setStatus("SUCCESS");
                payment.setCompletedAt(LocalDateTime.now(ZoneId.of(appTimeZone)));
                paymentRepository.saveAndFlush(payment);
                updateBooking(payment.getOrderId(), "CONFIRMED", true);
                log.info("Payment {} marked SUCCESS after complete_payment", payment.getPaymentId());
        }

        private void triggerAutomaticRefund(Payment payment, Map<String, String> payload, String reason) {
                // First, run DB-side refund with should_refund = true (instant refund for slot
                // conflicts)
                try {
                        java.util.Map<String, Object> dbOutcome = callProcessRefund(payment.getPaymentId(), true);
                        log.info("process_refund outcome for payment {} => {}", payment.getPaymentId(), dbOutcome);
                } catch (Exception e) {
                        log.error("process_refund failed for payment {}", payment.getPaymentId(), e);
                }

                // Then, attempt PayHere refund to actually reverse the charge
                applyPayHereDetails(payment, payload);
                String payHerePaymentId = payment.getPayherePaymentId();
                if (payHerePaymentId == null) {
                        log.warn("Skipping gateway refund: missing PayHere payment id for payment {}",
                                        payment.getPaymentId());
                        return;
                }
                try {
                        PayHereRefundResponse resp = payHereService.refund(payHerePaymentId,
                                        reason != null ? reason : "Automatic refund due to slot conflict");
                        log.info("PayHere refund attempted for payment {} -> status {}", payment.getPaymentId(),
                                        resp.getStatus());
                } catch (Exception ex) {
                        log.error("Failed to call PayHere refund API for payment {}", payment.getPaymentId(), ex);
                }
        }

        private void applyPayHereDetails(Payment payment, Map<String, String> payload) {
                String payHereId = normalize(payload.get("payment_id"));
                if (payHereId != null) {
                        payment.setPayherePaymentId(payHereId);
                }

                String method = normalize(firstNonNull(payload.get("payment_method"), payload.get("method")));
                if (method == null) {
                        method = payment.getPaymentMethod() != null ? payment.getPaymentMethod() : "PAYHERE";
                }
                payment.setPaymentMethod(method);
                payment.setCardHolderName(normalize(payload.get("card_holder_name")));
        }

        private void updateBooking(String orderId, String bookingStatus, Boolean confirmed) {
                if (orderId == null)
                        return;
                bookingRepository.findByOrderId(orderId).ifPresent(booking -> {
                        if (bookingStatus != null)
                                booking.setBookingStatus(bookingStatus);
                        if (confirmed != null)
                                booking.setIsConfirmed(confirmed);
                        bookingRepository.save(booking);
                });
        }

        private <T> T coalesce(T primary, T fallback) {
                return primary != null ? primary : fallback;
        }

        @Transactional
        public PaymentStatusResponse getPaymentStatusByOrderId(String orderId) {
                if (orderId == null || orderId.isBlank()) {
                        throw new IllegalArgumentException("orderId is required");
                }

                Payment payment = paymentRepository.findByOrderId(orderId)
                                .orElseThrow(() -> new RuntimeException("Payment not found for order_id: " + orderId));

                String bookingStatus = bookingRepository.findByOrderId(orderId)
                                .map(Booking::getBookingStatus)
                                .orElse(null);

                return new PaymentStatusResponse(orderId,
                                payment.getStatus(),
                                payment.getPayherePaymentId(),
                                bookingStatus);
        }

        /**
         * Returns monthly earnings for the given tutor (Jan-Dec) and the total amount
         * so far.
         * Only payments with status 'SUCCESS' and a non-null completedAt are
         * considered.
         */
        @Transactional
        public com.edu.tutor_platform.payment.dto.TutorEarningsAnalyticsDTO getTutorEarningsAnalytics(Long tutorId) {
                if (tutorId == null) {
                        throw new IllegalArgumentException("tutorId is required");
                }

                // Initialize months map 1..12 to 0.0
                double[] months = new double[12];

                List<Object[]> rows = paymentRepository.sumAmountGroupedByMonth(tutorId);
                if (rows != null) {
                        for (Object[] r : rows) {
                                if (r == null || r.length < 2)
                                        continue;
                                Number monthNum = (Number) r[0];
                                Number total = (Number) r[1];
                                if (monthNum == null)
                                        continue;
                                int m = monthNum.intValue(); // 1..12
                                if (m >= 1 && m <= 12) {
                                        months[m - 1] = total != null ? total.doubleValue() : 0.0;
                                }
                        }
                }

                String[] names = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
                java.util.List<com.edu.tutor_platform.payment.dto.MonthlyPoint> monthly = new java.util.ArrayList<>();
                double totalAll = 0.0;
                for (int i = 0; i < 12; i++) {
                        double v = months[i];
                        totalAll += v;
                        monthly.add(new com.edu.tutor_platform.payment.dto.MonthlyPoint(names[i], v));
                }

                return new com.edu.tutor_platform.payment.dto.TutorEarningsAnalyticsDTO(monthly, totalAll);
        }

        /**
         * Returns monthly session counts for the given tutor (Jan-Dec) as an array of
         * 12 integers.
         * Only sessions with status 'COMPLETED' are counted.
         */
        @Transactional
        public List<Integer> getTutorSessionCountsByMonth(Long tutorId) {
                if (tutorId == null) {
                        throw new IllegalArgumentException("tutorId is required");
                }

                // Query to count completed sessions grouped by month
                String query = """
                                SELECT EXTRACT(MONTH FROM s.start_time) as month, COUNT(s.session_id) as count
                                FROM session s
                                JOIN class c ON s.class_id = c.class_id
                                WHERE c.tutor_id = :tutorId
                                  AND s.status = 'COMPLETED'
                                  AND EXTRACT(YEAR FROM s.start_time) = EXTRACT(YEAR FROM CURRENT_DATE)
                                GROUP BY EXTRACT(MONTH FROM s.start_time)
                                ORDER BY month
                                """;

                @SuppressWarnings("unchecked")
                List<Object[]> results = entityManager.createNativeQuery(query)
                                .setParameter("tutorId", tutorId)
                                .getResultList();

                // Initialize array with 12 zeros (Jan to Dec)
                Integer[] monthlyCounts = new Integer[12];
                for (int i = 0; i < 12; i++) {
                        monthlyCounts[i] = 0;
                }

                // Fill in the actual counts
                for (Object[] row : results) {
                        Number monthNum = (Number) row[0];
                        Number countNum = (Number) row[1];
                        if (monthNum != null && countNum != null) {
                                int month = monthNum.intValue(); // 1-12
                                if (month >= 1 && month <= 12) {
                                        monthlyCounts[month - 1] = countNum.intValue();
                                }
                        }
                }

                return java.util.Arrays.asList(monthlyCounts);
        }
}
