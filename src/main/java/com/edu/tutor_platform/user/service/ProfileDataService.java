package com.edu.tutor_platform.user.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.edu.tutor_platform.util.JwtUtil;
import com.edu.tutor_platform.user.entity.User;
import com.edu.tutor_platform.user.repository.UserRepository;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileDataService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public User getLoggedInUser(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String email = jwtUtil.extractEmail(token); // Extracted from token
            return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        }
        throw new RuntimeException("Invalid Authorization header");
    }

}
