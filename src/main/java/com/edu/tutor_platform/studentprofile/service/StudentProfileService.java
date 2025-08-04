package com.edu.tutor_platform.studentprofile.service;


import com.edu.tutor_platform.studentprofile.repository.StudentProfileRepository;
import com.edu.tutor_platform.studentprofile.entity.StudentProfile;
import com.edu.tutor_platform.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentProfileService {

    private final StudentProfileRepository studentProfileRepository;

    @Transactional
    public void createStudentProfile(User user) {
        StudentProfile studentProfile = StudentProfile.builder()
                .user(user)
                .membership(null)
                .build();

        studentProfileRepository.save(studentProfile);
    }
}

