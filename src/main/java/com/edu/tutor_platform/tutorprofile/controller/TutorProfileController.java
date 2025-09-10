package com.edu.tutor_platform.tutorprofile.controller;

import com.edu.tutor_platform.tutorprofile.dto.TutorDto;
import com.edu.tutor_platform.tutorprofile.service.TutorProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/tutors")
public class TutorProfileController {

    private final TutorProfileService tutorProfileService;

    @Autowired
    public TutorProfileController(TutorProfileService tutorProfileService) {
        this.tutorProfileService = tutorProfileService;
    }

    @GetMapping("")
    public ResponseEntity<List<TutorDto>> getAllTutors() {
        List<TutorDto> tutors = tutorProfileService.getAllTutors();
        return ResponseEntity.ok(tutors);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TutorDto> updateTutor(@PathVariable String id, @RequestBody TutorDto tutorDto) {
        TutorDto tutor = tutorProfileService.updateTutorProfile(id, tutorDto);
        return new ResponseEntity<TutorDto>(tutor, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTutor(@PathVariable String id) {
        tutorProfileService.deleteTutorProfile(id);
        return ResponseEntity.noContent().build();
    }
}
