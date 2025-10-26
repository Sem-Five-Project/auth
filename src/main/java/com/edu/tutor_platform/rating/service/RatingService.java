package com.edu.tutor_platform.rating.service;

import com.edu.tutor_platform.rating.dto.AddRatingRequest;
import com.edu.tutor_platform.rating.dto.RatingResponse;
import com.edu.tutor_platform.rating.dto.TutorRatingsSummary;
import com.edu.tutor_platform.rating.entity.Rating;
import com.edu.tutor_platform.rating.exception.DuplicateRatingException;
import com.edu.tutor_platform.rating.exception.RatingNotFoundException;
import com.edu.tutor_platform.rating.exception.UnauthorizedRatingException;
import com.edu.tutor_platform.rating.repository.RatingRepository;
import com.edu.tutor_platform.session.entity.Session;
import com.edu.tutor_platform.studentprofile.repository.StudentProfileRepository;
import com.edu.tutor_platform.session.service.SessionService;
import com.edu.tutor_platform.studentprofile.entity.StudentProfile;
import com.edu.tutor_platform.studentprofile.service.StudentProfileService;
import com.edu.tutor_platform.tutorprofile.entity.TutorProfile;
import com.edu.tutor_platform.tutorprofile.service.TutorProfileService;
import com.edu.tutor_platform.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RatingService {

    private final RatingRepository ratingRepository;
    private final StudentProfileService studentProfileService;
    private final TutorProfileService tutorProfileService;
    private final SessionService sessionService;
    private final StudentProfileRepository studentProfileRepository;

    /**
     * Add a new rating for a session
     */
    @Transactional
    public RatingResponse addRating(AddRatingRequest request, User currentUser) {
        // Get student profile for current user
        StudentProfile student = studentProfileService.getStudentProfileByUserId(currentUser.getId());

        // Get session and validate
        Session session = sessionService.getSessionEntityById(request.getSessionId());

        // Get tutor profile and validate
        TutorProfile tutor = tutorProfileService.getTutorProfileById(request.getTutorId());

        // Validate that the session belongs to the tutor being rated
        if (!session.getClassEntity().getTutorId().equals(tutor.getTutorId())) {
            throw new UnauthorizedRatingException("Session does not belong to the specified tutor");
        }

        // Validate that the student was part of this session (you might need to add
        // this logic based on your booking system)
        // For now, we'll assume any student can rate if they have access to the session

        // Check if rating already exists for this session and student
        if (ratingRepository.findBySessionAndStudent(session, student).isPresent()) {
            throw new DuplicateRatingException("You have already rated this session");
        }

        // Create and save rating
        Rating rating = Rating.builder()
                .student(student)
                .tutor(tutor)
        .session(session)
        .classEntity(session.getClassEntity())
                .ratingValue(request.getRatingValue())
                .reviewText(request.getReviewText())
                .build();

        Rating savedRating = ratingRepository.save(rating);

        // Update tutor's average rating
        updateTutorAverageRating(tutor);

        return convertToRatingResponse(savedRating);
    }

    /**
     * Get all ratings for a specific tutor
     */
    @Transactional(readOnly = true)
    public List<RatingResponse> getRatingsByTutor(Long tutorId, int page, int size) {
        TutorProfile tutor = tutorProfileService.getTutorProfileById(tutorId);

        Pageable pageable = PageRequest.of(page, size);
        Page<Rating> ratingsPage = ratingRepository.findByTutorOrderByCreatedAtDesc(tutor, pageable);

        return ratingsPage.getContent().stream()
                .map(this::convertToRatingResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all ratings by a specific student
     */
    @Transactional(readOnly = true)
    public List<RatingResponse> getRatingsByStudent(User currentUser, int page, int size) {
        StudentProfile student = studentProfileService.getStudentProfileByUserId(currentUser.getId());

        Pageable pageable = PageRequest.of(page, size);
        Page<Rating> ratingsPage = ratingRepository.findByStudentOrderByCreatedAtDesc(student, pageable);

        return ratingsPage.getContent().stream()
                .map(this::convertToRatingResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get ratings summary for a tutor
     */
    @Transactional(readOnly = true)
    public TutorRatingsSummary getTutorRatingsSummary(Long tutorId) {
        TutorProfile tutor = tutorProfileService.getTutorProfileById(tutorId);

        BigDecimal averageRating = ratingRepository.findAverageRatingByTutor(tutor);
        Long totalRatings = ratingRepository.countByTutor(tutor);

        // Count ratings by star value
        Long fiveStarRatings = ratingRepository.countByTutorAndRatingValueBetween(tutor, new BigDecimal("4.5"),
                new BigDecimal("5.0"));
        Long fourStarRatings = ratingRepository.countByTutorAndRatingValueBetween(tutor, new BigDecimal("3.5"),
                new BigDecimal("4.4"));
        Long threeStarRatings = ratingRepository.countByTutorAndRatingValueBetween(tutor, new BigDecimal("2.5"),
                new BigDecimal("3.4"));
        Long twoStarRatings = ratingRepository.countByTutorAndRatingValueBetween(tutor, new BigDecimal("1.5"),
                new BigDecimal("2.4"));
        Long oneStarRatings = ratingRepository.countByTutorAndRatingValueBetween(tutor, new BigDecimal("1.0"),
                new BigDecimal("1.4"));

        return TutorRatingsSummary.builder()
                .tutorId(tutor.getTutorId())
                .tutorName(tutor.getUser().getFirstName() + " " + tutor.getUser().getLastName())
                .averageRating(
                        averageRating != null ? averageRating.setScale(1, RoundingMode.HALF_UP) : BigDecimal.ZERO)
                .totalRatings(totalRatings)
                .fiveStarRatings(fiveStarRatings)
                .fourStarRatings(fourStarRatings)
                .threeStarRatings(threeStarRatings)
                .twoStarRatings(twoStarRatings)
                .oneStarRatings(oneStarRatings)
                .build();
    }

    /**
     * Get a specific rating by ID
     */
    @Transactional(readOnly = true)
    public RatingResponse getRatingById(Long ratingId) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new RatingNotFoundException("Rating not found with ID: " + ratingId));

        return convertToRatingResponse(rating);
    }

    /**
     * Delete a rating (only by the student who created it)
     */
    @Transactional
    public void deleteRating(Long ratingId, User currentUser) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new RatingNotFoundException("Rating not found with ID: " + ratingId));

        // Check if current user is the student who created the rating
        StudentProfile student = studentProfileService.getStudentProfileByUserId(currentUser.getId());
        if (!rating.getStudent().getStudentId().equals(student.getStudentId())) {
            throw new UnauthorizedRatingException("You can only delete your own ratings");
        }

        TutorProfile tutor = rating.getTutor();
        ratingRepository.delete(rating);

        // Update tutor's average rating after deletion
        updateTutorAverageRating(tutor);
    }

    /**
     * Update tutor's average rating
     */
    private void updateTutorAverageRating(TutorProfile tutor) {
        BigDecimal averageRating = ratingRepository.findAverageRatingByTutor(tutor);
        if (averageRating != null) {
            tutor.setRating(averageRating.setScale(1, RoundingMode.HALF_UP));
        } else {
            tutor.setRating(BigDecimal.ZERO);
        }
        tutorProfileService.save(tutor);
    }

    /**
     * Convert Rating entity to RatingResponse DTO
     */
    private RatingResponse convertToRatingResponse(Rating rating) {
        return RatingResponse.builder()
                .ratingId(rating.getRatingId())
                .studentId(rating.getStudent().getStudentId())
                .studentName(rating.getStudent().getUser().getFirstName() + " "
                        + rating.getStudent().getUser().getLastName())
                .tutorId(rating.getTutor().getTutorId())
                .tutorName(rating.getTutor().getUser().getFirstName() + " " + rating.getTutor().getUser().getLastName())
                .sessionId(rating.getSession().getSessionId())
                .sessionName(rating.getSession().getSessionName())
                .ratingValue(rating.getRatingValue())
                .reviewText(rating.getReviewText())
                .createdAt(rating.getCreatedAt())
                .updatedAt(rating.getUpdatedAt())
                .build();
    }

    /**
     * Create a quick rating from external source (no authentication)
     * Fields that cannot be resolved will be left null where DB allows; otherwise an exception is thrown.
     */
    @Transactional
    public RatingResponse createQuickRating(com.edu.tutor_platform.rating.dto.RatingQuickRequest request) {
        // Resolve tutor and session (require these for relational integrity)
        TutorProfile tutor = null;
        if (request.getTutorId() != null) {
            tutor = tutorProfileService.getTutorProfileById(request.getTutorId());
        }

        Session session = null;
        if (request.getClass_id() != null) {
            session = sessionService.getSessionEntityById(request.getClass_id());
        }

        if (tutor == null) {
            throw new IllegalArgumentException("Invalid or missing tutorId");
        }
        if (session == null) {
            throw new IllegalArgumentException("Invalid or missing class_id (session id)");
        }

        // Pick any existing student profile as fallback so DB non-null constraint is satisfied
        StudentProfile student = studentProfileRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("No student profile found in the system to associate rating"));

        java.math.BigDecimal ratingValue = null;
        if (request.getRatingValue() != null) {
            ratingValue = java.math.BigDecimal.valueOf(request.getRatingValue().intValue());
        }

        Rating rating = Rating.builder()
                .student(student)
                .tutor(tutor)
                .session(session)
        .classEntity(session.getClassEntity())
        .ratingValue(ratingValue)
                .reviewText(request.getFeedback())
                .build();

        Rating saved = ratingRepository.save(rating);

        // update tutor average
        updateTutorAverageRating(tutor);

        return convertToRatingResponse(saved);
    }
}