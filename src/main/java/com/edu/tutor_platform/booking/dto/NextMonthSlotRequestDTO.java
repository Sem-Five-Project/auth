package com.edu.tutor_platform.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NextMonthSlotRequestDTO {
    private List<Long> availabilityIds; // required
    private Integer year; // required
    private Integer month; // required (1-12)
}
