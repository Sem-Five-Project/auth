package com.edu.tutor_platform.payment.dto;

import lombok.Data;

@Data
public class PaymentRequestDTO {
    private String orderId;
    private double amount;
    private String currency = "LKR";
    private Long classId;
    private Long studentId;
    private Long tutorId;
}