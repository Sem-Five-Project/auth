package com.edu.tutor_platform.rating.controller;

import com.edu.tutor_platform.rating.dto.AddRatingRequest;
import com.edu.tutor_platform.rating.dto.RatingResponse;
import com.edu.tutor_platform.rating.dto.TutorRatingsSummary;
import com.edu.tutor_platform.rating.service.RatingService;
import com.edu.tutor_platform.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    /**
     * Add a new rating for a session
     */
    @PostMapping
    public ResponseEntity<RatingResponse> addRating(
            @Valid @RequestBody AddRatingRequest request,
            @AuthenticationPrincipal User currentUser) {

        RatingResponse response = ratingService.addRating(request, currentUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get all ratings for a specific tutor with pagination
     */
    @GetMapping("/tutor/{tutorId}")
    public ResponseEntity<List<RatingResponse>> getRatingsByTutor(
            @PathVariable Long tutorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<RatingResponse> ratings = ratingService.getRatingsByTutor(tutorId, page, size);
        return ResponseEntity.ok(ratings);
    }

    /**
     * Get ratings summary for a specific tutor
     */
    @GetMapping("/tutor/{tutorId}/summary")
    public ResponseEntity<TutorRatingsSummary> getTutorRatingsSummary(@PathVariable Long tutorId) {
        TutorRatingsSummary summary = ratingService.getTutorRatingsSummary(tutorId);
        return ResponseEntity.ok(summary);
    }

    /**
     * Get all ratings by the current student with pagination
     */
    @GetMapping("/my-ratings")
    public ResponseEntity<List<RatingResponse>> getMyRatings(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<RatingResponse> ratings = ratingService.getRatingsByStudent(currentUser, page, size);
        return ResponseEntity.ok(ratings);
    }

    /**
     * Get a specific rating by ID
     */
    @GetMapping("/{ratingId}")
    public ResponseEntity<RatingResponse> getRatingById(@PathVariable Long ratingId) {
        RatingResponse rating = ratingService.getRatingById(ratingId);
        return ResponseEntity.ok(rating);
    }

    /**
     * Delete a rating (only by the student who created it)
     */
    @DeleteMapping("/{ratingId}")
    public ResponseEntity<Void> deleteRating(
            @PathVariable Long ratingId,
            @AuthenticationPrincipal User currentUser) {

        ratingService.deleteRating(ratingId, currentUser);
        return ResponseEntity.noContent().build();
    }
}