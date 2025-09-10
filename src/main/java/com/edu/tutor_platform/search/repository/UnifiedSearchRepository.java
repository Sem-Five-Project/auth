package com.edu.tutor_platform.search.repository;

import com.edu.tutor_platform.search.dto.UnifiedSearchResponse;
import com.edu.tutor_platform.tutorprofile.entity.TutorProfile;
import com.edu.tutor_platform.tutorprofile.repository.TutorProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class UnifiedSearchRepository {
    
    private final TutorProfileRepository tutorProfileRepository;
    
    /**
     * Performs unified search using JPA repository methods
     */
    public List<UnifiedSearchResponse.SearchResult> performUnifiedSearch(
            String query, Double minRating, Integer minExperience, 
            List<String> classTypes, List<Long> subjectIds,
            String sortBy, String sortOrder, Integer page, Integer size) {
        
        List<UnifiedSearchResponse.SearchResult> results = new ArrayList<>();
        
        // Create sort and pageable
        Sort sort = createSort(sortBy, sortOrder);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Convert parameters
        String searchQuery = query != null ? query.trim() : "";
        BigDecimal minRatingBd = BigDecimal.valueOf(minRating != null ? minRating : 0.0);
        Integer minExp = minExperience != null ? minExperience : 0;
        
        // Search tutors
        Page<TutorProfile> tutorResults;
        if (searchQuery.isEmpty()) {
            // If no search query, get all tutors with filters
            tutorResults = tutorProfileRepository.searchTutors("", minRatingBd, minExp, pageable);
        } else {
            tutorResults = tutorProfileRepository.searchTutors(searchQuery, minRatingBd, minExp, pageable);
        }
        
        // Convert tutors to search results
        for (TutorProfile tutor : tutorResults.getContent()) {
            results.add(mapTutorToSearchResult(tutor, searchQuery));
        }
        
        return results;
    }
    
    /**
     * Creates appropriate sort for the query
     */
    private Sort createSort(String sortBy, String sortOrder) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
        
        switch (sortBy.toLowerCase()) {
            case "rating":
                return Sort.by(direction, "rating");
            case "experience":
                return Sort.by(direction, "experienceInMonths");
            case "name":
            case "class_name":
                return Sort.by(direction, "user.firstName", "user.lastName");
            case "relevance":
            default:
                return Sort.by(Sort.Direction.DESC, "rating", "experienceInMonths"); // Default sort
        }
    }
    
    /**
     * Maps TutorProfile entity to SearchResult DTO
     */
    private UnifiedSearchResponse.SearchResult mapTutorToSearchResult(TutorProfile tutor, String query) {
        double relevanceScore = calculateTutorRelevanceScore(tutor, query);
        
        return UnifiedSearchResponse.SearchResult.builder()
                .resultType("tutor")
                .id(tutor.getTutorId())
                .title(tutor.getUser().getFirstName() + " " + tutor.getUser().getLastName())
                .description(tutor.getBio())
                .rating(tutor.getRating())
                .experienceMonths(tutor.getExperienceInMonths())
                .subjectName(null)
                .tutorName(tutor.getUser().getFirstName() + " " + tutor.getUser().getLastName())
                .tutorFirstName(tutor.getUser().getFirstName())
                .tutorLastName(tutor.getUser().getLastName())
                .maxDays(null)
                .schedule(null)
                .hourlyRate(tutor.getHourlyRate())
                .classType("tutor")
                .relevanceScore(relevanceScore)
                .bio(tutor.getBio())
                .classCompletionRate(tutor.getClassCompletionRate())
                .build();
    }
    
    /**
     * Calculates relevance score for tutor based on query match
     */
    private double calculateTutorRelevanceScore(TutorProfile tutor, String query) {
        if (query == null || query.trim().isEmpty()) {
            return 50.0; // Default score when no query
        }
        
        String queryLower = query.toLowerCase();
        String fullName = (tutor.getUser().getFirstName() + " " + tutor.getUser().getLastName()).toLowerCase();
        String bio = tutor.getBio() != null ? tutor.getBio().toLowerCase() : "";
        
        // Higher score for exact matches
        if (fullName.equals(queryLower)) {
            return 100.0; // Exact name match
        } else if (fullName.contains(queryLower)) {
            return 90.0; // Partial name match
        } else if (bio.contains(queryLower)) {
            return 75.0; // Bio match
        } else {
            return 60.0; // Default match score
        }
    }
    
    /**
     * Gets total count for pagination
     */
    public Integer getTotalSearchResults(String query, Double minRating, Integer minExperience,
                                       List<String> classTypes, List<Long> subjectIds) {
        
        String searchQuery = query != null ? query.trim() : "";
        BigDecimal minRatingBd = BigDecimal.valueOf(minRating != null ? minRating : 0.0);
        Integer minExp = minExperience != null ? minExperience : 0;
        
        Long count = tutorProfileRepository.countSearchResults(searchQuery, minRatingBd, minExp);
        return count.intValue();
    }
    
    /**
     * Gets search suggestions using JPA queries
     */
    public List<String> getSearchSuggestions(String query, Integer limit) {
        if (query == null || query.trim().length() < 2) {
            return List.of();
        }
        
        // Get tutor name suggestions
        Pageable pageable = PageRequest.of(0, limit);
        List<String> tutorSuggestions = tutorProfileRepository.findTutorNameSuggestions(query, pageable);
        
        // TODO: Add subject and class name suggestions when entities are ready
        List<String> allSuggestions = new ArrayList<>(tutorSuggestions);
        
        // Add some sample subject suggestions for demonstration
        if (query.toLowerCase().startsWith("chem")) {
            allSuggestions.add("Chemistry");
            allSuggestions.add("Chemical Bonds Lesson");
        } else if (query.toLowerCase().startsWith("phys")) {
            allSuggestions.add("Physics");
            allSuggestions.add("Physics Sound Waves Class");
        } else if (query.toLowerCase().startsWith("math")) {
            allSuggestions.add("Mathematics");
            allSuggestions.add("Calculus Class");
        }
        
        return allSuggestions.stream()
                .distinct()
                .limit(limit)
                .toList();
    }
    
    /**
     * Gets available subjects for filter options
     */
    public List<Map<String, Object>> getAvailableSubjects() {
        // Sample data - replace with actual subject repository when available
        List<Map<String, Object>> subjects = new ArrayList<>();
        
        Map<String, Object> math = new HashMap<>();
        math.put("subject_id", 1L);
        math.put("name", "Mathematics");
        math.put("class_count", 15);
        subjects.add(math);
        
        Map<String, Object> physics = new HashMap<>();
        physics.put("subject_id", 2L);
        physics.put("name", "Physics");
        physics.put("class_count", 12);
        subjects.add(physics);
        
        Map<String, Object> chemistry = new HashMap<>();
        chemistry.put("subject_id", 3L);
        chemistry.put("name", "Chemistry");
        chemistry.put("class_count", 8);
        subjects.add(chemistry);
        
        return subjects;
    }
}