package com.edu.tutor_platform.studentprofile.repository;


import com.edu.tutor_platform.studentprofile.entity.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

    public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
        Optional<StudentProfile> findByUserUserId(Long userId);
    }

