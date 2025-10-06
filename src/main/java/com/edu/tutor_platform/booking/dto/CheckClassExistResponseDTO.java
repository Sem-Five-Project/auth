// ...existing code...
package com.edu.tutor_platform.booking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.time.LocalTime;
import java.util.List;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CheckClassExistResponseDTO {

    @JsonProperty("exists")
    Boolean exists;

    @JsonProperty("class_id")
    Long classId;

    @JsonProperty("slots")
    List<SlotInfo> slots;

    @Value
    @Builder
    public static class SlotInfo {
        @JsonProperty("weekday")
        String weekday;

        @JsonProperty("start_time")
        LocalTime startTime;

        @JsonProperty("end_time")
        LocalTime endTime;
    }
}
// ...existing code...