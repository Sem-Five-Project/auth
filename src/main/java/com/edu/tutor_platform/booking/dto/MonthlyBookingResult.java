package com.edu.tutor_platform.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class MonthlyBookingResult {
    private boolean success;
    private List<Long> unavailableSlots;
}
