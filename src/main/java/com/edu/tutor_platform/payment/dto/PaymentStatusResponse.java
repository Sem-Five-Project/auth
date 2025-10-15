package com.edu.tutor_platform.payment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentStatusResponse {
    private String orderId;
    private String status;  // Value from payment.status
    private String payherePaymentId;
    private String bookingStatus;
}
