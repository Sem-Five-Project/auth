// // package com.edu.tutor_platform.tutorsearch.service;

// // import com.edu.tutor_platform.tutorprofile.entity.TutorProfile;
// // import com.edu.tutor_platform.tutorprofile.repository.TutorProfileRepository;
// // import com.edu.tutor_platform.tutorsearch.dto.TutorSearchDTO;
// // import com.edu.tutor_platform.tutorsearch.repository.TutorSpecification;
// // import org.springframework.beans.factory.annotation.Autowired;
// // import org.springframework.data.domain.Page;
// // import org.springframework.data.domain.Pageable;
// // import org.springframework.data.jpa.domain.Specification;
// // import org.springframework.stereotype.Service;

// // @Service
// // public class TutorSearchService {

// //     @Autowired
// //     private TutorProfileRepository tutorProfileRepository;

// //     public Page<TutorSearchDTO> findTutors(Double minRating, Integer minExperience, Double minHourlyRate, Pageable pageable) {

// //         // 1. Build the dynamic specification
// //         Specification<TutorProfile> spec = Specification.where(TutorSpecification.hasRatingGreaterThanOrEqual(minRating))
// //                 .and(TutorSpecification.hasExperienceGreaterThanOrEqual(minExperience))
// //                 .and(TutorSpecification.hasMinHourlyRate(minHourlyRate));
// //                 // .and(...) for other filters

// //         // 2. Execute the query with pagination
// //         return tutorProfileRepository.findAll(spec, pageable)
// //                 .map(this::convertToDTO); // Convert entity to DTO
// //     }

// //     private TutorSearchDTO convertToDTO(TutorProfile tutorProfile) {
// //         // Logic to map the TutorProfile entity and its related entities
// //         // to the flat TutorSearchDTO.
// //         // This is where you'll fetch subject, language, etc.
// //         // and build the final optimized object.
// //         return new TutorSearchDTO(/*... mapping logic ...*/);
// //     }
// // }
// package com.edu.tutor_platform.tutorsearch.service;

// import com.edu.tutor_platform.tutorprofile.entity.TutorProfile;
// import com.edu.tutor_platform.tutorprofile.repository.TutorProfileRepository;
// import com.edu.tutor_platform.tutorsearch.dto.TutorCardDTO;
// import com.edu.tutor_platform.tutorsearch.dto.TutorSubjectDTO;
// import com.edu.tutor_platform.tutorsearch.repository.TutorSpecifications;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.jpa.domain.Specification;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.util.Collections;
// import java.util.stream.Collectors;

// @Service
// public class TutorSearchService {

//     @Autowired
//     private TutorProfileRepository tutorProfileRepository;

//     @Transactional(readOnly = true)
//     public Page<TutorCardDTO> searchTutors(
//             Double minRating, Integer minExperience, Double minHourlyRate, Double maxHourlyRate,
//             Double minClassCompletionRate, String subjectName, String tutorName, Pageable pageable) {

//         // Build the dynamic query specification from the filters
//         Specification<TutorProfile> spec = TutorSpecifications.withFilters(
//                 minRating, minExperience, minHourlyRate, maxHourlyRate,
//                 minClassCompletionRate, subjectName, tutorName);

//         // Execute the query and get a paginated result of entities
//         Page<TutorProfile> tutorProfilesPage = tutorProfileRepository.findAll(spec, pageable);

//         // Map the entity page to a DTO page
//         return tutorProfilesPage.map(this::convertToDto);
//     }

//     private TutorCardDTO convertToDto(TutorProfile tutorProfile) {
//         TutorCardDTO dto = new TutorCardDTO();

//         // Map data from TutorProfile entity
//         dto.setTutorProfileId(tutorProfile.getTutorId());
//         dto.setExperience(tutorProfile.getExperienceInMonths());
//         dto.setRating(tutorProfile.getRating());
//         dto.setClassCompletionRate(tutorProfile.getClassCompletionRate());

//         // Map data from the associated User entity
//         if (tutorProfile.getUser() != null) {
//             dto.setUserId(tutorProfile.getUser().getId());
//             dto.setFirstName(tutorProfile.getUser().getFirstName());
//             dto.setLastName(tutorProfile.getUser().getLastName());
//             dto.setProfileImage(tutorProfile.getUser().getProfileImage());
//         }

