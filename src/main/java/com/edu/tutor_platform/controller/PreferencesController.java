package com.authsystem.controller;

import com.authsystem.entity.Preferences;
import com.authsystem.entity.User;
import com.authsystem.service.PreferencesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/preferences")
public class PreferencesController {
    @Autowired
    private PreferencesService preferencesService;

    @GetMapping
    public Preferences getPreferences(@AuthenticationPrincipal User user) {
        return preferencesService.getPreferences(user);
    }

    @PutMapping("/setPreferences")
    public Preferences setPreferences(@AuthenticationPrincipal User user, @RequestBody Preferences prefs) {
        if (user == null) {
            System.out.println("user NULL");
            throw new RuntimeException("Unauthorized: User not found in token");
        }
        System.out.println("Setting preferences for user: " + user.getUsername());
        System.out.println("Preferences: " + prefs);
        return preferencesService.setOrUpdatePreferences(user, prefs);
    }

    @PutMapping
    public Preferences updatePreferences(@AuthenticationPrincipal User user, @RequestBody Preferences prefs) {
        return preferencesService.setOrUpdatePreferences(user, prefs);
    }
}
