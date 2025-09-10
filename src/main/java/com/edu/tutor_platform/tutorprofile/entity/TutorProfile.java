package com.edu.tutor_platform.tutorprofile.entity;

import com.edu.tutor_platform.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

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
    private Long tutorId;

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
    @Enumerated(EnumType.ORDINAL)
    private TutorProfileStatus status = TutorProfileStatus.ACTIVE;

    @Column(name="admin_notes")
    private String adminNotes;
}
