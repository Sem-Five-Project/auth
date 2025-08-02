package com.edu.tutor_platform.tutorprofile.service;

import com.edu.tutor_platform.tutorprofile.entity.TutorProfile;
import com.edu.tutor_platform.tutorprofile.repository.TutorProfileRepository;
import com.edu.tutor_platform.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TutorProfileService {

    private final TutorProfileRepository tutorProfileRepository;

    @Transactional
    public void createTutorProfile(User user) {
        TutorProfile tutorProfile = TutorProfile.builder()
                .user(user)
                .bio(null) // or provide default bio
                .hourlyRate(null) // or BigDecimal.ZERO
                .build();

        tutorProfileRepository.save(tutorProfile);
    }
}
