// package com.edu.tutor_platform.tutorsearch.dto;

// import lombok.AllArgsConstructor;
// import lombok.Data;

// /**
//  * Represents a single subject that a tutor teaches, including their specific rate.
//  */
// @Data
// @AllArgsConstructor
// public class TutorSubjectDTO {
//     private String subjectName;
//     private double hourlyRate;
//     private String language;
// }
package com.edu.tutor_platform.tutorsearch.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TutorSubjectDTO {

    private String subjectName;
    private Double hourlyRate;
    private String language;
}
