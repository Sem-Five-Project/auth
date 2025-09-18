package com.edu.tutor_platform.studentprofile.repository;

import com.edu.tutor_platform.studentprofile.entity.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {

    // Find a student profile by the user ID
//    Optional<StudentProfile> findByUserId(Long userId);
    Page<StudentProfile> findAll(Pageable pageable);


}
