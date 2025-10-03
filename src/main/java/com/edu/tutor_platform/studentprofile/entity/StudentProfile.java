package com.edu.tutor_platform.studentprofile.entity;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.edu.tutor_platform.studentprofile.enums.EducationalLevel;
import com.edu.tutor_platform.studentprofile.enums.StudentProfileStatus;
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
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)               // keeps PostgreSQL enum (student_profile_status)
    @Column(name = "status", columnDefinition = "student_profile_status")
    private StudentProfileStatus status = StudentProfileStatus.ACTIVE;

    // @Enumerated(EnumType.STRING)
    // @Column(name = "educational_level")
    // private EducationalLevel educationalLevel;

    @Column(name = "education_level")
    private String educationLevel;

    @Column(name = "stream")
    private String stream;

    @Enumerated(EnumType.STRING)
    @Column(name = "membership")
    private Membership membership;
}




