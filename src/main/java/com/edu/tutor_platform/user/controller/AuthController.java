package com.edu.tutor_platform.user.controller;

import com.edu.tutor_platform.user.dto.AuthResponse;
import com.edu.tutor_platform.user.dto.AuthResponse.UserInfo;
import com.edu.tutor_platform.user.dto.LoginRequest;
import com.edu.tutor_platform.user.dto.RegisterRequest;
import com.edu.tutor_platform.user.entity.LoginAttempt;
import com.edu.tutor_platform.user.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "https://edimy-front-end.vercel.app", "https://edimy.vercel.app"}, allowCredentials = "true")
public class AuthController {

    private String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
            String ipAddress = getClientIpAddress(request);
            System.out.println("Login endpoint called with username: " + loginRequest.getUsernameOrEmail() + " from IP: " + ipAddress);

            AuthResponse authResponse = authService.login(loginRequest, ipAddress);
            // Get refresh token string from refreshTokenService
            String refreshTokenStr = authService.getRefreshTokenForUser(authResponse.getUser().getId());
            System.out.println("refreshTokenStr: " + refreshTokenStr);

            // Set refresh token as HTTP-only cookie for cross-site support
            ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshTokenStr)
                    .httpOnly(true)
                    .secure(true) // REQUIRED for SameSite=None
                    .path("/")
                    .maxAge(30 * 24 * 60 * 60) // 30 days
                    .sameSite("None") // REQUIRED for cross-site cookies
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest, HttpServletRequest request, HttpServletResponse response) {
        String ipAddress = getClientIpAddress(request);
        System.out.println("Register endpoint called with username: " + registerRequest.getUsername() + " from IP: " + ipAddress);

        try {
            AuthResponse authResponse = authService.register(registerRequest, ipAddress);
            System.out.println("Registration successful for username: " + registerRequest.getUsername());
            // Get refresh token string from refreshTokenService
            String refreshTokenStr = authService.getRefreshTokenForUser(authResponse.getUser().getId());
            // Set refresh token as HTTP-only cookie for cross-site support
            ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshTokenStr)
                    .httpOnly(true)
                    .secure(true) // REQUIRED for SameSite=None
                    .path("/")
                    .maxAge(30 * 24 * 60 * 60) // 30 days
                    .sameSite("None") // REQUIRED for cross-site cookies
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            System.out.println("Registration failed: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String ipAddress = getClientIpAddress(request);
        System.out.println("Refresh endpoint called with username: before try from IP: " + ipAddress);

        try {
            System.out.println("Refresh endpoint called with username: after try");

            String refreshToken = null;
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("refreshToken".equals(cookie.getName())) {
                        refreshToken = cookie.getValue();
                        break;
                    }
                }
            }

            if (refreshToken == null) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Refresh token not found");
                return ResponseEntity.badRequest().body(error);
            }

            AuthResponse authResponse = authService.refreshToken(refreshToken, ipAddress);
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(Authentication authentication, HttpServletResponse response) {
        try {
            if (authentication != null) {
                authService.logout(authentication.getName());
            }

            // Clear refresh token cookie with matching attributes
            ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", "")
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(0) // Delete cookie
                    .sameSite("None")
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

            Map<String, String> response_body = new HashMap<>();
            response_body.put("message", "Logged out successfully");
            return ResponseEntity.ok(response_body);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        try {
            if (authentication == null) {
                return ResponseEntity.status(401).body(Map.of(
                        "success", false,
                        "error", "User not authenticated"
                ));
            }

            com.edu.tutor_platform.user.entity.User user = (com.edu.tutor_platform.user.entity.User) authentication.getPrincipal();
            UserInfo userInfo = authService.buildCurrentUserInfo(user);

            // Mirror AuthResponse style but without issuing a new token
            Map<String, Object> payload = new HashMap<>();
            payload.put("user", userInfo);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", payload
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Boolean>> checkUsernameAvailability(@RequestParam String username) {
        Map<String, Boolean> response = new HashMap<>();
        boolean isAvailable = !authService.isUsernameTaken(username);
        response.put("available", isAvailable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rate-limit-status")
    public ResponseEntity<?> getRateLimitStatus(HttpServletRequest request) {
        String ipAddress = getClientIpAddress(request);
        LoginAttempt loginAttempt = authService.getLoginAttemptStatus(ipAddress);

        Map<String, Object> response = new HashMap<>();
        response.put("ipAddress", ipAddress);
        response.put("attempts", loginAttempt.getAttempts());
        response.put("lastAttempt", loginAttempt.getLastAttempt());
        response.put("blockedUntil", loginAttempt.getBlockedUntil());
        response.put("isBlocked", loginAttempt.isBlocked());
        response.put("isRateLimited", authService.isRateLimited(ipAddress));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/fcmtoken")
    public ResponseEntity<String> storeFcmtoken(@RequestBody String fcmToken, HttpServletRequest request) {
        System.out.println(request);
        authService.storeFcmToken(fcmToken, request);
        return ResponseEntity.ok("FCMtoken stored successfully!");
    }
}
