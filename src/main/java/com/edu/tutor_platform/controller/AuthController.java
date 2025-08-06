package com.authsystem.controller;

import com.authsystem.dto.AuthResponse;
import com.authsystem.dto.LoginRequest;
import com.authsystem.dto.RegisterRequest;
import com.authsystem.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:5173"}, allowCredentials = "true")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            System.out.println("logged endpoint called with username: ");

            AuthResponse authResponse = authService.login(loginRequest);
            // Get refresh token string from refreshTokenService
            String refreshTokenStr = authService.getRefreshTokenForUser(authResponse.getUser().getId());

            // Set refresh token as HTTP-only cookie
            Cookie refreshTokenCookie = new Cookie("refreshToken", refreshTokenStr);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(true); // Set to true in production with HTTPS
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(30 * 24 * 60 * 60); // 30 days
            response.addCookie(refreshTokenCookie);

            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest, HttpServletResponse response) {
        System.out.println("Register endpoint called with username: " + registerRequest.getUsername());

        try {
            AuthResponse authResponse = authService.register(registerRequest);
            System.out.println("Registration successful for username: " + registerRequest.getUsername());
            // Set refresh token as HTTP-only cookie
            Cookie refreshTokenCookie = new Cookie("refreshToken", authResponse.getUser().getId().toString());
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(false); // Set to true in production with HTTPS
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(30 * 24 * 60 * 60); // 30 days
            response.addCookie(refreshTokenCookie);

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
        System.out.println("Refresh endpoint called with username: before try");

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
            // this part added by me
//            if (token.isExpired()) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token expired");
//            }

            AuthResponse authResponse = authService.refreshToken(refreshToken);
            System.out.println("here");
            System.out.println(authResponse);

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

            // Clear refresh token cookie
            Cookie refreshTokenCookie = new Cookie("refreshToken", null);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(false);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(0);
            response.addCookie(refreshTokenCookie);

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
                Map<String, String> error = new HashMap<>();
                error.put("message", "User not authenticated");
                return ResponseEntity.badRequest().body(error);
            }

            // Return current user info
            Map<String, String> userInfo = new HashMap<>();
            userInfo.put("username", authentication.getName());
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}

