package com.edu.tutor_platform.tutorprofile.entity;

import com.edu.tutor_platform.language.entity.Language;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tutor_language")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(TutorLanguageId.class)
public class TutorLanguage {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id", nullable = false)
    private TutorProfile tutorProfile;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id", nullable = false)
    private Language language;
}