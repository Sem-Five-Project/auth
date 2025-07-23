// controller/AuthController.java
package com.edu.edu_auth.controller;

import com.edu.edu_auth.dto.AuthResponse;
import com.edu.edu_auth.dto.LoginRequest;
import com.edu.edu_auth.dto.RegisterRequest;
import com.edu.edu_auth.entity.User;
import com.edu.edu_auth.service.AuthService;
import com.edu.edu_auth.service.UserService;
import com.edu.edu_auth.service.ProfileDataService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final ProfileDataService profileDataService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        userService.register(request);
        return ResponseEntity.ok("User registered successfully!");
    }

    @GetMapping("/hello")
    public String getMethodName() {
        return "Hello " + "World! ";
    }
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        User user = profileDataService.getLoggedInUser(request);
        Map<String, Object> response = new HashMap<>();
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        return ResponseEntity.ok(response);
}
    
}
