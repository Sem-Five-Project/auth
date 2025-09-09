package com.edu.tutor_platform.booking.dto;

import com.edu.tutor_platform.booking.enums.DayOfWeek;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TutorAvailabilityDTO {

    private Long availabilityId;

    @NotNull(message = "Tutor ID is required")
    private Long tutorId;

    @NotNull(message = "Day of week is required")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    @Builder.Default
    private Boolean recurring = false;

    // Additional fields for response
    private String tutorName;
    private Integer generatedSlots; // Number of slots generated for this availability
}