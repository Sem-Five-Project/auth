package com.edu.tutor_platform.payment.dto;

import lombok.Data;

@Data
public class PaymentNotificationDTO {
    // PayHere standard fields
    private String merchant_id;
    private String order_id;
    private String payhere_amount;
    private String payhere_currency;
    private String status_code;  // 2 = success
    private String md5sig;       // For verification
    
    // Custom fields that can be sent from PayHere
    private String student_id;   // Custom field for student identification
    private String tutor_id;     // Custom field for tutor identification
    private String class_id;     // Custom field for class identification
    
    // Additional PayHere fields (optional)
    private String card_holder_name;
    private String payment_method;
}