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
@CrossOrigin(origins = {"http://localhost:5173"}, allowCredentials = "true")
@RequiredArgsConstructor
public class UnifiedSearchController {
    
    private final UnifiedSearchService unifiedSearchService;
    
    /**
     * Main unified search endpoint that searches across tutors and classes
     * 
     * @param request Search parameters including query, filters, and pagination
     * @return Unified search results with both tutors and classes
     */
    @PostMapping("/unified")
    public ResponseEntity<UnifiedSearchResponse> unifiedSearch(
            @RequestBody UnifiedSearchRequest request) {
        
        UnifiedSearchResponse response = unifiedSearchService.performUnifiedSearch(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET version for simple searches without filters
     * 
     * @param query Search term
     * @param page Page number (default: 0)
     * @param size Page size (default: 20)
     * @return Unified search results
     */
    @GetMapping("/unified")
    public ResponseEntity<UnifiedSearchResponse> unifiedSearchGet(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        
        UnifiedSearchRequest request = UnifiedSearchRequest.builder()
                .query(query)
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