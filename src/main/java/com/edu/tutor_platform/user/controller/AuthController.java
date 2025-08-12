// controller/AuthController.java
package com.edu.tutor_platform.user.controller;

import com.edu.tutor_platform.user.dto.AuthResponse;
import com.edu.tutor_platform.user.dto.LoginRequest;
import com.edu.tutor_platform.user.dto.RegisterRequest;
import com.edu.tutor_platform.user.service.AuthService;
import com.edu.tutor_platform.user.service.ProfileDataService;
import com.edu.tutor_platform.user.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final ProfileDataService profileDataService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request){
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

    @PostMapping("/Fcmtoken")
    public ResponseEntity<String> storeFcmtoken(@RequestBody String fcmToken, HttpServletRequest request) {
        System.out.println(request);
        userService.storeFcmToken(fcmToken, request);
        return ResponseEntity.ok("FCMtoken stored successfully!");
    }
//    @GetMapping("/profile")
//    public ResponseEntity<?> getProfile(HttpServletRequest request) {
//        User user = profileDataService.getLoggedInUser(request);
//        Map<String, Object> response = new HashMap<>();
//        response.put("username", user.getname());
//        response.put("email", user.getEmail());
//        return ResponseEntity.ok(response);
//    }

}
