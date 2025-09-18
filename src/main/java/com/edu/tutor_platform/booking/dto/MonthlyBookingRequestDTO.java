package com.edu.tutor_platform.booking.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class MonthlyBookingRequestDTO {

    @NotEmpty(message = "slotIds cannot be empty.")
    private List<Long> slotIds;

    @NotNull(message = "studentId cannot be null.")
    private Long studentId;
}
