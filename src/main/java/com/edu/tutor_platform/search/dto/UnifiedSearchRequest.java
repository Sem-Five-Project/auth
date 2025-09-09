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
    
    private String query;                    // Main search term (e.g., "chemistry", "John Smith", "sound waves")
    private Double minRating;               // Filter: minimum tutor rating
    private Integer minExperienceMonths;    // Filter: minimum experience in months
    private List<String> classTypes;        // Filter: ["lesson", "monthly", "individual"]
    private List<Long> subjectIds;          // Filter: specific subject IDs
    private String sortBy;                  // "rating", "experience", "class_name", "relevance"
    private String sortOrder;               // "asc", "desc"
    private Integer page;                   // Pagination - page number (0-based)
    private Integer size;                   // Page size (default: 20)
    
    // Set default values
    public String getQuery() {
        return query != null ? query.trim() : "";
    }
    
    public Double getMinRating() {
        return minRating != null ? minRating : 0.0;
    }
    
    public Integer getMinExperienceMonths() {
        return minExperienceMonths != null ? minExperienceMonths : 0;
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
}