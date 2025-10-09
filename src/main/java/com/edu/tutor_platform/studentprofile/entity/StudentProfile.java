package com.edu.tutor_platform.studentprofile.entity;

import com.edu.tutor_platform.studentprofile.enums.StudentProfileStatus;
import com.edu.tutor_platform.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "StudentProfile")
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

    @Column(name = "education_level")
    private String educationLevel;

    @Column(name = "stream")
    private String stream;

    @Column(name = "class_count")
    private Integer classCount;

    @Column(name = "session_count")
    private Integer sessionCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "membership")
    private Membership membership;
}

