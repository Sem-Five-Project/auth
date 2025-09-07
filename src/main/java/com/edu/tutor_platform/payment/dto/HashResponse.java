package com.edu.tutor_platform.payment.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HashResponse {
    private String merchantId;
    private String hash;
}