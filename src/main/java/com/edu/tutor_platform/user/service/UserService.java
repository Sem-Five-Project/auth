// service/UserService.java
package com.edu.tutor_platform.user.service;

import com.edu.tutor_platform.studentprofile.service.StudentProfileService;
import com.edu.tutor_platform.tutorprofile.service.TutorProfileService;
import com.edu.tutor_platform.user.dto.RegisterRequest;
import com.edu.tutor_platform.user.entity.User;
import com.edu.tutor_platform.user.exception.EmailAlreadyInUseException;
import com.edu.tutor_platform.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StudentProfileService studentProfileService;
    private final TutorProfileService tutorProfileService;

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
}
