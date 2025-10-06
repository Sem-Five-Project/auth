package com.edu.tutor_platform.booking.dto;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Data
@Builder
public class CheckClassExistRequestDTO {
    @NotNull
    private Long languageId;
    @NotNull
    private Long subjectId;
    @NotNull
    private Long tutorId;
    @NotNull
    private Long studentId;
    @NotNull
    @Pattern(regexp = "(?i)RECURRING|ONE_TIME", message = "classType must be RECURRING or ONE_TIME")
    private String classType; // RECURRING or ONE_TIME
}
