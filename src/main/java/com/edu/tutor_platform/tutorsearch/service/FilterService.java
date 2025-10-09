package com.edu.tutor_platform.tutorsearch.service;

import com.edu.tutor_platform.tutorsearch.dto.SubjectDropdownRequestDTO;
import com.edu.tutor_platform.tutorsearch.dto.SubjectDropdownResponseDTO;
import com.edu.tutor_platform.tutorsearch.filter.enums.EDUCATION_LEVEL;
import com.edu.tutor_platform.subject.repository.SubjectRepository;

import com.edu.tutor_platform.tutorsearch.filter.enums.STREAM_TYPE;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilterService {

    private final SubjectRepository subjectRepository; // assume exists

    public List<SubjectDropdownResponseDTO> getSubjects(SubjectDropdownRequestDTO req) {
        EDUCATION_LEVEL level = req.getEducationLevel();
        STREAM_TYPE stream = req.getStream();
    var subjects = (level != null && stream != null) ?
        subjectRepository.findByEducationLevelAndStreamTypeOrderByNameAsc(level, stream) :
        (level != null) ? subjectRepository.findByEducationLevelOrderByNameAsc(level) :
        (stream != null) ? subjectRepository.findByStreamTypeOrderByNameAsc(stream) :
        subjectRepository.findAll(org.springframework.data.domain.Sort.by("name").ascending());

    return subjects.stream()
        .map(s -> SubjectDropdownResponseDTO.builder()
            .subjectId(s.getSubjectId())
            .subjectName(s.getName())
            .build())
        .toList();
    }
}