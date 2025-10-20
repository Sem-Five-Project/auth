package com.edu.tutor_platform.studentprofile.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClasssDetailResponseDto {
    // Updated property names to match new DB JSON keys
    private List<ClassDetail> get_student_classes_with_details;
    private List<ClassDetail> get_student_classes_with_details2;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClassDetail {
        private Long class_id;
        private Long tutor_id;
        private Long subject_id;
        private Long language_id;
        private String class_link;
        private String tutor_name;
        private String subject;
        private String language;
        private String class_type;
        private List<ClassDoc> class_docs; // may be null
        // hourly_rate may be null in DB; use wrapper Double
        private Double hourly_rate;
        private List<AvailabilityGroup> class_slots;
        // optional rating object returned by DB for some classes
        private RatingDto rating;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClassDoc {
        private Long class_doc_id;
        private String doc_type;
        private String link;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvailabilityGroup {
        private Long availability_id;
        private List<SlotItem> slots;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SlotItem {
        private String date;   // ISO date string
        private String status; // e.g., UPCOMMING, ONGOING
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RatingDto {
        private Integer rating_value;
        private String review_text;
    }
}
