package com.edu.tutor_platform.session.entity;

import com.edu.tutor_platform.studentprofile.entity.StudentProfile;
import com.edu.tutor_platform.tutorprofile.entity.TutorProfile;
import com.edu.tutor_platform.subject.entity.Subject;
import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;

@Entity
@Table(name = "Session")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long sessionId;

    @ManyToOne
    @JoinColumn(name = "tutor_id", nullable = false, foreignKey = @ForeignKey(name = "fk_session_tutor"))
    private TutorProfile tutor;

    @ManyToOne
    @JoinColumn(name = "subject_id", foreignKey = @ForeignKey(name = "fk_session_subject"))
    private Subject subject;

    @Column(nullable = false)
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private SessionStatus status;

    private String linkForMeeting;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void onCreate() {
        createdAt = LocalDateTime.now();
    }

    private boolean notificationSent = false;
}
