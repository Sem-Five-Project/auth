package com.edu.tutor_platform.tutorsearch.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TutorLanguageDTO {

    private Long languageId;
    private String languageName;
}