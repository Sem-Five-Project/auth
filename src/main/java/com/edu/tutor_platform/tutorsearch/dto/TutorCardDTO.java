// package com.edu.tutor_platform.tutorsearch.dto;

// import lombok.Data;
// import java.util.List;

// /**
//  * A flattened, optimized DTO representing a tutor's search result card.
//  * This contains only the data needed for the UI.
//  */
// @Data
// public class TutorCardDTO {
//     private Long tutorProfileId;
//     private Long userId;
//     private String firstName;
//     private String lastName;
//     private String profileImage;
//     private Integer experience; // in months
//     private Double rating;
//     private Double classCompletionRate;
//     private List<TutorSubjectDTO> subjects;
// }



package com.edu.tutor_platform.tutorsearch.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TutorCardDTO {

    private Long tutorProfileId;
    private Long userId;
    private String firstName;
    private String lastName;
    private String profileImage;
    private String bio;

    private Integer experience;
    private Double rating;
    private Double classCompletionRate;
    private Double hourlyRate; // Base hourly rate (can vary by subject)

    private List<TutorSubjectDTO> subjects; // List of subjects with their specific rates and languages
    private List<TutorLanguageDTO> languages; // List of languages tutor can teach in
}
