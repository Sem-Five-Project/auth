package com.edu.tutor_platform.tutorsearch.controller;

import com.edu.tutor_platform.tutorsearch.dto.SubjectDropdownRequestDTO;
import com.edu.tutor_platform.tutorsearch.dto.SubjectDropdownResponseDTO;
import com.edu.tutor_platform.tutorsearch.filter.enums.EDUCATION_LEVEL;
import com.edu.tutor_platform.tutorsearch.filter.enums.STREAM_TYPE;
import com.edu.tutor_platform.tutorsearch.service.FilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Subject filter endpoint.
 * Accepts (optional) educationLevel & stream (both may be null).
 * Returns subjects (id + name) valid for that combination.
 */
@RestController
@RequestMapping("/filters")
@RequiredArgsConstructor
public class FilterController {

    private final FilterService filterService;

    @PostMapping("/subjects")
    public ResponseEntity<List<SubjectDropdownResponseDTO>> getSubjects(
            @RequestBody SubjectDropdownRequestDTO requestDTO) {

        // Normalize nulls (service can interpret null = ALL)
        EDUCATION_LEVEL level = requestDTO != null ? requestDTO.getEducationLevel() : null;
        STREAM_TYPE stream = requestDTO != null ? requestDTO.getStream() : null;

        List<SubjectDropdownResponseDTO> subjects = filterService.getSubjects(
                SubjectDropdownRequestDTO.builder()
                        .educationLevel(level)
                        .stream(stream)
                        .build()
        );
        return ResponseEntity.ok(subjects);
    }
}