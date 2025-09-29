package com.edu.tutor_platform.tutorsearch.filter.controller;

import com.edu.tutor_platform.tutorsearch.filter.dto.TutorFilterRequestDTO;
import com.edu.tutor_platform.tutorsearch.filter.dto.TutorFilterResultDTO;
import com.edu.tutor_platform.tutorsearch.filter.service.TutorFilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tutors/filter")
@RequiredArgsConstructor
public class TutorFilterController {

    private final TutorFilterService tutorFilterService;

    @PostMapping
    public ResponseEntity<List<TutorFilterResultDTO>> filter(@RequestBody TutorFilterRequestDTO request) {
        return ResponseEntity.ok(tutorFilterService.search(request));
    }
}