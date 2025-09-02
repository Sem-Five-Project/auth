package com.edu.tutor_platform.payment.entity;

import com.edu.tutor_platform.booking.entity.Booking;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderId;
    private double amount;
    private String currency = "LKR";  // Default to LKR
    private String status;  // e.g., "SUCCESS", "FAILED"
    private Long classId;
    private Long studentId;
    private Long tutorId;
    private LocalDateTime timestamp = LocalDateTime.now();
    
    // Add relationship with Booking
    @OneToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;
}