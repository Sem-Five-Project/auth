package com.edu.tutor_platform.booking.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {

    private Long bookingId;
    private Long studentId;
    private String studentName;
    private Long slotId;
    private Long tutorId;
    private String tutorName;
    private LocalDate slotDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDateTime createdAt;
    private LocalDateTime lockedUntil;
    private Boolean isConfirmed;
    private Double amount;
    private String paymentStatus;
    private String paymentId;
    
    // Additional booking details
    private String subjectName;
    private Double hourlyRate;
    private String bookingStatus; // RESERVED, CONFIRMED, CANCELLED, EXPIRED
}