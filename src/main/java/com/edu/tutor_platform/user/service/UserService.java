// service/UserService.java
package com.edu.tutor_platform.user.service;

import com.edu.tutor_platform.studentprofile.service.StudentProfileService;
import com.edu.tutor_platform.tutorprofile.service.TutorProfileService;
import com.edu.tutor_platform.user.dto.RegisterRequest;
import com.edu.tutor_platform.user.entity.User;
import com.edu.tutor_platform.user.exception.EmailAlreadyInUseException;
import com.edu.tutor_platform.user.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.edu.tutor_platform.util.JwtUtil;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StudentProfileService studentProfileService;
    private final TutorProfileService tutorProfileService;
    private final JwtUtil jwtUtil;

    public void register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyInUseException("Email already in use");
        }

        User user = User.builder()
                .name(request.getName()) // assuming it's 'name' in the request
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole()) // required
                .profilePicture(request.getProfilePicture()) // optional
                .build();

        userRepository.save(user);

        User savedUser = userRepository.save(user);

        switch (savedUser.getRole()) {
            case STUDENT:
                studentProfileService.createStudentProfile(savedUser);
                break;
            case TUTOR:
                tutorProfileService.createTutorProfile(savedUser);
                break;
        }

    }

    public void storeFcmToken(String fcmToken, HttpServletRequest request) {
        System.out.println("Storing FCM token: " + fcmToken);
        String authHeader = request.getHeader("Authorization");
        System.out.println("Authorization header: " + authHeader);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            System.out.println("Extracted JWT: " + jwt);
            String email = jwtUtil.extractEmail(jwt);
            System.out.println("Extracted email: " + email);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            System.out.println("User found: " + user.getEmail());
            user.setFirebaseToken(fcmToken);
            userRepository.save(user);
        } else {
            System.out.println("Authorization header missing or invalid");
        }
    }
}
