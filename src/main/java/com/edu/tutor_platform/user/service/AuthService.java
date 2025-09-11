// package com.edu.tutor_platform.user.service;

// import com.edu.tutor_platform.user.dto.LoginRequest;
// import com.edu.tutor_platform.user.entity.User;
// import com.edu.tutor_platform.user.repository.UserRepository;
// import com.edu.tutor_platform.util.JwtUtil;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.stereotype.Service;

// @Service
// public class AuthService {

//     @Autowired
//     private AuthenticationManager authenticationManager;

//     @Autowired
//     private JwtUtil jwtUtil;

//     @Autowired
//     private UserDetailsService userDetailsService;

//     @Autowired
//     private UserRepository userRepository;

//     public String login(LoginRequest loginRequest) {
//         // Authenticate credentials
//         authenticationManager.authenticate(
//                 new UsernamePasswordAuthenticationToken(
//                         loginRequest.getEmail(), loginRequest.getPassword()));

//         // Load user
//         UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());

//         // Fetch role from User entity
//         User user = userRepository.findByEmail(loginRequest.getEmail())
//                 .orElseThrow(() -> new RuntimeException("User not found"));

//         // Generate token with role
//         return jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getName());
//     }
// }
package com.edu.tutor_platform.user.service;

import com.edu.tutor_platform.user.dto.AuthResponse;
import com.edu.tutor_platform.user.dto.LoginRequest;
import com.edu.tutor_platform.user.dto.RegisterRequest;
import com.edu.tutor_platform.user.entity.LoginAttempt;
import com.edu.tutor_platform.user.entity.RefreshToken;
import com.edu.tutor_platform.user.entity.User;
import com.edu.tutor_platform.user.repository.LoginAttemptRepository;
import com.edu.tutor_platform.user.repository.UserRepository;
import com.edu.tutor_platform.user.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private LoginAttemptRepository loginAttemptRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private RefreshTokenService refreshTokenService;
    
    public AuthResponse login(LoginRequest loginRequest, String ipAddress) {
        // Check if IP is blocked
        if (isIpBlocked(ipAddress)) {
            throw new RuntimeException("IP address is temporarily blocked due to too many failed attempts");
        }
        
        // Check if rate limited
        if (isRateLimited(ipAddress)) {
            throw new RuntimeException("Too many failed login attempts. Please try again later.");
        }
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsernameOrEmail(),
                            loginRequest.getPassword()
                    )
            );
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                        System.out.println("userDetails: " + userDetails);

            User user = userRepository.findByUsernameOrEmail(userDetails.getUsername(), userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            String accessToken = jwtUtil.generateAccessToken(userDetails);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
            
            // Register successful attempt
            registerSuccessfulAttempt(ipAddress);
            
            AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                    user.getId(),
                    user.getUsername(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getRole()
            );

            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            return new AuthResponse(accessToken, userInfo);
        } catch (Exception e) {
            // Register failed attempt
            registerFailedAttempt(ipAddress);
            throw e;
        }
    }
    
    public AuthResponse register(RegisterRequest registerRequest, String ipAddress) {
        // Check if IP is blocked
        if (isIpBlocked(ipAddress)) {
            throw new RuntimeException("IP address is temporarily blocked due to too many failed attempts");
        }
        
        // Check if rate limited
        if (isRateLimited(ipAddress)) {
            throw new RuntimeException("Too many failed registration attempts. Please try again later.");
        }
        
        try {
            // Validate passwords match
            if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
                throw new RuntimeException("Passwords do not match");
            }
            
            // Check if username exists
            if (userRepository.existsByUsername(registerRequest.getUsername())) {
                throw new RuntimeException("Username is already taken");
            }
            
            // Check if email exists
            if (userRepository.existsByEmail(registerRequest.getEmail())) {
                throw new RuntimeException("Email is already in use");
            }
            
            // Create new user
            System.out.println("Creating user with role: " + registerRequest.getRole());
            User user = User.builder()
                    .username(registerRequest.getUsername())
                    .firstName(registerRequest.getFirstName())
                    .lastName(registerRequest.getLastName())
                    .email(registerRequest.getEmail())
                    .password(passwordEncoder.encode(registerRequest.getPassword()))
                    .role(registerRequest.getRole())
                    .build();

            System.out.println("User created with role: " + user.getRole());
            
            user = userRepository.save(user);
            
            String accessToken = jwtUtil.generateAccessToken(user);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
            
            // Register successful attempt
            registerSuccessfulAttempt(ipAddress);
            
            AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                    user.getId(),
                    user.getUsername(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getRole()
            );
            
            return new AuthResponse(accessToken, userInfo);
        } catch (Exception e) {
            // Register failed attempt
            registerFailedAttempt(ipAddress);
            throw e;
        }
    }
    
    public String getRefreshTokenForUser(Long userId) {
        RefreshToken refreshToken = refreshTokenService.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Refresh token not found for user"));
        return refreshToken.getToken();
    }
    
    public AuthResponse refreshToken(String refreshTokenStr, String ipAddress) {
        // Check if IP is blocked
        if (isIpBlocked(ipAddress)) {
            throw new RuntimeException("IP address is temporarily blocked due to too many failed attempts");
        }
        
        // Check if rate limited
        if (isRateLimited(ipAddress)) {
            throw new RuntimeException("Too many failed refresh token attempts. Please try again later.");
        }
        
        try {
            return refreshTokenService.findByToken(refreshTokenStr)
                    .map(refreshTokenService::verifyExpiration)
                    .map(RefreshToken::getUser)
                    .map(user -> {
                        String accessToken = jwtUtil.generateAccessToken(user);
                        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                                user.getId(),
                                user.getUsername(),
                                user.getFirstName(),
                                user.getLastName(),
                                user.getEmail(),
                                user.getRole()
                        );
                        return new AuthResponse(accessToken, userInfo);
                    })
                    .orElseThrow(() -> new RuntimeException("Refresh token is not in database"));
        } catch (Exception e) {
            // Register failed attempt
            registerFailedAttempt(ipAddress);
            throw e;
        }
    }
    
    public void logout(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        refreshTokenService.deleteByUser(user);
    }
    
    public boolean isUsernameTaken(String username) {
        System.out.println("Checking if username is taken: " + username);
        boolean exists = userRepository.existsByUsername(username);
        System.out.println("Username " + username + " exists: " + exists);
        return exists;
    }
    
    public boolean isIpBlocked(String ipAddress) {
        Optional<LoginAttempt> loginAttemptOpt = loginAttemptRepository.findByIpAddress(ipAddress);
        if (loginAttemptOpt.isPresent()) {
            LoginAttempt loginAttempt = loginAttemptOpt.get();
            return loginAttempt.isBlocked();
        }
        return false;
    }
    
    public boolean isRateLimited(String ipAddress) {
        Optional<LoginAttempt> loginAttemptOpt = loginAttemptRepository.findByIpAddress(ipAddress);
        if (loginAttemptOpt.isPresent()) {
            LoginAttempt loginAttempt = loginAttemptOpt.get();
            // Check if blocked
            if (loginAttempt.isBlocked()) {
                return true;
            }
            // Check if rate limited (8 attempts, 15 minutes block)
            return loginAttempt.isRateLimited(8, 15);
        }
        return false;
    }
    
    public void registerFailedAttempt(String ipAddress) {
        Optional<LoginAttempt> loginAttemptOpt = loginAttemptRepository.findByIpAddress(ipAddress);
        LoginAttempt loginAttempt;
        
        if (loginAttemptOpt.isPresent()) {
            loginAttempt = loginAttemptOpt.get();
            loginAttempt.incrementAttempts();
            
            // If attempts reach 8, block for 15 minutes
            if (loginAttempt.getAttempts() >= 8) {
                loginAttempt.setBlockedUntil(LocalDateTime.now().plusMinutes(15));
            }
        } else {
            loginAttempt = new LoginAttempt(ipAddress);
            loginAttempt.incrementAttempts();
        }
        
        loginAttemptRepository.save(loginAttempt);
    }
    
    public void registerSuccessfulAttempt(String ipAddress) {
        Optional<LoginAttempt> loginAttemptOpt = loginAttemptRepository.findByIpAddress(ipAddress);
        if (loginAttemptOpt.isPresent()) {
            LoginAttempt loginAttempt = loginAttemptOpt.get();
            loginAttempt.resetAttempts();
            loginAttempt.setBlockedUntil(null);
            loginAttemptRepository.save(loginAttempt);
        }
    }
    
    public LoginAttempt getLoginAttemptStatus(String ipAddress) {
        return loginAttemptRepository.findByIpAddress(ipAddress).orElse(new LoginAttempt(ipAddress));
    }

    public void storeFcmToken(String fcmToken, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String userName = jwtUtil.extractUsername(token); // Extracted from token
            User user = userRepository.findByUsername(userName).orElseThrow(() -> new RuntimeException("User not found"));
            user.setFirebaseToken(fcmToken);
            userRepository.save(user);
        } else {
            throw new RuntimeException("Invalid Authorization header");
        }
    }
}


