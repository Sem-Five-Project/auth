package com.edu.tutor_platform.tutorsearch.filter.service;

import com.edu.tutor_platform.tutorsearch.filter.dto.TutorFilterRequestDTO;
import com.edu.tutor_platform.tutorsearch.filter.dto.TutorFilterResultDTO;
import com.edu.tutor_platform.tutorsearch.filter.repository.TutorFilterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TutorFilterService {

    private final TutorFilterRepository repository;

    public List<TutorFilterResultDTO> search(TutorFilterRequestDTO req) {
        normalize(req);
        return repository.searchByFilters(req);
    }

    private void normalize(TutorFilterRequestDTO req) {
        // Treat empty subjects list as no filter
        if (req.getSubjects() != null && req.getSubjects().isEmpty()) {
            req.setSubjects(null);
        }
    }
}