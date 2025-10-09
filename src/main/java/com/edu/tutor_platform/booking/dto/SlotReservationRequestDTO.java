package com.edu.tutor_platform.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlotReservationRequestDTO {
    
    @NotNull(message = "Slot ID is required")
    private Long slotId;
}