package com.edu.tutor_platform.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnifiedSearchResponse {
    
    private List<SearchResult> results;
    private SearchMetadata metadata;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SearchResult {
        private String resultType;          // "tutor", "lesson", "monthly", "individual"
        private Long id;                    // tutor_id for tutors, class_id for classes
        private String title;               // Tutor name or class name
        private String description;         // Bio or class comment
        private BigDecimal rating;          // Tutor rating
        private Integer experienceMonths;   // Tutor experience in months
        private String subjectName;         // Subject name (for classes)
        private String tutorName;           // Tutor name (for classes)
        private String tutorFirstName;      // Tutor first name
        private String tutorLastName;       // Tutor last name
        private Integer maxDays;            // For lesson classes - number of days to complete
        private String schedule;            // Schedule information (day and time)
        private BigDecimal hourlyRate;      // Tutor's hourly rate
        private String classType;           // lesson/monthly/individual
        private Double relevanceScore;      // Search relevance score
        private String bio;                 // Tutor bio
        private BigDecimal classCompletionRate; // Tutor's class completion rate
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SearchMetadata {
        private Integer totalResults;       // Total number of results found
        private Integer currentPage;        // Current page number (0-based)
        private Integer totalPages;         // Total number of pages
        private String searchQuery;         // The search query that was used
        private Integer pageSize;           // Number of results per page
    }
}