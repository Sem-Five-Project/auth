// package com.edu.tutor_platform.payment.Service;

// import com.edu.tutor_platform.payment.Entity.Payment;
// import com.edu.tutor_platform.payment.Repository.PaymentRepository;
// import com.edu.tutor_platform.studentprofile.entity.StudentProfile;
// import com.edu.tutor_platform.tutorprofile.entity.TutorProfile;
// import org.apache.commons.codec.digest.DigestUtils;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.math.BigDecimal;
// import java.util.HashMap;
// import java.util.Map;
// import java.util.Optional;
// import java.util.UUID;

// @Service
// public class PaymentService {

//     private final PaymentRepository paymentRepository;
//     private final String merchantId;
//     private final String merchantSecret;
//     private final String notifyUrl;
//     private final String returnUrl;
//     private final String cancelUrl;

//     public PaymentService(PaymentRepository paymentRepository,
//                           @Value("${payhere.merchant.id}") String merchantId,
//                           @Value("${payhere.merchant.secret}") String merchantSecret,
//                           @Value("${payhere.notify.url}") String notifyUrl,
//                           @Value("${payhere.return.url}") String returnUrl,
//                           @Value("${payhere.cancel.url}") String cancelUrl) {
//         this.paymentRepository = paymentRepository;
//         this.merchantId = merchantId;
//         this.merchantSecret = merchantSecret;
//         this.notifyUrl = notifyUrl;
//         this.returnUrl = returnUrl;
//         this.cancelUrl = cancelUrl;
//     }

//     @Transactional
//     public Map<String, Object> initiatePayment(Long studentId, Long teacherId, BigDecimal amount, String currency) {
//         String orderId = UUID.randomUUID().toString();
//         Payment payment = new Payment();
//         payment.setOrderId(orderId);
        
//         // Create StudentProfile with just the ID - you may need to fetch from repository in real implementation
//         StudentProfile student = new StudentProfile();
//         student.setStudentId(studentId);
//         payment.setStudent(student);
        
//         // Create TutorProfile with just the ID - you may need to fetch from repository in real implementation
//         TutorProfile teacher = new TutorProfile();
//         teacher.setTutorId(teacherId);
//         payment.setTeacher(teacher);
        
//         payment.setAmount(amount);
//         payment.setCurrency(currency);
//         payment.setStatus("PENDING");
//         paymentRepository.save(payment);

//         String hash = generateHash(orderId, amount, currency);

//         Map<String, Object> paymentParams = new HashMap<>();
//         paymentParams.put("merchant_id", merchantId);
//         paymentParams.put("order_id", orderId);
//         paymentParams.put("amount", amount);
//         paymentParams.put("currency", currency);
//         paymentParams.put("hash", hash);
//         paymentParams.put("notify_url", notifyUrl);
//         paymentParams.put("return_url", returnUrl);
//         paymentParams.put("cancel_url", cancelUrl);
//         paymentParams.put("custom_1", studentId.toString());
//         paymentParams.put("custom_2", teacherId.toString());

//         return paymentParams;
//     }

//     @Transactional
//     public void handlePaymentCallback(Map<String, String> callbackParams) {
//         String orderId = callbackParams.get("order_id");
//         String statusCode = callbackParams.get("status_code");
//         String md5sig = callbackParams.get("md5sig");

//         Optional<Payment> paymentOpt = paymentRepository.findByOrderId(orderId);
//         if (!paymentOpt.isPresent()) {
//             throw new IllegalStateException("Payment not found for order ID: " + orderId);
//         }

//         Payment payment = paymentOpt.get();
//         String localHash = generateHash(orderId, payment.getAmount(), payment.getCurrency());
//         // Note: Actual hash verification should include additional parameters as per PayHere docs
//         if (!verifyHash(md5sig, localHash)) {
//             throw new SecurityException("Invalid hash in callback");
//         }

//         if ("2".equals(statusCode)) { // 2 indicates successful payment
//             payment.setStatus("COMPLETED");
//             // Update teacher's account (e.g., add to balance or record payment)
//             TutorProfile teacher = payment.getTeacher();
//             // Example: teacher.setBalance(teacher.getBalance().add(payment.getAmount()));
//             // Save teacher if balance is updated
//             paymentRepository.save(payment);
//         } else {
//             payment.setStatus("FAILED");
//             paymentRepository.save(payment);
//         }
//     }

//     private String generateHash(String orderId, BigDecimal amount, String currency) {
//         String hashInput = merchantId + orderId + amount.toString() + currency + merchantSecret;
//         return DigestUtils.md5Hex(hashInput).toUpperCase();
//     }

//     private boolean verifyHash(String receivedHash, String localHash) {
//         // Simplified; actual implementation may require additional parameters
//         return receivedHash.equals(localHash);
//     }
// }

package com.edu.tutor_platform.payment.Service;

