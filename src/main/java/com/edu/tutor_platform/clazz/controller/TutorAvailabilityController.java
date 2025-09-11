package com.edu.tutor_platform.clazz.controller;

import com.edu.tutor_platform.booking.entity.TutorAvailability;
import com.edu.tutor_platform.clazz.service.TutorAvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import com.edu.tutor_platform.clazz.dto.TutorAvailabilityDTO;

@RestController
@RequestMapping("/tutor-availability")
public class TutorAvailabilityController {
    @Autowired
    private TutorAvailabilityService service;

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<TutorAvailability> create(@RequestBody TutorAvailability availability) {
        return ResponseEntity.ok(service.createAvailability(availability));
    }

    @DeleteMapping("/delete/{availabilityId}")
    public ResponseEntity<Void> delete(@PathVariable Long availabilityId) {
        service.deleteAvailability(availabilityId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/update/{availabilityId}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<TutorAvailability> update(@PathVariable Long availabilityId,
            @RequestBody TutorAvailability updated) {
        TutorAvailability result = service.updateAvailability(availabilityId, updated);
        if (result == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/tutor/{tutorId}")
    public ResponseEntity<List<TutorAvailabilityDTO>> getByTutor(@PathVariable Long tutorId) {
        List<TutorAvailability> entities = service.getByTutorId(tutorId);
        List<TutorAvailabilityDTO> dtos = entities.stream().map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    private TutorAvailabilityDTO toDTO(TutorAvailability entity) {
        TutorAvailabilityDTO dto = new TutorAvailabilityDTO();
        dto.setAvailabilityId(entity.getAvailabilityId());
        dto.setDayOfWeek(entity.getDayOfWeek().toString());
        dto.setStartTime(entity.getStartTime());
        dto.setEndTime(entity.getEndTime());
        dto.setRecurring(entity.getRecurring());
        return dto;
    }
}
