package com.edu.tutor_platform.studentprofile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentStatsDto {
    private Long totalStudents;
    private Long activeStudents;
    private Long suspendedStudents;
    private Long newStudentsThisMonth;
}
