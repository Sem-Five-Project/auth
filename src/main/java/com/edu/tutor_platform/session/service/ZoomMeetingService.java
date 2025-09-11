package com.edu.tutor_platform.session.service;

import com.edu.tutor_platform.session.dto.ZoomMeetingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ZoomMeetingService {

    @Value("${nextjs.api.url:http://localhost:3000}")
    private String nextjsApiUrl;

    private final RestTemplate restTemplate;
    private final ZoomTokenService zoomTokenService;

    @Autowired
    public ZoomMeetingService(RestTemplate restTemplate, ZoomTokenService zoomTokenService) {
        this.restTemplate = restTemplate;
        this.zoomTokenService = zoomTokenService;
    }

    public ZoomMeetingResponse createMeeting() {
        String url = nextjsApiUrl + "/api/zoom/createMeeting";

        // Get access token from ZoomTokenService
        String accessToken = zoomTokenService.getAccessToken().getAccess_token();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        try {
            ResponseEntity<ZoomMeetingResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    ZoomMeetingResponse.class
            );

            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Zoom meeting", e);
        }
    }
}