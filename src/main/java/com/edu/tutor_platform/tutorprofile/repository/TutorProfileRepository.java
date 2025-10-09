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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TutorProfileRepository extends JpaRepository<TutorProfile, Long>, JpaSpecificationExecutor<TutorProfile> {
       // Direct lookup by associated user id (improves performance vs. scanning all profiles)
       Optional<TutorProfile> findByUser_Id(Long userId);
    
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
}
