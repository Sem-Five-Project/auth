package com.edu.tutor_platform.search.controller;

import com.edu.tutor_platform.search.dto.UnifiedSearchRequest;
import com.edu.tutor_platform.search.dto.UnifiedSearchResponse;
import com.edu.tutor_platform.search.service.UnifiedSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"}, allowCredentials = "true")
@RequiredArgsConstructor
public class UnifiedSearchController {
    
    private final UnifiedSearchService unifiedSearchService;
    
    /**
     * Main unified search endpoint that searches across tutors and slots
     *
     * @param request Search parameters including query, filters, and pagination
     * @return Unified search results with tutors and available slots
     */
    @PostMapping("/unified")
    public ResponseEntity<UnifiedSearchResponse> unifiedSearch(
            @RequestBody UnifiedSearchRequest request) {
        
        UnifiedSearchResponse response = unifiedSearchService.performUnifiedSearch(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET version with comprehensive filters for tutor and slot search
     * Supports all filter parameters as query params for easy frontend integration
     *
     * @param query Search term (matches tutor names, subjects, class names)
     * @param minRating Minimum tutor rating (0.0 - 5.0)
     * @param minExperience Minimum experience in months
     * @param minCompletionRate Minimum class completion rate (0.0 - 100.0)
     * @param tutorName Filter by tutor name (first or last name)
     * @param name Alternative parameter for tutor name
     * @param subject Filter by subject name
     * @param minPrice Minimum hourly rate
     * @param maxPrice Maximum hourly rate
     * @param sortBy Sort field: rating, experience, price, completion_rate, relevance
     * @param sortOrder Sort order: asc, desc
     * @param searchType Search type: tutors, slots, both
     * @param page Page number (0-based)
     * @param size Page size
     * @return Unified search results
     */
    @GetMapping("/unified")
    public ResponseEntity<UnifiedSearchResponse> unifiedSearchGet(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Double minCompletionRate,
            @RequestParam(required = false) String tutorName,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "relevance") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(defaultValue = "both") String searchType,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
            System.out.println("Reached here111");
        
        UnifiedSearchRequest request = UnifiedSearchRequest.builder()
                .query(query)
                .minRating(minRating)
                .minExperience(minExperience)
                .minCompletionRate(minCompletionRate)
                .tutorName(tutorName != null ? tutorName : name) // Support both parameter names
                .subject(subject)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .sortBy(sortBy)
                .sortOrder(sortOrder)
                .searchType(searchType)
                .page(page)
                .size(size)
                .build();
        
        UnifiedSearchResponse response = unifiedSearchService.performUnifiedSearch(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Autocomplete/suggestions endpoint for predictive search
     * Returns suggestions for tutors, subjects, and classes as user types
     * 
     * @param query Partial search term (e.g., "chem")
     * @return List of suggestions (e.g., ["Chemistry", "Chemical Bonds Lesson", "Dr. Chemistry Tutor"])
     */
    @GetMapping("/suggestions")
    public ResponseEntity<List<String>> getSearchSuggestions(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") Integer limit) {
        
        List<String> suggestions = unifiedSearchService.getSearchSuggestions(query, limit);
        return ResponseEntity.ok(suggestions);
    }
    
    /**
     * Get available filter options for the frontend
     * 
     * @return Available subjects, class types, etc.
     */
    @GetMapping("/filter-options")
    public ResponseEntity<?> getFilterOptions() {
        var filterOptions = unifiedSearchService.getFilterOptions();
        return ResponseEntity.ok(filterOptions);
    }
}