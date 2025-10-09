package com.edu.tutor_platform.booking.dto;

import lombok.Data;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
public class BulkReservationRequestDTO {

    @NotEmpty(message = "slotIds cannot be empty.")
    private List<Long> slotIds;

    @NotNull(message = "studentId cannot be null.")
    private Long studentId;
}
