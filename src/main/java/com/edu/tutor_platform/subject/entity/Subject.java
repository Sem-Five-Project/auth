package com.edu.tutor_platform.subject.entity;


import com.edu.tutor_platform.subject.enums.HighSchoolStreamType;
import org.hibernate.annotations.JdbcType;

import com.edu.tutor_platform.tutorsearch.filter.enums.EDUCATION_LEVEL;
import com.edu.tutor_platform.tutorsearch.filter.enums.STREAM_TYPE;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

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




//    @Enumerated(EnumType.STRING)
//    @Column(name = "stream")
//    private HighSchoolStreamType stream;




    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "education_level", nullable = false, columnDefinition = "education_level")
    private EDUCATION_LEVEL educationLevel;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "stream", nullable = false, columnDefinition = "high_school_streame_type")
    private STREAM_TYPE streamType;

}
