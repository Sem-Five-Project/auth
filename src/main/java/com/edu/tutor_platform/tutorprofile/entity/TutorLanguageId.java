package com.edu.tutor_platform.tutorprofile.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TutorLanguageId implements Serializable {

    private Long tutorProfile;
    private Long language;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TutorLanguageId that = (TutorLanguageId) o;
        return Objects.equals(tutorProfile, that.tutorProfile) &&
               Objects.equals(language, that.language);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tutorProfile, language);
    }
}