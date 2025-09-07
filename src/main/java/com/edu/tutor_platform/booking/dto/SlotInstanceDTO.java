package com.edu.tutor_platform.booking.dto;

import com.edu.tutor_platform.booking.enums.DayOfWeek;
import com.edu.tutor_platform.booking.enums.SlotStatus;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlotInstanceDTO {

    private Long slotId;
    private Long availabilityId;
    private Long tutorId;
    private String tutorName;
    private LocalDate slotDate;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private SlotStatus status;
    private Double hourlyRate;
    private String tutorBio;
    private Integer tutorExperience;
    
    // Additional metadata for booking interface
    private Boolean isRecurring;
    private String subjectName;
    private Double rating;
}