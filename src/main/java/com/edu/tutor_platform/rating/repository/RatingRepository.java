package com.edu.tutor_platform.rating.repository;

import com.edu.tutor_platform.rating.entity.Rating;
import com.edu.tutor_platform.studentprofile.entity.StudentProfile;
import com.edu.tutor_platform.tutorprofile.entity.TutorProfile;
import com.edu.tutor_platform.session.entity.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    // Check if rating already exists for a session and student
    Optional<Rating> findBySessionAndStudent(Session session, StudentProfile student);

    // Get all ratings for a specific tutor
    List<Rating> findByTutorOrderByCreatedAtDesc(TutorProfile tutor);

    // Get paginated ratings for a specific tutor
    Page<Rating> findByTutorOrderByCreatedAtDesc(TutorProfile tutor, Pageable pageable);

    // Get all ratings by a specific student
    List<Rating> findByStudentOrderByCreatedAtDesc(StudentProfile student);

    // Get paginated ratings by a specific student
    Page<Rating> findByStudentOrderByCreatedAtDesc(StudentProfile student, Pageable pageable);

    // Get ratings for a specific session
    List<Rating> findBySession(Session session);

    // Calculate average rating for a tutor
    @Query("SELECT AVG(r.ratingValue) FROM Rating r WHERE r.tutor = :tutor")
    BigDecimal findAverageRatingByTutor(@Param("tutor") TutorProfile tutor);

    // Count total ratings for a tutor
    Long countByTutor(TutorProfile tutor);

    // Count ratings by value for a tutor (for rating distribution)
    @Query("SELECT COUNT(r) FROM Rating r WHERE r.tutor = :tutor AND r.ratingValue >= :minValue AND r.ratingValue <= :maxValue")
    Long countByTutorAndRatingValueBetween(@Param("tutor") TutorProfile tutor,
            @Param("minValue") BigDecimal minValue,
            @Param("maxValue") BigDecimal maxValue);

    // Get recent ratings for a tutor (last N ratings)
    @Query("SELECT r FROM Rating r WHERE r.tutor = :tutor ORDER BY r.createdAt DESC")
    List<Rating> findRecentRatingsByTutor(@Param("tutor") TutorProfile tutor, Pageable pageable);

    // Check if student has already rated a tutor
    boolean existsByTutorAndStudent(TutorProfile tutor, StudentProfile student);
}