package com.edu.tutor_platform.tutorsearch.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectDropdownResponseDTO {
    private Long subjectId;
    private String subjectName;
}