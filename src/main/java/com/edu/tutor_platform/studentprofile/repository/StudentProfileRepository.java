package com.edu.tutor_platform.studentprofile.repository;

import com.edu.tutor_platform.studentprofile.entity.StudentProfile;
import com.edu.tutor_platform.studentprofile.entity.StudentProfileStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {

    // Find a student profile by the user ID
//    Optional<StudentProfile> findByUserId(Long userId);
    Page<StudentProfile> findAll(Pageable pageable);


    Long countByStatus(StudentProfileStatus studentProfileStatus);

    @Query("SELECT sp FROM StudentProfile sp JOIN sp.user u WHERE " +
            "(:name IS NULL OR LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:username IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))) AND " +
            "(:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
            "(:studentId IS NULL OR sp.studentId = :studentId) AND " +
            "(:studentProfileStatus IS NULL OR sp.status = :studentProfileStatus)")
    Page<StudentProfile> searchByAdmin(
            @Param("name") String name,
            @Param("username") String username,
            @Param("email") String email,
            @Param("studentId") Long studentId,
            @Param("studentProfileStatus") StudentProfileStatus studentProfileStatus,
            Pageable pageable
    );


    @Query("SELECT COUNT(sp) FROM StudentProfile sp JOIN sp.user u " +
            "WHERE YEAR(u.createdAt) = YEAR(CURRENT_DATE) " +
            "AND MONTH(u.createdAt) = MONTH(CURRENT_DATE)")
    Long countNewStudentsThisMonth();

}
