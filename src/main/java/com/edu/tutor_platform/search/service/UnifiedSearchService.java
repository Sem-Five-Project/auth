package com.edu.tutor_platform.search.service;

import com.edu.tutor_platform.search.dto.UnifiedSearchRequest;
import com.edu.tutor_platform.search.dto.UnifiedSearchResponse;
import com.edu.tutor_platform.search.repository.UnifiedSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UnifiedSearchService {
    
    private final UnifiedSearchRepository unifiedSearchRepository;
    
    /**
     * Performs unified search across tutors and classes
     * 
     * @param request Search parameters including query, filters, and pagination
     * @return Unified search results with metadata
     */
    public UnifiedSearchResponse performUnifiedSearch(UnifiedSearchRequest request) {
        // Get search results
        List<UnifiedSearchResponse.SearchResult> results = 
            unifiedSearchRepository.performUnifiedSearch(
                request.getQuery(), 
                request.getMinRating(), 
                request.getMinExperienceMonths(), 
                request.getClassTypes(), 
                request.getSubjectIds(), 
                request.getSortBy(), 
                request.getSortOrder(), 
                request.getPage(), 
                request.getSize()
            );
        
        // Get total count for pagination
        Integer totalResults = unifiedSearchRepository.getTotalSearchResults(
            request.getQuery(), 
            request.getMinRating(), 
            request.getMinExperienceMonths(), 
            request.getClassTypes(), 
            request.getSubjectIds()
        );
        
        // Calculate pagination metadata
        int totalPages = (int) Math.ceil((double) totalResults / request.getSize());
        
        // Build metadata
        UnifiedSearchResponse.SearchMetadata metadata = UnifiedSearchResponse.SearchMetadata.builder()
                .totalResults(totalResults)
                .currentPage(request.getPage())
                .totalPages(totalPages)
                .searchQuery(request.getQuery())
                .pageSize(request.getSize())
                .build();
        
        // Build response
        return UnifiedSearchResponse.builder()
                .results(results)
                .metadata(metadata)
                .build();
    }
    
    /**
     * Gets search suggestions for autocomplete functionality
     * 
     * @param query Partial search term
     * @param limit Maximum number of suggestions
     * @return List of suggestions
     */
    public List<String> getSearchSuggestions(String query, Integer limit) {
        if (query == null || query.trim().length() < 2) {
            return List.of(); // Return empty list for very short queries
        }
        
        return unifiedSearchRepository.getSearchSuggestions(query.trim(), limit);
    }
    
    /**
     * Gets available filter options for the frontend
     * 
     * @return Map containing available subjects, class types, etc.
     */
    public Map<String, Object> getFilterOptions() {
        Map<String, Object> filterOptions = new HashMap<>();
        
        // Get available subjects
        List<Map<String, Object>> subjects = unifiedSearchRepository.getAvailableSubjects();
        filterOptions.put("subjects", subjects);
        
        // Get available class types
        List<String> classTypes = List.of("lesson", "monthly", "individual");
        filterOptions.put("classTypes", classTypes);
        
        // Get rating ranges
        Map<String, Object> ratingOptions = new HashMap<>();
        ratingOptions.put("min", 0.0);
        ratingOptions.put("max", 5.0);
        ratingOptions.put("step", 0.5);
        filterOptions.put("rating", ratingOptions);
        
        // Get experience ranges
        Map<String, Object> experienceOptions = new HashMap<>();
        experienceOptions.put("min", 0);
        experienceOptions.put("max", 120); // 10 years max
        experienceOptions.put("step", 6);   // 6-month intervals
        filterOptions.put("experience", experienceOptions);
        
        // Get sort options
        List<Map<String, String>> sortOptions = List.of(
            Map.of("value", "relevance", "label", "Most Relevant"),
            Map.of("value", "rating", "label", "Highest Rated"),
            Map.of("value", "experience", "label", "Most Experienced"),
            Map.of("value", "class_name", "label", "Alphabetical")
        );
        filterOptions.put("sortOptions", sortOptions);
        
        return filterOptions;
    }
}