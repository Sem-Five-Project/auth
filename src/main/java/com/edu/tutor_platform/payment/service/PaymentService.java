package com.edu.tutor_platform.payment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.edu.tutor_platform.payment.dto.PaymentRequestDTO;
import com.edu.tutor_platform.payment.dto.PaymentNotificationDTO;
import com.edu.tutor_platform.payment.entity.Payment;
import com.edu.tutor_platform.payment.repository.PaymentRepository;
import com.edu.tutor_platform.payment.util.HashUtil;
import com.edu.tutor_platform.tutorprofile.entity.TutorProfile;
import com.edu.tutor_platform.tutorprofile.repository.TutorProfileRepository;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class PaymentService {
    
    private static final Logger logger = Logger.getLogger(PaymentService.class.getName());
    
    @Autowired
    private HashUtil hashUtil;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private TutorProfileRepository tutorProfileRepository;  // Using the correct tutor repository

    @Value("${payhere.merchant.id}")
    private String merchantId;

    public String generatePaymentHash(PaymentRequestDTO request) {
        return hashUtil.generateHash(merchantId, request.getOrderId(), request.getAmount(), request.getCurrency());
    }

    public void handlePaymentNotification(PaymentNotificationDTO notification) {
        if (!hashUtil.verifyNotification(notification, merchantId)) {
            throw new RuntimeException("Invalid signature");
        }

        if ("2".equals(notification.getStatus_code())) {  // Success
            // Save payment details
            Payment payment = new Payment();
            payment.setOrderId(notification.getOrder_id());
            payment.setAmount(Double.parseDouble(notification.getPayhere_amount()));
            payment.setCurrency(notification.getPayhere_currency());
            payment.setStatus("SUCCESS");
            
            // Set IDs from custom form data
            try {
                if (notification.getStudent_id() != null && !notification.getStudent_id().isEmpty()) {
                    payment.setStudentId(Long.parseLong(notification.getStudent_id()));
                    logger.info("Set student ID: " + notification.getStudent_id());
                }
                if (notification.getTutor_id() != null && !notification.getTutor_id().isEmpty()) {
                    payment.setTutorId(Long.parseLong(notification.getTutor_id()));
                    logger.info("Set tutor ID: " + notification.getTutor_id());
                }
                if (notification.getClass_id() != null && !notification.getClass_id().isEmpty()) {
                    payment.setClassId(Long.parseLong(notification.getClass_id()));
                    logger.info("Set class ID: " + notification.getClass_id());
                }
            } catch (NumberFormatException e) {
                logger.warning("Invalid ID format in notification: " + e.getMessage());
            }
            
            paymentRepository.save(payment);
            logger.info("Payment saved successfully for Order ID: " + notification.getOrder_id());

            // Update tutor balance (assume TutorProfile entity has double availableBalance)
            // TutorProfile tutor = tutorProfileRepository.findById(/* tutorId from context */).orElseThrow();
            // tutor.setAvailableBalance(tutor.getAvailableBalance() + payment.getAmount());  // Minus fee if any
            // tutorProfileRepository.save(tutor);

            // Optional: Update student/class status as paid
        } else {
            // Handle failure (e.g., save as "FAILED")
        }
    }

    // Overloaded method to handle Map<String, String> from webhook
    public void handlePaymentNotification(Map<String, String> notificationData) {
        try {
            // Log received data for debugging
            logger.info("Received notification data: " + notificationData.toString());
            
            // Convert Map to PaymentNotificationDTO with null checks
            PaymentNotificationDTO notification = new PaymentNotificationDTO();
            notification.setMerchant_id(getFormValue(notificationData, "merchant_id"));
            notification.setOrder_id(getFormValue(notificationData, "order_id"));
            notification.setPayhere_amount(getFormValue(notificationData, "payhere_amount"));
            notification.setPayhere_currency(getFormValue(notificationData, "payhere_currency"));
            notification.setStatus_code(getFormValue(notificationData, "status_code"));
            notification.setMd5sig(getFormValue(notificationData, "md5sig"));
            
            // Extract custom fields
            notification.setStudent_id(getFormValue(notificationData, "student_id"));
            notification.setTutor_id(getFormValue(notificationData, "tutor_id"));
            notification.setClass_id(getFormValue(notificationData, "class_id"));
            notification.setCard_holder_name(getFormValue(notificationData, "card_holder_name"));
            notification.setPayment_method(getFormValue(notificationData, "payment_method"));

            // Validate all required fields are present
            validateNotificationFields(notification);

            // Log notification details
            logger.info("Processing notification for Order ID: " + notification.getOrder_id() +
                       ", Amount: " + notification.getPayhere_amount() +
                       ", Status: " + notification.getStatus_code() +
                       ", Student ID: " + notification.getStudent_id() +
                       ", Tutor ID: " + notification.getTutor_id() +
                       ", Class ID: " + notification.getClass_id());

            // Call the existing method with the DTO
            handlePaymentNotification(notification);
        } catch (Exception e) {
            logger.severe("Failed to process payment notification: " + e.getMessage());
            throw new RuntimeException("Failed to process payment notification: " + e.getMessage(), e);
        }
    }

    private String getFormValue(Map<String, String> data, String key) {
        // Try exact match first
        String value = data.get(key);
        if (value != null && !value.trim().isEmpty()) {
            return value.trim();
        }
        
        // Try case-insensitive match for common PayHere field name variations
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(key)) {
                return entry.getValue() != null ? entry.getValue().trim() : "";
            }
        }
        
        // Check for common PayHere field name variations
        if ("md5sig".equalsIgnoreCase(key)) {
            value = data.get("md5Sig");
            if (value == null) value = data.get("hash");
        } else if ("merchant_id".equalsIgnoreCase(key)) {
            value = data.get("merchantId");
        } else if ("order_id".equalsIgnoreCase(key)) {
            value = data.get("orderId");
        } else if ("status_code".equalsIgnoreCase(key)) {
            value = data.get("statusCode");
        }
        
        return value != null ? value.trim() : "";
    }

    private void validateNotificationFields(PaymentNotificationDTO notification) {
        if (isNullOrEmpty(notification.getMerchant_id())) {
            throw new RuntimeException("Missing required field: merchant_id");
        }
        if (isNullOrEmpty(notification.getOrder_id())) {
            throw new RuntimeException("Missing required field: order_id");
        }
        if (isNullOrEmpty(notification.getPayhere_amount())) {
            throw new RuntimeException("Missing required field: payhere_amount");
        }
        if (isNullOrEmpty(notification.getPayhere_currency())) {
            throw new RuntimeException("Missing required field: payhere_currency");
        }
        if (isNullOrEmpty(notification.getStatus_code())) {
            throw new RuntimeException("Missing required field: status_code");
        }
        if (isNullOrEmpty(notification.getMd5sig())) {
            throw new RuntimeException("Missing required field: md5sig");
        }
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}