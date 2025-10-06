package com.edu.tutor_platform.clazz.controller;

import com.edu.tutor_platform.booking.entity.TutorAvailability;
import com.edu.tutor_platform.clazz.service.TutorAvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tutor-availability")
public class TutorAvailabilityController {
    @Autowired
    private TutorAvailabilityService service;

    @PostMapping
    public ResponseEntity<TutorAvailability> create(@RequestBody TutorAvailability availability) {
        return ResponseEntity.ok(service.createAvailability(availability));
    }

    @DeleteMapping("/delete/{availabilityId}")
    public ResponseEntity<Void> delete(@PathVariable Long availabilityId) {
        service.deleteAvailability(availabilityId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update/{availabilityId}")
    public ResponseEntity<TutorAvailability> update(@PathVariable Long availabilityId,
            @RequestBody TutorAvailability updated) {
        TutorAvailability result = service.updateAvailability(availabilityId, updated);
        if (result == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/tutor/{tutorId}")
    public ResponseEntity<List<TutorAvailability>> getByTutor(@PathVariable Integer tutorId) {
        return ResponseEntity.ok(service.getByTutorId(tutorId));
    }
}
