package com.edu.tutor_platform.tutorsearch.filter.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TutorFilterResultDTO {
    private Long tutorId;
    private String bio;
    private Double rating;              // nullable
    private Integer experienceMonths;   // nullable
    private List<SubjectWithRateDTO> subjects;
    private List<LanguageWithIdDTO> languages;
}