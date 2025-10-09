package com.edu.tutor_platform.tutorsearch.filter.repository;

import com.edu.tutor_platform.tutorsearch.filter.dto.TutorFilterRequestDTO;
import com.edu.tutor_platform.tutorsearch.filter.dto.TutorFilterResultDTO;

import java.util.List;

public interface TutorFilterRepository {
    List<TutorFilterResultDTO> searchByFilters(TutorFilterRequestDTO request);
}