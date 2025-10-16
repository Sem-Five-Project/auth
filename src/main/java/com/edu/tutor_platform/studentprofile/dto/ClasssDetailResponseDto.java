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
    // Keep property name to match DB JSON exactly
    private List<ClassDetail> get_student_classes_with_details;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClassDetail {
        private Long class_id;
        private String class_link;
        private String tutor_name;
        private String subject;
        private String language;
        private String class_type;
        private List<ClassDoc> class_docs; // may be null
        private List<AvailabilityGroup> class_slots;
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
}