//         // Map the subjects taught by the tutor
//         // Note: This assumes you have a `getTutorSubjects()` method on your TutorProfile entity
//         // that returns a list of the join table entity (e.g., List<TutorSubject>).
//         if (tutorProfile.getTutorSubjects() != null) {
//              dto.setSubjects(tutorProfile.getTutorSubjects().stream()
//                 .map(tutorSubject -> new TutorSubjectDTO(
//                     tutorSubject.getSubject().getName(),
//                     tutorSubject.getHourlyRate(),
//                     tutorSubject.getLanguage().getName() // Assumes Language entity is linked
//                 ))
//                 .collect(Collectors.toList()));
//         } else {
//             dto.setSubjects(Collections.emptyList());
//         }
       
//         return dto;
//     }
// }


package com.edu.tutor_platform.tutorsearch.service;

import com.edu.tutor_platform.tutorprofile.entity.TutorProfile;
import com.edu.tutor_platform.tutorprofile.repository.TutorProfileRepository;
import com.edu.tutor_platform.tutorsearch.dto.TutorCardDTO;
import com.edu.tutor_platform.tutorsearch.dto.TutorSubjectDTO;
import com.edu.tutor_platform.tutorsearch.dto.TutorLanguageDTO;
import com.edu.tutor_platform.tutorsearch.repository.TutorSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class TutorSearchService {

    @Autowired
    private TutorProfileRepository tutorProfileRepository;

    @Transactional(readOnly = true)
    public Page<TutorCardDTO> searchTutors(
            Double minRating, Integer minExperience, Double minHourlyRate, Double maxHourlyRate,
            Double minClassCompletionRate, String subjectName, String tutorName, Pageable pageable) {

        // Build dynamic query specification from filters
        Specification<TutorProfile> spec = TutorSpecifications.withFilters(
                minRating, minExperience, minHourlyRate, maxHourlyRate,
                minClassCompletionRate, subjectName, tutorName
        );

        // Fetch paginated results
        Page<TutorProfile> tutorProfilesPage = tutorProfileRepository.findAll(spec, pageable);

        // Map entities to DTOs
        return tutorProfilesPage.map(this::convertToDto);
    }

    // -----------------------------
    // Private helper: Convert entity -> DTO
    // -----------------------------
    private TutorCardDTO convertToDto(TutorProfile tutorProfile) {
        TutorCardDTO dto = new TutorCardDTO();

        // Map TutorProfile fields
        dto.setTutorProfileId(tutorProfile.getTutorId());
        dto.setExperience(tutorProfile.getExperienceInMonths());
        dto.setRating(tutorProfile.getRating() != null ? tutorProfile.getRating().doubleValue() : 0.0);
        dto.setClassCompletionRate(tutorProfile.getClassCompletionRate() != null ? tutorProfile.getClassCompletionRate().doubleValue() : 0.0);
        dto.setHourlyRate(tutorProfile.getHourlyRate() != null ? tutorProfile.getHourlyRate().doubleValue() : 0.0);
        dto.setBio(tutorProfile.getBio());

        // Map associated User fields
        if (tutorProfile.getUser() != null) {
            dto.setUserId(tutorProfile.getUser().getId());
            dto.setFirstName(tutorProfile.getUser().getFirstName());
            dto.setLastName(tutorProfile.getUser().getLastName());
            dto.setProfileImage(tutorProfile.getUser().getProfileImage());
        }

        // Map tutor subjects (NO language - that's separate)
        if (tutorProfile.getTutorSubjects() != null && !tutorProfile.getTutorSubjects().isEmpty()) {
            dto.setSubjects(
                tutorProfile.getTutorSubjects().stream()
                    .map(tutorSubject -> TutorSubjectDTO.builder()
                        .subjectId(tutorSubject.getSubject().getSubjectId()) // Subject ID
                        .subjectName(tutorSubject.getSubject().getName()) // Get name from Subject entity via subject_id
                        .hourlyRate(tutorSubject.getHourlyRate() != null ? tutorSubject.getHourlyRate().doubleValue() : 0.0) // From tutor_subjects.hourly_rate
                        .build())
                    .collect(Collectors.toList())
            );
        } else {
            dto.setSubjects(Collections.emptyList());
        }

        // Map tutor languages (from separate tutor_language table)
        if (tutorProfile.getTutorLanguages() != null && !tutorProfile.getTutorLanguages().isEmpty()) {
            dto.setLanguages(
                tutorProfile.getTutorLanguages().stream()
                    .map(tutorLanguage -> TutorLanguageDTO.builder()
                        .languageId(tutorLanguage.getLanguage().getLanguageId())
                        .languageName(tutorLanguage.getLanguage().getName())
                        .build())
                    .collect(Collectors.toList())
            );
        } else {
            dto.setLanguages(Collections.emptyList());
        }

        return dto;
    }
}
