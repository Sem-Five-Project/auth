package com.edu.tutor_platform.search.service;

import com.edu.tutor_platform.search.dto.UnifiedSearchRequest;
import com.edu.tutor_platform.search.dto.UnifiedSearchResponse;
import com.edu.tutor_platform.search.repository.OptimizedSearchRepository;
import com.edu.tutor_platform.subject.entity.Subject;
import com.edu.tutor_platform.subject.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UnifiedSearchService {
    
    private final OptimizedSearchRepository optimizedSearchRepository;
    private final SubjectRepository subjectRepository;
    
    /**
     * Performs unified search across tutors and classes
     * 
     * @param request Search parameters including query, filters, and pagination
     * @return Unified search results with metadata
     */
    public UnifiedSearchResponse performUnifiedSearch(UnifiedSearchRequest request) {
        log.info("Performing optimized unified search with request: {}", request);
        
        try {
            // Convert subject name to subject ID if provided
            Integer subjectId = null;
            if (request.getSubject() != null && !request.getSubject().isEmpty()) {
                subjectId = getSubjectIdByName(request.getSubject());
                log.debug("Converted subject '{}' to ID: {}", request.getSubject(), subjectId);
            }
            
            // Determine sort field mapping
            String sortBy = mapSortField(request.getSortBy());
            Boolean sortAsc = "asc".equalsIgnoreCase(request.getSortOrder());
            
            // Use the optimized stored procedure
            UnifiedSearchResponse response = optimizedSearchRepository.findTutorsOptimized(
                request.getMinRating(),
                request.getMinExperience(),
                request.getMinCompletionRate(),
                subjectId,
                request.getMinPrice(),
                request.getMaxPrice(),
                buildSearchKeyword(request),
                sortBy,
                sortAsc,
                request.getPage(),
                request.getSize()
            );
            
            log.info("Optimized search completed successfully");
            return response;
            
        } catch (Exception e) {
            log.error("Error in performUnifiedSearch: {}", e.getMessage(), e);
            throw new RuntimeException("Search operation failed", e);
        }
    }
    
    /**
     * Build search keyword from multiple possible fields
     */
    private String buildSearchKeyword(UnifiedSearchRequest request) {
        StringBuilder keyword = new StringBuilder();
        
        if (request.getQuery() != null && !request.getQuery().trim().isEmpty()) {
            keyword.append(request.getQuery().trim());
        }
        
        if (request.getTutorName() != null && !request.getTutorName().isEmpty()) {
            if (keyword.length() > 0) keyword.append(" ");
            keyword.append(request.getTutorName().trim());
        }
        
        return keyword.toString();
    }
    
    /**
     * Map frontend sort field to database column names
     */
    private String mapSortField(String sortBy) {
        if (sortBy == null) return "rating";
        
        return switch (sortBy.toLowerCase()) {
            case "rating" -> "rating";
            case "experience" -> "experience";
            case "price" -> "price";
            case "completion_rate" -> "completion_rate";
            case "relevance" -> "relevance";
            default -> "rating";
        };
    }
    
    /**
     * Get subject ID by name for stored procedure
     */
    private Integer getSubjectIdByName(String subjectName) {
        try {
            Optional<Subject> subject = subjectRepository.findByNameIgnoreCase(subjectName);
            return subject.map(s -> s.getSubjectId().intValue()).orElse(null);
        } catch (Exception e) {
            log.warn("Could not find subject by name '{}': {}", subjectName, e.getMessage());
            return null;
        }
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
        
        try {
            String suggestionsJson = optimizedSearchRepository.getSearchSuggestionsOptimized(query.trim(), limit);
            
            // Parse JSON array to List<String>
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.readValue(suggestionsJson,
                    mapper.getTypeFactory().constructCollectionType(List.class, String.class));
                    
        } catch (Exception e) {
            log.error("Error getting search suggestions: {}", e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Gets available filter options for the frontend
     * 
     * @return Map containing available subjects, class types, etc.
     */
    public Map<String, Object> getFilterOptions() {
        Map<String, Object> filterOptions = new HashMap<>();
        
        try {
            // Get available subjects from database
            List<Subject> subjects = subjectRepository.findAll();
            List<Map<String, Object>> subjectOptions = subjects.stream()
                    .map(subject -> {
                        Map<String, Object> option = new HashMap<>();
                        option.put("subject_id", subject.getSubjectId());
                        option.put("name", subject.getName());
                        return option;
                    })
                    .toList();
            filterOptions.put("subjects", subjectOptions);
            
        } catch (Exception e) {
            log.error("Error fetching subjects: {}", e.getMessage());
            filterOptions.put("subjects", List.of());
        }
        
        // Get available class types
        List<String> classTypes = List.of("lesson", "monthly", "individual");
        filterOptions.put("classTypes", classTypes);
        
        // Get rating ranges
        Map<String, Object> ratingOptions = new HashMap<>();
        ratingOptions.put("min", 0.0);
        ratingOptions.put("max", 5.0);
        ratingOptions.put("step", 0.5);
        filterOptions.put("rating", ratingOptions);
        
        // Get experience ranges (in months)
        Map<String, Object> experienceOptions = new HashMap<>();
        experienceOptions.put("min", 0);
        experienceOptions.put("max", 120); // 10 years max
        experienceOptions.put("step", 6);   // 6-month intervals
        filterOptions.put("experience", experienceOptions);
        
        // Get enhanced sort options
        List<Map<String, String>> sortOptions = List.of(
            Map.of("value", "relevance", "label", "Most Relevant"),
            Map.of("value", "rating", "label", "Rating"),
            Map.of("value", "experience", "label", "Experience"),
            Map.of("value", "price", "label", "Price"),
            Map.of("value", "completion_rate", "label", "Completion Rate"),
            Map.of("value", "class_name", "label", "Alphabetical")
        );
        filterOptions.put("sortOptions", sortOptions);
        
        // Get price ranges
        Map<String, Object> priceOptions = new HashMap<>();
        priceOptions.put("min", 0.0);
        priceOptions.put("max", 10000.0); // Max hourly rate in LKR
        priceOptions.put("step", 100.0);   // Price step intervals
        filterOptions.put("price", priceOptions);
        
        // Get completion rate ranges
        Map<String, Object> completionRateOptions = new HashMap<>();
        completionRateOptions.put("min", 0.0);
        completionRateOptions.put("max", 100.0);
        completionRateOptions.put("step", 5.0);
        filterOptions.put("completionRate", completionRateOptions);
        
        // Get search type options
        List<Map<String, String>> searchTypeOptions = List.of(
            Map.of("value", "both", "label", "Tutors & Slots"),
            Map.of("value", "tutors", "label", "Tutors Only"),
            Map.of("value", "slots", "label", "Available Slots Only")
        );
        filterOptions.put("searchTypes", searchTypeOptions);
        
        return filterOptions;
    }
}