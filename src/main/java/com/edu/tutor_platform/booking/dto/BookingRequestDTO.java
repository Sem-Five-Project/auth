package com.edu.tutor_platform.booking.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDTO {

    @NotNull(message = "Slot ID is required")
    private Long slotId;

    @NotNull(message = "Student ID is required")
    private Long studentId;

    // Optional: If booking for a specific subject
    private Long subjectId;

    // For payment processing
    private String paymentMethod;
    private String returnUrl;
    private String cancelUrl;
    
    // Additional notes or requirements
    private String notes;
}