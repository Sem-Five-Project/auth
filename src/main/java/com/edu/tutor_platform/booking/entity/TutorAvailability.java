package com.edu.tutor_platform.booking.entity;

import com.edu.tutor_platform.booking.enums.DayOfWeek;
import com.edu.tutor_platform.tutorprofile.entity.TutorProfile;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tutor_availability")
@Data
@ToString(exclude = { "slotInstances", "tutorProfile" })
@EqualsAndHashCode(exclude = { "slotInstances", "tutorProfile" })
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TutorAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "availability_id")
    private Long availabilityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "tutor_id", nullable = false)
    private TutorProfile tutorProfile;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Builder.Default
    @Column(name = "recurring", nullable = false)
    private Boolean recurring = false;

    // One-to-many relationship with SlotInstance
    @Builder.Default
    @OneToMany(mappedBy = "tutorAvailability", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SlotInstance> slotInstances = new ArrayList<>();
}