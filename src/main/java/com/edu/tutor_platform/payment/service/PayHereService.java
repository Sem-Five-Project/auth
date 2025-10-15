package com.edu.tutor_platform.payment.service;

import com.edu.tutor_platform.payment.dto.PayHereRefundRequest;
import com.edu.tutor_platform.payment.dto.PayHereRefundResponse;
import com.edu.tutor_platform.payment.dto.PayHereTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

@Service
public class PayHereService {

    @Value("${payhere.app.id}")
    private String appId;

    @Value("${payhere.app.secret}")
    private String appSecret;

    @Value("${payhere.api.token-url}")
    private String tokenUrl;

    @Value("${payhere.api.refund-url}")
    private String refundUrl;

    private final RestTemplate rest = new RestTemplate();

    // Simple in-memory token cache
    private String cachedToken;
    private long tokenExpiryEpochSeconds = 0L;

    public synchronized String getAccessToken() {
        long now = Instant.now().getEpochSecond();
        if (cachedToken != null && now < tokenExpiryEpochSeconds - 30) { // 30s buffer
            return cachedToken;
        }

        String basic = Base64.getEncoder().encodeToString((appId + ":" + appSecret).getBytes(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + basic);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> entity = new HttpEntity<>("grant_type=client_credentials", headers);
        ResponseEntity<PayHereTokenResponse> resp = rest.exchange(tokenUrl, HttpMethod.POST, entity, PayHereTokenResponse.class);
        if (resp.getStatusCode().is2xxSuccessful()) {
            PayHereTokenResponse body = resp.getBody();
            if (body != null) {
                cachedToken = body.getAccessToken();
                tokenExpiryEpochSeconds = now + Math.max(60, body.getExpiresIn());
                return cachedToken;
            }
        }
        throw new RuntimeException("Failed to obtain PayHere access token: " + resp.getStatusCode());
    }

    public PayHereRefundResponse refund(String payherePaymentId, String description) {
        String token = getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        PayHereRefundRequest req = PayHereRefundRequest.builder()
                .paymentId(payherePaymentId)
                .description(description == null ? "Refund requested" : description)
                .build();

        HttpEntity<PayHereRefundRequest> entity = new HttpEntity<>(req, headers);
        ResponseEntity<PayHereRefundResponse> resp = rest.exchange(refundUrl, HttpMethod.POST, entity, PayHereRefundResponse.class);
        if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
            return resp.getBody();
        }
        PayHereRefundResponse fallback = new PayHereRefundResponse();
        fallback.setStatus(0);
        fallback.setMsg("HTTP " + resp.getStatusCode().value());
        return fallback;
    }
}
