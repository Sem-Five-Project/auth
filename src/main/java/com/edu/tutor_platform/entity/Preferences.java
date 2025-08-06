package com.authsystem.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Preferences {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String age;
    private String language;

    @ElementCollection
    private List<String> subjects;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    // Constructors
    public Preferences() {}

    public Preferences(String age, String language, List<String> subjects, User user) {
        this.age = age;
        this.language = language;
        this.subjects = subjects;
        this.user = user;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
