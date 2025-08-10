package com.edu.tutor_platform.user.service;

import com.edu.tutor_platform.user.dto.LoginRequest;
import com.edu.tutor_platform.user.entity.User;
import com.edu.tutor_platform.user.repository.UserRepository;
import com.edu.tutor_platform.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    public String login(LoginRequest loginRequest) {
        // Authenticate credentials
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(), loginRequest.getPassword()));

        // Load user
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());

        // Fetch role from User entity
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate token with role
        return jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getName());
    }
}
