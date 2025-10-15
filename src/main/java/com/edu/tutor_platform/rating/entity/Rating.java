package com.edu.tutor_platform.rating.entity;

import com.edu.tutor_platform.clazz.entity.ClassEntity;
import com.edu.tutor_platform.session.entity.Session;
import com.edu.tutor_platform.studentprofile.entity.StudentProfile;
import com.edu.tutor_platform.tutorprofile.entity.TutorProfile;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rating")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rating_id")
    private Long ratingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false, foreignKey = @ForeignKey(name = "fk_rating_student"))
    private StudentProfile student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id", nullable = false, foreignKey = @ForeignKey(name = "fk_rating_tutor"))
    private TutorProfile tutor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false, foreignKey = @ForeignKey(name = "fk_rating_session"))
    private Session session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false, foreignKey = @ForeignKey(name = "fk_rating_class"))
    private ClassEntity classEntity;

    @NotNull
    @DecimalMin(value = "1.0", message = "Rating must be at least 1.0")
    @DecimalMax(value = "5.0", message = "Rating must not exceed 5.0")
    @Column(name = "rating_value", precision = 2, scale = 1, nullable = false)
    private BigDecimal ratingValue;

    @Column(name = "review_text", columnDefinition = "TEXT")
    private String reviewText;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        // Set classEntity from session if not already set
        if (classEntity == null && session != null) {
            classEntity = session.getClassEntity();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}