package com.edu.tutor_platform.studentprofile.entity;

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
    @JoinColumn(name = "user_id", nullable = false, unique = true, foreignKey = @ForeignKey(name = "fk_student_user"))
    private User user;

    @Column(name = "education_level")
    private String educationLevel;

    @Column(name = "status")
    private StudentProfileStatus status = StudentProfileStatus.ACTIVE;

    @Column(name="admin_notes")
    private String adminNotes;
}
