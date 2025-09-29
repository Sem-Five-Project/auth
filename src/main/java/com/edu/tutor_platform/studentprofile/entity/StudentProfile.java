package com.edu.tutor_platform.studentprofile.entity;

import com.edu.tutor_platform.studentprofile.enums.EducationalLevel;
import com.edu.tutor_platform.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "student_profile")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Long studentId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "admin_notes")
    private String adminNotes;

    @Builder.Default
    @Column(name = "status", nullable = false)
    private Short status = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "educational_level")
    private EducationalLevel educationalLevel;

    @Column(name = "education_level")
    private String educationLevel;

    @Column(name = "stream")
    private String stream;

    @Enumerated(EnumType.STRING)
    @Column(name = "membership")
    private Membership membership;
}

