package com.edu.tutor_platform.booking.entity;

import com.edu.tutor_platform.booking.enums.SlotStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "slot_instance")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SlotInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "slot_id")
    private Long slotId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "availability_id", nullable = false)
    private TutorAvailability tutorAvailability;

    @Column(name = "slot_date", nullable = false)
    private LocalDate slotDate;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SlotStatus status = SlotStatus.AVAILABLE;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    // One-to-many relationship with Booking
    @Builder.Default
    @OneToMany(mappedBy = "slotInstance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings = new ArrayList<>();
}