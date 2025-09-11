package com.edu.tutor_platform.session.service;

import com.edu.tutor_platform.session.dto.ZoomTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ZoomTokenService {

    private final RestTemplate restTemplate;
    private final String nextjsApiUrl;

    public ZoomTokenService(@Value("${nextjs.api.url:http://localhost:3000}") String nextjsApiUrl) {
        this.restTemplate = new RestTemplate();
        this.nextjsApiUrl = nextjsApiUrl;
    }

    public ZoomTokenResponse getAccessToken() {
        String url = nextjsApiUrl + "/api/zoom/token";
        return restTemplate.getForObject(url, ZoomTokenResponse.class);
    }
}