package com.edu.tutor_platform.studentprofile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentProfileInfoRespondDTO {
    private String educationLevel;
    private String stream;
    private Integer classCount;
    private Integer sessionCount;
}
