package com.edu.tutor_platform.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.time.LocalTime;
import java.util.List;

@Value
@Builder
public class NextMonthSlotRespondDTO {

    @JsonProperty("availability_id")
    Long availabilityId;

    @JsonProperty("start_time")
    LocalTime startTime;

    @JsonProperty("end_time")
    LocalTime endTime;

    @JsonProperty("week_day")
    String weekDay;

    @JsonProperty("available_dates")
    List<String> availableDates;
}
