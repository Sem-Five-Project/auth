package com.edu.tutor_platform.tutorsearch.repository;

import com.edu.tutor_platform.tutorprofile.entity.TutorProfile;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Join;

public class TutorSpecification {

    public static Specification<TutorProfile> hasRatingGreaterThanOrEqual(Double minRating) {
        return (root, query, cb) -> minRating == null ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get("rating"), minRating);
    }

    public static Specification<TutorProfile> hasExperienceGreaterThanOrEqual(Integer minExperience) {
        return (root, query, cb) -> minExperience == null ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get("experience"), minExperience);
    }

    public static Specification<TutorProfile> hasMinHourlyRate(Double minRate) {
        return (root, query, cb) -> {
            if (minRate == null) return cb.conjunction();
            Join<Object, Object> tutorSubjects = root.join("tutorSubjects");
            return cb.greaterThanOrEqualTo(tutorSubjects.get("hourlyRate"), minRate);
        };
    }

    // Add similar methods for max hourly rate, subject name, tutor name, etc.
}