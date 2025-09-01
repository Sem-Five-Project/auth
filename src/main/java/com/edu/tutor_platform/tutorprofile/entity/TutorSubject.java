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

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tutor_subject")
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
    @ManyToOne
    @JoinColumn(name = "tutor_id", nullable = false)
    private TutorProfile tutorProfile;

    @Column(name = "subject_name", nullable = false)
    private String subjectName;  // Previously assumed Subject entity

    @Column(name = "hourly_rate", precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    @Column(name = "language", nullable = false)
    private String language;  // Previously assumed Language entity

}
