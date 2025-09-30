package com.edu.tutor_platform.session.entity;


import jakarta.persistence.*;
import lombok.*;
import com.edu.tutor_platform.clazz.entity.ClassEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "session")
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
    @JoinColumn(name = "class_id", nullable = false, foreignKey = @ForeignKey(name = "fk_session_class"))
    private ClassEntity classEntity;

    @Column(name = "session_name", nullable = false)
    private String sessionName;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;


    @Column(name = "status")
    private String status = "SCHEDULED";

    @Column(name = "link_for_meeting")
    private String linkForMeeting;

    @Column(name = "link_for_host")
    private String linkForHost;

    @Column(name = "notification_sent")
    private boolean notificationSent = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;



    @PrePersist
    private void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
