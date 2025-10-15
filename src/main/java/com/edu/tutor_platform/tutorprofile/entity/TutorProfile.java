package com.edu.tutor_platform.tutorprofile.entity;

import com.edu.tutor_platform.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity

@Table(name = "tutor_profile")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TutorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tutor_id")
    private Long tutorId; // ✅ use getTutorId() instead of getId()

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true, foreignKey = @ForeignKey(name = "fk_tutor_user"))
    private User user;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "hourly_rate", precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    @Column(name = "verified", nullable = false)
    private boolean verified = false;

    @Column(name = "status")
    private String status = "ACTIVE";

    @Column(name = "admin_notes")
    private String adminNotes;

    // ⭐ Rating (0.0 – 5.0) with Lombok @Builder default
    @Builder.Default
    @Column(name = "rating", precision = 2, scale = 1, nullable = false)
    private BigDecimal rating = BigDecimal.ZERO;

    // ⭐ Experience in months with @Builder default
    @Builder.Default
    @Column(name = "experience_months", nullable = false)
    private Integer experienceInMonths = 0;

    // ⭐ Class completion rate (0.00 – 100.00%) with @Builder default
    @Builder.Default
    @Column(name = "class_completion_rate", precision = 5, scale = 2, nullable = false)
    private BigDecimal classCompletionRate = BigDecimal.ZERO;

    // ⭐ Subjects relation (one tutor has many subjects)
    @Builder.Default
    @OneToMany(mappedBy = "tutorProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TutorSubject> tutorSubjects = new ArrayList<>();

    // ⭐ Languages relation (many-to-many through junction table)
    @Builder.Default
    @OneToMany(mappedBy = "tutorProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TutorLanguage> tutorLanguages = new ArrayList<>();

    // Bidirectional mapping for TutorAvailability
    @com.fasterxml.jackson.annotation.JsonManagedReference
    @OneToMany(mappedBy = "tutorProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<com.edu.tutor_platform.booking.entity.TutorAvailability> tutorAvailabilities = new ArrayList<>();

}