import com.edu.tutor_platform.payment.Entity.Payment;
import com.edu.tutor_platform.payment.Repository.PaymentRepository;
import com.edu.tutor_platform.session.entity.SessionStudent;
import com.edu.tutor_platform.session.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.digest.DigestUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService {

    @Value("${payhere.merchant.id}")
    private String merchantId;

    @Value("${payhere.merchant.secret}")
    private String merchantSecret;

    @Value("${payhere.notify.url}")
    private String notifyUrl;

    @Value("${payhere.return.url}")
    private String returnUrl;

    @Value("${payhere.cancel.url}")
    private String cancelUrl;

    private final PaymentRepository paymentRepository;
    private final SessionRepository sessionRepository;

    public PaymentService(PaymentRepository paymentRepository, SessionRepository sessionRepository) {
        this.paymentRepository = paymentRepository;
        this.sessionRepository = sessionRepository;
    }

    public Map<String, Object> initiatePayment(Long studentId, Long sessionId, BigDecimal amount, String currency,
                                               String paymentMethod, String paymentType, String firstName, String lastName,
                                               String email, String phone, String address, String city, String country) {
        // Validate student and session
        if (!paymentRepository.existsByStudentId(studentId)) {
            throw new IllegalArgumentException("Student not found");
        }
        if (!sessionRepository.existsById(sessionId)) {
            throw new IllegalArgumentException("Session not found");
        }

        UUID orderIdUUID = UUID.randomUUID();
        String orderIdString = orderIdUUID.toString();
        
        // Format amount to 2 decimal places for hash generation
        String formattedAmount = String.format("%.2f", amount);
        String hash = generatePayHereHash(merchantId, orderIdString, formattedAmount, currency);

        // Save payment
        Payment payment = new Payment();
        payment.setStudentId(studentId);
        payment.setSessionId(sessionId);
        payment.setAmount(amount);
        payment.setCurrency(currency);
        payment.setOrderId(orderIdUUID);
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentType(paymentType);
        payment.setPaymentStatus("PENDING");
        paymentRepository.save(payment);

        // Link payment to session_student
        SessionStudent sessionStudent = new SessionStudent();
        sessionStudent.setSessionId(sessionId);
        sessionStudent.setStudentId(studentId);
        sessionStudent.setPaymentId(payment.getPaymentId());
        sessionRepository.saveSessionStudent(sessionStudent);

        // Update student_count
        sessionRepository.incrementStudentCount(sessionId);

        // Prepare PayHere parameters (all required fields)
        Map<String, Object> params = new HashMap<>();
        params.put("sandbox", true); // Set to false for production
        params.put("merchant_id", merchantId);
        params.put("return_url", returnUrl);
        params.put("cancel_url", cancelUrl);
        params.put("notify_url", notifyUrl);
        params.put("order_id", orderIdString);
        params.put("items", "Tutoring Session Payment");
        params.put("currency", currency);
        params.put("amount", formattedAmount);
        params.put("hash", hash);
        
        // Customer details (required)
        params.put("first_name", firstName);
        params.put("last_name", lastName);
        params.put("email", email);
        params.put("phone", phone);
        params.put("address", address);
        params.put("city", city);
        params.put("country", country);
        
        // Custom parameters
        params.put("custom_1", String.valueOf(studentId));
        params.put("custom_2", String.valueOf(sessionId));
        
        return params;
    }

    public void handlePaymentCallback(Map<String, String> callbackParams) {
        String orderIdString = callbackParams.get("order_id");
        UUID orderIdUUID = UUID.fromString(orderIdString);
        String statusCode = callbackParams.get("status_code");
        String md5sig = callbackParams.get("md5sig");
        String payHereAmount = callbackParams.get("payhere_amount");
        String payHereCurrency = callbackParams.get("payhere_currency");
        String merchantId = callbackParams.get("merchant_id");

        Payment payment = paymentRepository.findByOrderId(orderIdUUID)
            .orElseThrow(() -> new IllegalArgumentException("Payment not found for order: " + orderIdString));

        // Verify hash according to PayHere documentation
        String expectedHash = generateCallbackHash(merchantId, orderIdString, payHereAmount, payHereCurrency, statusCode);
        
        if (!md5sig.equalsIgnoreCase(expectedHash)) {
            throw new SecurityException("Invalid hash signature. Payment verification failed.");
        }

        // Update payment status based on status code
        switch (statusCode) {
            case "2": // Success
                payment.setPaymentStatus("COMPLETED");
                paymentRepository.save(payment);
                
                // Update tutor balance
                Long tutorId = sessionRepository.findTutorIdBySessionId(payment.getSessionId());
                paymentRepository.updateTutorBalance(tutorId, payment.getAmount());
                break;
                
            case "0": // Pending
                payment.setPaymentStatus("PENDING");
                paymentRepository.save(payment);
                break;
                
            case "-1": // Cancelled
                payment.setPaymentStatus("CANCELLED");
                paymentRepository.save(payment);
                break;
                
            case "-2": // Failed
                payment.setPaymentStatus("FAILED");
                paymentRepository.save(payment);
                break;
                
            case "-3": // Chargedback
                payment.setPaymentStatus("CHARGEDBACK");
                paymentRepository.save(payment);
                break;
                
            default:
                payment.setPaymentStatus("UNKNOWN");
                paymentRepository.save(payment);
        }
    }

    // Hash generation for payment initiation (PayHere format)
    private String generatePayHereHash(String merchantId, String orderId, String amount, String currency) {
        String merchantSecretHash = DigestUtils.md5Hex(merchantSecret).toUpperCase();
        String hashInput = merchantId + orderId + amount + currency + merchantSecretHash;
        return DigestUtils.md5Hex(hashInput).toUpperCase();
    }
    
    // Hash generation for callback verification (PayHere format)
    private String generateCallbackHash(String merchantId, String orderId, String amount, String currency, String statusCode) {
        String merchantSecretHash = DigestUtils.md5Hex(merchantSecret).toUpperCase();
        String hashInput = merchantId + orderId + amount + currency + statusCode + merchantSecretHash;
        return DigestUtils.md5Hex(hashInput).toUpperCase();
    }
}