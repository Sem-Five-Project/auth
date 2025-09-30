package com.edu.tutor_platform.tutorprofile.dto;


import com.edu.tutor_platform.subject.entity.Subject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TutorApprovalsDto {
    private Long TutorId;
    private String userName;
    private List<SubjectInfoDto> subjects;
    private LocalDateTime submissionDate;
}
