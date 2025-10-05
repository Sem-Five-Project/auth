package com.edu.tutor_platform.subject.entity;

import com.edu.tutor_platform.subject.enums.EducationLevel;
import com.edu.tutor_platform.subject.enums.HighSchoolStreamType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subject")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subject_id")
    private Long subjectId;

    @Column(name = "name", nullable = false, unique = true, length = 255)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "education_level")
    private EducationLevel educationLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "stream")
    private HighSchoolStreamType stream;
}
