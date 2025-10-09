package com.edu.tutor_platform.tutorsearch.filter.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubjectWithRateDTO {
    private String name;
    private Number subjectId;
    @JsonProperty("hourly_rate")
    private BigDecimal hourlyRate;
}