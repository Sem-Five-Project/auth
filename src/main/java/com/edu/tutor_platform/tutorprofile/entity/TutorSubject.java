// package com.edu.tutor_platform.tutorprofile.entity;

// import jakarta.persistence.*;
// import lombok.*;

// import java.math.BigDecimal;

// @Entity
// @Table(name = "tutor_subjects")
// @Data
// @NoArgsConstructor
// @AllArgsConstructor
// @Builder
// public class TutorSubject {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     private String name;

//     @Column(name = "hourly_rate", precision = 10, scale = 2)
//     private BigDecimal hourlyRate;

//     private String language;

//     @ManyToOne
//     @JoinColumn(name = "tutor_id", nullable = false)
//     private TutorProfile tutor;
// }
package com.edu.tutor_platform.tutorprofile.entity;

import com.edu.tutor_platform.subject.entity.Subject;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tutor_subjects")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TutorSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // Many subjects belong to one tutor
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id", nullable = false)
    private TutorProfile tutorProfile;

    // Link to actual Subject entity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(name = "hourly_rate", precision = 10, scale = 2, nullable = false)
    private BigDecimal hourlyRate;

    // Note: Languages are handled separately via TutorLanguage junction table
    // No language_id in tutor_subjects table

}
