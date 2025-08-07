package com.authsystem.service;

import com.authsystem.entity.Preferences;
import com.authsystem.entity.User;
import com.authsystem.repository.PreferencesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PreferencesService {
    @Autowired
    private PreferencesRepository preferencesRepository;

    public Preferences getPreferences(User user) {
        return preferencesRepository.findByUser(user).orElseGet(() -> {
            Preferences prefs = new Preferences();
            prefs.setUser(user);
            return preferencesRepository.save(prefs);
        });
    }

    public Preferences setOrUpdatePreferences(User user, Preferences newPrefs) {
        Preferences prefs = preferencesRepository.findByUser(user).orElse(new Preferences());
        prefs.setUser(user);
        prefs.setAge(newPrefs.getAge());
        prefs.setLanguage(newPrefs.getLanguage());
        prefs.setSubjects(newPrefs.getSubjects());
        return preferencesRepository.save(prefs);
    }
}
