package com.edu.tutor_platform.studentprofile.dto;

import com.edu.tutor_platform.studentprofile.enums.StudentProfileStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentsDto {
    private Long studentId;
    private String firstName;
    private String lastName;
    private StudentProfileStatus status;
    private String username;
}
