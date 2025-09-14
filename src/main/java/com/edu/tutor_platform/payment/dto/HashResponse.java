package com.edu.tutor_platform.payment.dto;

public class HashResponse {
    private String merchantId;
    private String hash;

    public HashResponse(String merchantId, String hash) {
        this.merchantId = merchantId;
        this.hash = hash;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public String getHash() {
        return hash;
    }
}
