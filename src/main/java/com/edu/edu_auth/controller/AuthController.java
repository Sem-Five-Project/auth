// controller/AuthController.java
package com.edu.edu_auth.controller;

import com.edu.edu_auth.dto.RegisterRequest;
import com.edu.edu_auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        userService.register(request);
        return ResponseEntity.ok("User registered successfully!");
    }

    @GetMapping("/hello")
    public String getMethodName() {
        return "Hello " + "World! ";
    }
    
}
