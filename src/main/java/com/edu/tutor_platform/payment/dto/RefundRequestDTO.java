package com.edu.tutor_platform.payment.dto;

import lombok.Data;

@Data
public class RefundRequestDTO {
    private String paymentId;
    private Long bookingId;
}
