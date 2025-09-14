package com.edu.tutor_platform.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnifiedSearchRequest {
    
    // Search query
    private String query;                    // Main search term (e.g., "chemistry", "John Smith", "his")
    
    // Tutor filters
    private Double minRating;               // Filter: minimum tutor rating (0.0 - 5.0)
    private Integer minExperience;          // Filter: minimum experience in months
    private Double minCompletionRate;       // Filter: minimum class completion rate (0.0 - 100.0)
    private String tutorName;               // Filter: tutor name (first name or last name)
    private String name;                    // Alternative field for tutor name
    
    // Subject and pricing filters
    private String subject;                 // Filter: subject name (e.g., "Mathematics", "History")
    private List<Long> subjectIds;          // Filter: specific subject IDs
    private Double minPrice;                // Filter: minimum hourly rate
    private Double maxPrice;                // Filter: maximum hourly rate
    
    // Class type filters
    private List<String> classTypes;        // Filter: ["lesson", "monthly", "individual"]
    
    // Sorting options
    private String sortBy;                  // "rating", "experience", "price", "completion_rate", "relevance"
    private String sortOrder;               // "asc", "desc"
    
    // Pagination
    private Integer page;                   // Pagination - page number (0-based)
    private Integer size;                   // Page size (default: 20)
    
    // Search type
    private String searchType;              // "tutors", "slots", "both" (default: "both")
    
    // Getter methods with default values
    public String getQuery() {
        return query != null ? query.trim() : "";
    }
    
    public Double getMinRating() {
        return minRating != null ? minRating : 0.0;
    }
    
    public Integer getMinExperience() {
        return minExperience != null ? minExperience : 0;
    }
    
    public Double getMinCompletionRate() {
        return minCompletionRate != null ? minCompletionRate : 0.0;
    }
    
    public String getTutorName() {
        // Use either tutorName or name field
        if (tutorName != null && !tutorName.trim().isEmpty()) {
            return tutorName.trim();
        }
        return name != null ? name.trim() : "";
    }
    
    public String getSubject() {
        return subject != null ? subject.trim() : "";
    }
    
    public Double getMinPrice() {
        return minPrice != null ? minPrice : 0.0;
    }
    
    public Double getMaxPrice() {
        return maxPrice != null ? maxPrice : Double.MAX_VALUE;
    }
    
    public String getSortBy() {
        return sortBy != null ? sortBy : "relevance";
    }
    
    public String getSortOrder() {
        return sortOrder != null ? sortOrder : "desc";
    }
    
    public Integer getPage() {
        return page != null ? page : 0;
    }
    
    public Integer getSize() {
        return size != null ? size : 20;
    }
    
    public String getSearchType() {
        return searchType != null ? searchType : "both";
    }
}