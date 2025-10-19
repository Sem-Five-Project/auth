package com.edu.tutor_platform.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefreshResponse {
    private final AuthResponse authResponse;
    private final String newRefreshToken;
}
