package com.edu.tutor_platform.studentprofile.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentUpcomingClassResponseDto {

    private boolean upcoming;

    @JsonProperty("get_student_upcoming_classes")
    private List<UpcomingClassDetail> upcomingClasses;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpcomingClassDetail {
        @JsonProperty("class_id")
        private int classId;

        @JsonProperty("class_link")
        private String classLink;

        @JsonProperty("tutor_name")
        private String tutorName;

        private String subject;
        private String language;

        @JsonProperty("class_type")
        private String classType;

        @JsonProperty("class_docs")
        private List<ClassDoc> classDocs;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClassDoc {
        @JsonProperty("class_doc_id")
        private int classDocId;

        @JsonProperty("doc_type")
        private String docType;

        private String link;
    }
}
