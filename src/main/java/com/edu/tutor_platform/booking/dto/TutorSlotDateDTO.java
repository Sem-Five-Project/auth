package com.edu.tutor_platform.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class TutorSlotDateDTO {
    @JsonProperty("slot_id")
    Long slotId;

    @JsonProperty("date")
    LocalDate date;

    @JsonProperty("status")
    String status;

}