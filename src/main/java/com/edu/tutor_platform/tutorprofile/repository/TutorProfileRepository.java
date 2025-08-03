package com.edu.tutor_platform.tutorprofile.repository;

import com.edu.tutor_platform.tutorprofile.entity.TutorProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TutorProfileRepository extends JpaRepository<TutorProfile, Long> {
    Optional<TutorProfile> findByUserUserId(Long userId);
}
