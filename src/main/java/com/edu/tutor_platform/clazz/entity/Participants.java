package com.edu.tutor_platform.clazz.entity;

import com.edu.tutor_platform.session.entity.Session;
import com.edu.tutor_platform.studentprofile.entity.StudentProfile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "participants", uniqueConstraints = @UniqueConstraint(columnNames = { "class_id", "student_id" }))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Participants {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id")
    private Long participantId;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private ClassEntity classEntity;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private StudentProfile student;

    @Column(name = "payment_id")
    private String paymentId;

    // @ManyToOne
    // @JoinColumn(name = "session_id", nullable = false)
    // private Session session;
}