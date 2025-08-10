package com.edu.tutor_platform.subject.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Subject")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subject_id")
    private Long subjectId;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

}
