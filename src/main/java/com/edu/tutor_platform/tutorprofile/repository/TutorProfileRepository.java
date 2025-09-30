// // package com.edu.tutor_platform.tutorprofile.repository;

// // import com.edu.tutor_platform.tutorprofile.entity.TutorProfile;
// // import org.springframework.data.jpa.repository.JpaRepository;

// // import java.util.Optional;

// // public interface TutorProfileRepository extends JpaRepository<TutorProfile, Long> {
// //     Optional<TutorProfile> findByUserId(Long userId);
// // }
// package com.edu.tutor_platform.tutorprofile.repository;

// import com.edu.tutor_platform.tutorprofile.entity.TutorProfile;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // <-- ADD THIS IMPORT

// public interface TutorProfileRepository extends JpaRepository<TutorProfile, Long>, JpaSpecificationExecutor<TutorProfile> { // <-- AND EXTEND HERE
// }
package com.edu.tutor_platform.tutorprofile.repository;

import com.edu.tutor_platform.tutorprofile.entity.TutorProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.edu.tutor_platform.tutorprofile.entity.TutorProfileStatus;

import java.math.BigDecimal;
import java.util.List;

public interface TutorProfileRepository extends JpaRepository<TutorProfile, Long>, JpaSpecificationExecutor<TutorProfile> {
    
    // Search tutors by name and bio
    @Query("SELECT tp FROM TutorProfile tp JOIN tp.user u WHERE " +
           "(LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(tp.bio) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "tp.rating >= :minRating AND tp.experienceInMonths >= :minExperience")
    Page<TutorProfile> searchTutors(@Param("query") String query,
                                   @Param("minRating") BigDecimal minRating,
                                   @Param("minExperience") Integer minExperience,
                                   Pageable pageable);
    
    // Get tutor name suggestions
    @Query("SELECT DISTINCT CONCAT(u.firstName, ' ', u.lastName) FROM TutorProfile tp JOIN tp.user u " +
           "WHERE LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT(:query, '%'))")
    List<String> findTutorNameSuggestions(@Param("query") String query, Pageable pageable);





    // Count search results
    @Query("SELECT COUNT(tp) FROM TutorProfile tp JOIN tp.user u WHERE " +
           "(LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(tp.bio) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "tp.rating >= :minRating AND tp.experienceInMonths >= :minExperience")
    Long countSearchResults(@Param("query") String query,
                           @Param("minRating") BigDecimal minRating,
                           @Param("minExperience") Integer minExperience);


    @Query("SELECT tp FROM TutorProfile tp JOIN tp.user u WHERE " +
            "(:name IS NULL OR LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE :name) AND " +
            "(:username IS NULL OR LOWER(u.username) LIKE :username) AND " +
            "(:email IS NULL OR LOWER(u.email) LIKE :email) AND " +
            "(:tutorId IS NULL OR tp.tutorId = :tutorId) AND " +
            "(:status IS NULL OR UPPER(tp.status) = :status) AND " +
            "(:verified IS NULL OR tp.verified = :verified)")
    Page<TutorProfile> searchByAdmin(
            @Param("name") String name,
            @Param("username") String username,
            @Param("email") String email,
            @Param("tutorId") Long tutorId,
            @Param("status") String status,
            @Param("verified") Boolean verified,
            Pageable pageable);

    List<TutorProfile> findByStatusIsNull();

    @Query("SELECT tp FROM TutorProfile tp JOIN tp.tutorSubjects tus WHERE tus.verification = 'PENDING' AND tp.status = 'ACTIVE' AND tp.verified = true")
    List<TutorProfile> findByTutorsHavePendingSubjects();

}
