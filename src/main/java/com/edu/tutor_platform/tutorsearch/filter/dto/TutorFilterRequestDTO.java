package com.edu.tutor_platform.tutorsearch.filter.dto;
import com.edu.tutor_platform.tutorsearch.filter.enums.EDUCATION_LEVEL;
import com.edu.tutor_platform.tutorsearch.filter.enums.STREAM_TYPE;
import lombok.ToString;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@ToString
public class TutorFilterRequestDTO {
    private EDUCATION_LEVEL educationLevel;      // e.g. "undergraduate" (frontend) → map to DB "UNDERGRAD"
    private STREAM_TYPE stream;              // optional (if later needed)
    private List<String> subjects;      // ["Mathematics", ...]
    private String classType;           // "ONE_TIME" | "MONTHLY"
    private Double rating;              // min rating
    private Integer experience;         // min months
    private Double maxPrice;
    private SearchSession session;      // only for ONE_TIME
    private Recurring recurring;        // only for MONTHLY
    private SortSpec sort;
    private String search;              // tutor name search

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class SearchSession {
        private LocalDate date;
        private LocalTime startTime;
        private LocalTime endTime;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Recurring {
        private List<RecurringDay> days;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class RecurringDay {
        private Integer weekday;              // 1..7 or whatever frontend uses
        private List<SlotWindow> slots;       // optional
        private List<LocalDate> dates;        // optional
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class SlotWindow {
        private LocalTime startTime;
        private LocalTime endTime;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class SortSpec {
        private String field;     // "PRICE" | "RATING"
        private String direction; // "ASC" | "DESC"
    }
}