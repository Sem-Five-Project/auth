package com.edu.tutor_platform.studentprofile.entity;

import com.edu.tutor_platform.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

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

    private String educationLevel;


    private Membership membership;
}

