package com.edu.tutor_platform.tutorsearch.filter.service;

import com.edu.tutor_platform.tutorsearch.filter.dto.TutorFilterRequestDTO;
import com.edu.tutor_platform.tutorsearch.filter.dto.TutorFilterResultDTO;
import com.edu.tutor_platform.tutorsearch.filter.repository.TutorFilterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class TutorFilterService {

    private final TutorFilterRepository repository;

    public List<TutorFilterResultDTO> search(TutorFilterRequestDTO req) {
        normalize(req);
        return repository.searchByFilters(req);
    }

    private void normalize(TutorFilterRequestDTO req) {
        if (req.getEducationLevel() != null) {
            String v = req.getEducationLevel().trim().toLowerCase(Locale.ROOT);
            // Map frontend synonyms to DB enum values
            switch (v) {
                case "undergraduate", "undergrad" -> req.setEducationLevel("UNDERGRAD");
                case "postgraduate", "postgrad" -> req.setEducationLevel("POSTGRAD");
                case "a_level", "advanced", "advanced_level", "a-level" -> req.setEducationLevel("A_LEVEL");
                case "o_level", "ordinary", "ordinary_level", "o-level" -> req.setEducationLevel("O_LEVEL");
                case "primary" -> req.setEducationLevel("PRIMARY");
                default -> req.setEducationLevel(req.getEducationLevel().toUpperCase(Locale.ROOT));
            }
        }
        if (req.getStream() != null) {
            req.setStream(req.getStream().trim().toUpperCase(Locale.ROOT));
        }
        // Optionally lowercase subjects for function which lower()s compare
        if (req.getSubjects() != null) {
            req.setSubjects(req.getSubjects().stream()
                    .filter(s -> s != null && !s.isBlank())
                    .map(s -> s.trim())
                    .toList());
        }
    }
}