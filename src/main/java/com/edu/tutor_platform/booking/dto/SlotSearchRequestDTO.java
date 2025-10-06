package com.edu.tutor_platform.booking.dto;

import com.edu.tutor_platform.booking.enums.DayOfWeek;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlotSearchRequestDTO {

    private Long tutorId;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate specificDate;
    private List<DayOfWeek> daysOfWeek;
    private LocalTime preferredStartTime;
    private LocalTime preferredEndTime;
    private Long subjectId;
    private Double minHourlyRate;
    private Double maxHourlyRate;
    private Double minRating;
    private Boolean recurringOnly;
    private Integer page = 0;
    private Integer size = 20;
    private String sortBy = "slotDate"; 
    private String sortDirection = "ASC"; 
}