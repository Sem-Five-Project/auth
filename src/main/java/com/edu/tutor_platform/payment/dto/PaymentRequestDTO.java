package com.edu.tutor_platform.payment.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {
    private String orderId;
    private double amount;
    private String currency = "LKR";
    
    // Existing fields
    private Long classId;
    private Long studentId;
    private Long tutorId;
    
    // New fields for slot booking
    private Long availabilityId;
    private Long slotId;
    
    // Optional: Additional booking details
    private String sessionNotes;
    private String bookingType; // "individual", "group", etc.
}