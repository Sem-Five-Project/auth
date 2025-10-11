package com.edu.tutor_platform.payment.dto;

import lombok.Data;

@Data
public class PayHereRefundResponse {
    private int status;
    private String msg;
    private String data; // This can be the refund reference
}
