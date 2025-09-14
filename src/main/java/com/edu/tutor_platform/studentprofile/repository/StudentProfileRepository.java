package com.edu.tutor_platform.studentprofile.repository;

import com.edu.tutor_platform.studentprofile.entity.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {

    // Find a student profile by the user ID
    Optional<StudentProfile> findByUserId(Long userId);
    
    // Alternative method name for consistency
    Optional<StudentProfile> findByUser_Id(Long userId);
    
    // Find student profile by user email
    @Query("SELECT sp FROM StudentProfile sp JOIN sp.user u WHERE u.email = :email")
    Optional<StudentProfile> findByUser_Email(@Param("email") String email);
    
    // Find students by status
    List<StudentProfile> findByStatus(Short status);
    
    // Find active students
    @Query("SELECT sp FROM StudentProfile sp WHERE sp.status = 1")
    List<StudentProfile> findActiveStudents();
    
    // Find students by membership type
    @Query("SELECT sp FROM StudentProfile sp WHERE sp.membership = :membership")
    List<StudentProfile> findByMembership(@Param("membership") String membership);
    
    // Check if user already has a student profile
    boolean existsByUserId(Long userId);
}
