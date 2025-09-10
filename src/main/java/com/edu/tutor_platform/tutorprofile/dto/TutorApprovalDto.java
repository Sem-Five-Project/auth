package com.edu.tutor_platform.tutorprofile.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TutorApprovalDto {
    private boolean approved;
    private String adminNotes;
}
