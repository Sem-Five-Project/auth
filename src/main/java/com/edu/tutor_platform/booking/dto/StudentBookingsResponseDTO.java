package com.edu.tutor_platform.booking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentBookingsResponseDTO {
    @JsonProperty("booking_id")
    private Long bookingId;

    @JsonProperty("booking_status")
    private String bookingStatus; // CONFIRMED | RESERVED | EXPIRED

    @JsonProperty("payment_id")
    private String paymentId;

    @JsonProperty("paid_amount")
    private BigDecimal paidAmount;

    @JsonProperty("class_details")
    private ClassDetails classDetails;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClassDetails {
        private Tutor tutor;
        private Subject subject;
        private Language language;

        @JsonProperty("class_times")
        private List<ClassTimeWindow> classTimes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Tutor {
        private Long id;
        private String name;
        private String bio;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Subject {
        private Long id;
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Language {
        private Long id;
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClassTimeWindow {
        @JsonProperty("start_time")
        private LocalTime startTime;
        @JsonProperty("end_time")
        private LocalTime endTime;
        private List<LocalDate> slots; // list of dates belonging to this window
    }
}
