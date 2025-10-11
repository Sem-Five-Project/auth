package com.edu.tutor_platform.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PayHereRefundRequest {
    @JsonProperty("payment_id")
    private String paymentId;

    private String description;
}
