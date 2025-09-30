package com.edu.tutor_platform.tutorprofile.controller;

import com.edu.tutor_platform.tutorprofile.dto.*;
import com.edu.tutor_platform.tutorprofile.service.TutorProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    @GetMapping("")
    public ResponseEntity<List<TutorsDto>> getAllTutors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<TutorsDto> tutors = tutorProfileService.getAllTutors(page, size);
        return ResponseEntity.ok(tutors);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    @GetMapping({"/{id}"})
    public ResponseEntity<TutorDto> getTutorById(@PathVariable String id) {
        TutorDto tutor = tutorProfileService.getTutorDetailsById(Long.parseLong(id));
        return ResponseEntity.ok(tutor);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    @GetMapping("/searchByAdmin")
    public ResponseEntity<List<TutorsDto>> searchTutorsByAdmin(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Long tutorId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean verified,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {


        List<TutorsDto> tutors = tutorProfileService.searchTutorsByAdmin(
                name, username, email, tutorId, status, verified, page, size
        );

        return ResponseEntity.ok(tutors);
    }

    @GetMapping("/statistics")
    public ResponseEntity<TutorStatsDto> getTutorStatistics() {
        TutorStatsDto stats = tutorProfileService.getTutorStatistics();
        return ResponseEntity.ok(stats);
    }



    @PutMapping("/{id}/admin")
    public ResponseEntity<TutorDto> adminUpdateTutor(@PathVariable String id, @RequestBody TutorDto tutorDto) {
        TutorDto tutor = tutorProfileService.adminUpdateTutorProfile(id, tutorDto);
        return new ResponseEntity<>(tutor, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTutor(@PathVariable String id) {
        tutorProfileService.deleteTutorProfile(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending-approvals")
    public ResponseEntity<List<TutorApprovalsDto>> getPendingTutorApprovals() {
        List<TutorApprovalsDto> pendingApprovals = tutorProfileService.getPendingTutorApprovals();
        return ResponseEntity.ok(pendingApprovals);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/reverification-requests")
    public ResponseEntity<List<ReverificationsDto>> getReverificationRequests() {
        List<ReverificationsDto> requests = tutorProfileService.getReverificationRequests();
        return ResponseEntity.ok(requests);
    }


}
