package com.edu.tutor_platform.studentprofile.dto;

import lombok.*;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor @Builder
public class StudentAcademicInfoDTO {
    private String educationLevel;  // enum/string value
    private String stream;          // optional stream
}
