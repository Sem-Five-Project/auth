package com.edu.tutor_platform.language.repository;

import com.edu.tutor_platform.language.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Long> {
    
    Optional<Language> findByName(String name);
    boolean existsByName(String name);
}