package com.edu.tutor_platform.studentprofile.repository;

import com.edu.tutor_platform.studentprofile.entity.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {

    // Find a student profile by the user ID
    Optional<StudentProfile> findByUserId(Long userId);


}
