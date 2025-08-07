package com.authsystem.repository;

import com.authsystem.entity.Preferences;
import com.authsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PreferencesRepository extends JpaRepository<Preferences, Long> {
    Optional<Preferences> findByUser(User user);
}
