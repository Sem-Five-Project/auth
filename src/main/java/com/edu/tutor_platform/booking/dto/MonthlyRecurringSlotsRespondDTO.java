package com.edu.tutor_platform.booking.dto;
import lombok.Builder;
import lombok.Value;
import java.time.LocalTime;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Singular;
import java.util.List;

@Value
@Builder
public class MonthlyRecurringSlotsRespondDTO {
    @JsonProperty("start_time")
    LocalTime startTime;

    @JsonProperty("end_time")
    LocalTime endTime;

    @JsonProperty("availability_id")
    Long availabilityId;

    @JsonProperty("slots")
    @Singular
    List<TutorSlotDateDTO> slots;
}