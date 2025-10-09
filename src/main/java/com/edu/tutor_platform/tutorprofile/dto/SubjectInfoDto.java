package com.edu.tutor_platform.tutorprofile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectInfoDto {
    private Long id;
    private String name;
    private String verificationDocs;
}

