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
import com.edu.tutor_platform.studentprofile.repository.StudentProfileRepository;
import com.edu.tutor_platform.tutorprofile.repository.TutorProfileRepository;
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

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private TutorProfileRepository tutorProfileRepository;
    
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
            refreshTokenService.createRefreshToken(user);

            registerSuccessfulAttempt(ipAddress);

            AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                    user.getId(),
                    user.getUsername(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getRole(),
                    user.getProfileImage()
            );
            try {
                if ("STUDENT".equalsIgnoreCase(user.getRole())) {
                    studentProfileRepository.findByUser_Id(user.getId()).ifPresentOrElse(
                            p -> userInfo.setStudentId(p.getStudentId()),
                            () -> {
                                // Auto-create minimal profile if absent
                                com.edu.tutor_platform.studentprofile.entity.StudentProfile sp =
                                        com.edu.tutor_platform.studentprofile.entity.StudentProfile.builder()
                                                .user(user)
                                                .build();
                                sp = studentProfileRepository.save(sp);
                                userInfo.setStudentId(sp.getStudentId());
                            }
                    );
                } else if ("TUTOR".equalsIgnoreCase(user.getRole())) {
            tutorProfileRepository.findByUser_Id(user.getId()).ifPresentOrElse(
                tp -> userInfo.setTutorId(tp.getTutorId()),
                () -> {
                com.edu.tutor_platform.tutorprofile.entity.TutorProfile tpNew =
                    com.edu.tutor_platform.tutorprofile.entity.TutorProfile.builder()
                        .user(user)
                        .build();
                tpNew = tutorProfileRepository.save(tpNew);
                userInfo.setTutorId(tpNew.getTutorId());
                }
            );
                }
            } catch (Exception ex) {
                System.out.println("Failed to attach role specific id: " + ex.getMessage());
            }
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
        User user = new User(
            registerRequest.getUsername(),
            registerRequest.getFirstName(),
            registerRequest.getLastName(),
            registerRequest.getEmail(),
            passwordEncoder.encode(registerRequest.getPassword()),
            registerRequest.getRole()
        );
        User savedUser = userRepository.save(user);

        String accessToken = jwtUtil.generateAccessToken(savedUser);
        refreshTokenService.createRefreshToken(savedUser);
            
            // Register successful attempt
            registerSuccessfulAttempt(ipAddress);
            
        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
            savedUser.getId(),
            savedUser.getUsername(),
            savedUser.getFirstName(),
            savedUser.getLastName(),
            savedUser.getEmail(),
            savedUser.getRole(),
            savedUser.getProfileImage()
        );
            try {
        if ("STUDENT".equalsIgnoreCase(savedUser.getRole())) {
            studentProfileRepository.findByUser_Id(savedUser.getId()).ifPresentOrElse(
                p -> userInfo.setStudentId(p.getStudentId()),
                () -> {
                com.edu.tutor_platform.studentprofile.entity.StudentProfile sp =
                    com.edu.tutor_platform.studentprofile.entity.StudentProfile.builder()
                        .user(savedUser)
                        .build();
                sp = studentProfileRepository.save(sp);
                userInfo.setStudentId(sp.getStudentId());
                }
            );
        } else if ("TUTOR".equalsIgnoreCase(savedUser.getRole())) {
            tutorProfileRepository.findByUser_Id(savedUser.getId()).ifPresentOrElse(
                tp -> userInfo.setTutorId(tp.getTutorId()),
                () -> {
                com.edu.tutor_platform.tutorprofile.entity.TutorProfile tpNew =
                    com.edu.tutor_platform.tutorprofile.entity.TutorProfile.builder()
                        .user(savedUser)
                        .build();
                tpNew = tutorProfileRepository.save(tpNew);
                userInfo.setTutorId(tpNew.getTutorId());
                }
            );
                }
            } catch (Exception ex) {
                System.out.println("Failed to attach role specific id (register): " + ex.getMessage());
            }
            
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
                                user.getRole(),
                                user.getProfileImage()
                        );
                        // Attach role-specific id as in login/register (no auto-create here to avoid side-effects on silent refresh)
                        try {
                            if ("STUDENT".equalsIgnoreCase(user.getRole())) {
                                studentProfileRepository.findByUser_Id(user.getId()).ifPresent(p -> userInfo.setStudentId(p.getStudentId()));
                            } else if ("TUTOR".equalsIgnoreCase(user.getRole())) {
                                tutorProfileRepository.findByUser_Id(user.getId()).ifPresent(tp -> userInfo.setTutorId(tp.getTutorId()));
                            }
                        } catch (Exception ex) {
                            System.out.println("Failed to attach role specific id (refresh): " + ex.getMessage());
                        }
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

    /**
     * Build an AuthResponse.UserInfo for a given authenticated user (used by /auth/me).
     * Ensures role-specific id is present (auto-creates profile if missing to be consistent with login behaviour).
     */
    public AuthResponse.UserInfo buildCurrentUserInfo(User user) {
        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                user.getProfileImage()
        );
        try {
            if ("STUDENT".equalsIgnoreCase(user.getRole())) {
                studentProfileRepository.findByUser_Id(user.getId()).ifPresentOrElse(
                        p -> userInfo.setStudentId(p.getStudentId()),
                        () -> {
                            com.edu.tutor_platform.studentprofile.entity.StudentProfile sp =
                                    com.edu.tutor_platform.studentprofile.entity.StudentProfile.builder()
                                            .user(user)
                                            .build();
                            sp = studentProfileRepository.save(sp);
                            userInfo.setStudentId(sp.getStudentId());
                        }
                );
            } else if ("TUTOR".equalsIgnoreCase(user.getRole())) {
                tutorProfileRepository.findByUser_Id(user.getId()).ifPresentOrElse(
                        tp -> userInfo.setTutorId(tp.getTutorId()),
                        () -> {
                            com.edu.tutor_platform.tutorprofile.entity.TutorProfile tpNew =
                                    com.edu.tutor_platform.tutorprofile.entity.TutorProfile.builder()
                                            .user(user)
                                            .build();
                            tpNew = tutorProfileRepository.save(tpNew);
                            userInfo.setTutorId(tpNew.getTutorId());
                        }
                );
            }
        } catch (Exception ex) {
            System.out.println("Failed to attach role specific id (/me): " + ex.getMessage());
        }
        return userInfo;
    }
}


