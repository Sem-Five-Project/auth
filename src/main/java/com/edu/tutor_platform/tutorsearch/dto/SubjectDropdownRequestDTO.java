package com.edu.tutor_platform.tutorsearch.dto;

import com.edu.tutor_platform.tutorsearch.filter.enums.EDUCATION_LEVEL;
import com.edu.tutor_platform.tutorsearch.filter.enums.STREAM_TYPE;
import lombok.*;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubjectDropdownRequestDTO {
    private EDUCATION_LEVEL educationLevel; // nullable
    private STREAM_TYPE stream;             // nullable

}