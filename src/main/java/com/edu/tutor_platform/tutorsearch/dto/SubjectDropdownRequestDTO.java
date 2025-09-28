package com.edu.tutor_platform.tutorsearch.dto;

import com.edu.tutor_platform.tutorsearch.enums.EDUCATIONAL_LEVEL;
import com.edu.tutor_platform.tutorsearch.enums.STREAM_TYPE;
import lombok.*;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubjectDropdownRequestDTO {
    private EDUCATIONAL_LEVEL educationLevel; // nullable
    private STREAM_TYPE stream;             // nullable

}