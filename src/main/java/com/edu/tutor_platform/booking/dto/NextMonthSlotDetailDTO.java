package com.edu.tutor_platform.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class NextMonthSlotDetailDTO {

    @JsonProperty("slot_id")
    Long slotId;

    @JsonProperty("slot_date")
    LocalDate slotDate;

    @JsonProperty("status")
    String status;
}
