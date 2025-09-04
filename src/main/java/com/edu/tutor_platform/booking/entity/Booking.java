package com.edu.tutor_platform.booking.entity;

import com.edu.tutor_platform.studentprofile.entity.StudentProfile;
import com.edu.tutor_platform.payment.entity.Payment;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "booking")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long bookingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentProfile studentProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id")
    private SlotInstance slotInstance;

    @Builder.Default
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Link to payment for completed bookings
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    // Fields for handling time-limited reservations
    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    @Builder.Default
    @Column(name = "is_confirmed")
    private Boolean isConfirmed = false;
    
    // Order ID for payment tracking
    @Column(name = "order_id", unique = true)
    private String orderId;
    
    // Additional booking metadata
    @Column(name = "session_notes", columnDefinition = "TEXT")
    private String sessionNotes;
    
    @Builder.Default
    @Column(name = "booking_type")
    private String bookingType = "individual";
    
    @Builder.Default
    @Column(name = "booking_status")
    private String bookingStatus = "PENDING"; // PENDING, CONFIRMED, CANCELLED, EXPIRED
    
    // Update timestamp when booking is modified
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}