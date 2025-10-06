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
import com.edu.tutor_platform.tutorprofile.enums.VerificationEnum;
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
    @Column(name = "tutor_subject_id") // ✅ matches DB
    private Integer  tutorSubjectId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id", nullable = false)
    private TutorProfile tutorProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(name = "hourly_rate", precision = 10, scale = 2, nullable = false)
    private BigDecimal hourlyRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification")
    private VerificationEnum verification;  // you'll need an enum in Java

    @Column(name = "verification_docs")
    private String verificationDocs;
}
