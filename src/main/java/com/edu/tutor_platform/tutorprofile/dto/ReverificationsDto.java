package com.edu.tutor_platform.tutorprofile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReverificationsDto {
    private Long TutorId;
    private String userName;
    private List<SubjectInfoDto> subjectInfo;
}
