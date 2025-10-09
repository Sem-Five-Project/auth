package com.edu.tutor_platform.booking.dto;

import com.edu.tutor_platform.booking.enums.DayOfWeek;
import com.edu.tutor_platform.booking.enums.SlotStatus;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalTime;

@Value
@Builder
public class SlotInstanceSummaryDTO {
    Long slotId;
    Long availabilityId;
    LocalDate slotDate;
    DayOfWeek dayOfWeek;
    LocalTime startTime;
    LocalTime endTime;
    SlotStatus status;
}
