// package com.edu.tutor_platform.tutorsearch.dto;

// import java.util.List;

// public class TutorSearchDTO {
//     private Long tutorId;
//     private String firstName;
//     private String lastName;
//     private String profileImage;
//     private Double rating;
//     private Integer experienceInMonths;
//     private Double classCompletionRate;
//     private List<SubjectDTO> subjects;

//     // Constructors, Getters, and Setters
// }

// // Create a nested DTO for subject details
// class SubjectDTO {
//     private Long subjectId;
//     private String subjectName;
//     private Double hourlyRate;
//     private String language;
    
//     // Constructors, Getters, and Setters
// }
package com.edu.tutor_platform.tutorsearch.dto;

import java.util.List;

public class TutorSearchDTO {
    private Long tutorId;
    private String firstName;
    private String lastName;
    private String profileImage;
    private Double rating;
    private Integer experienceInMonths;
    private Double classCompletionRate;
    private List<SubjectDTO> subjects;

    public TutorSearchDTO() {}

    public TutorSearchDTO(Long tutorId, String firstName, String lastName, String profileImage,
                          Double rating, Integer experienceInMonths, Double classCompletionRate,
                          List<SubjectDTO> subjects) {
        this.tutorId = tutorId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profileImage = profileImage;
        this.rating = rating;
        this.experienceInMonths = experienceInMonths;
        this.classCompletionRate = classCompletionRate;
        this.subjects = subjects;
    }

    // Getters and Setters
    public Long getTutorId() { return tutorId; }
    public void setTutorId(Long tutorId) { this.tutorId = tutorId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public Integer getExperienceInMonths() { return experienceInMonths; }
    public void setExperienceInMonths(Integer experienceInMonths) { this.experienceInMonths = experienceInMonths; }

    public Double getClassCompletionRate() { return classCompletionRate; }
    public void setClassCompletionRate(Double classCompletionRate) { this.classCompletionRate = classCompletionRate; }

    public List<SubjectDTO> getSubjects() { return subjects; }
    public void setSubjects(List<SubjectDTO> subjects) { this.subjects = subjects; }

    // Nested DTO
    public static class SubjectDTO {
        private Long subjectId;
        private String subjectName;
        private Double hourlyRate;
        private String language;

        public SubjectDTO() {}

        public SubjectDTO(Long subjectId, String subjectName, Double hourlyRate, String language) {
            this.subjectId = subjectId;
            this.subjectName = subjectName;
            this.hourlyRate = hourlyRate;
            this.language = language;
        }

        public Long getSubjectId() { return subjectId; }
        public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

        public String getSubjectName() { return subjectName; }
        public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

        public Double getHourlyRate() { return hourlyRate; }
        public void setHourlyRate(Double hourlyRate) { this.hourlyRate = hourlyRate; }

        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }
    }
}
