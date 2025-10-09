package com.edu.tutor_platform.search.repository;

import com.edu.tutor_platform.search.dto.UnifiedSearchResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class OptimizedSearchRepository {
    
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    
    /**
     * High-performance tutor search using stored procedure
     * Returns JSON directly from database, minimizing server processing
     */
    public UnifiedSearchResponse findTutorsOptimized(
            Double minRating,
            Integer minExperience, 
            Double minCompletionRate,
            Integer subjectId,
            Double minPrice,
            Double maxPrice,
            String searchKeyword,
            String sortBy,
            Boolean sortAsc,
            Integer page,
            Integer pageSize) {
        
        log.info("Executing optimized search: keyword={}, sortBy={}, sortAsc={}, page={}", 
                searchKeyword, sortBy, sortAsc, page);
        
        // Build the SQL call to the stored procedure
        String sql = "SELECT find_tutors(:minRating, :minExperience, :minCompletionRate, " +
                     ":subjectId, :minPrice, :maxPrice, :searchKeyword, :sortBy, :sortAsc, :page, :pageSize)";
        
        // Prepare parameters with null handling
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("minRating", minRating)
                .addValue("minExperience", minExperience)
                .addValue("minCompletionRate", minCompletionRate)
                .addValue("subjectId", subjectId)
                .addValue("minPrice", minPrice)
                .addValue("maxPrice", maxPrice)
                .addValue("searchKeyword", searchKeyword != null ? searchKeyword : "")
                .addValue("sortBy", sortBy != null ? sortBy : "rating")
                .addValue("sortAsc", sortAsc != null ? sortAsc : false)
                .addValue("page", page != null ? page : 1)
                .addValue("pageSize", pageSize != null ? pageSize : 20);
        
        try {
            // Execute the stored procedure and get JSON result
            String resultJson = jdbcTemplate.queryForObject(sql, params, String.class);
            System.out.println("Reached here000");
            if (resultJson == null) {
                log.warn("Stored procedure returned null result");
                return createEmptyResponse();
            }
            
            // Parse JSON response directly into DTO
            UnifiedSearchResponse response = objectMapper.readValue(resultJson, UnifiedSearchResponse.class);
            
            log.info("Search completed successfully. Found {} results", 
                    response.getMetadata().getTotalResults());
            
            return response;
            
        } catch (JsonProcessingException e) {
            log.error("Error parsing JSON response from stored procedure: {}", e.getMessage(), e);
            throw new RuntimeException("Error processing search results", e);
        } catch (Exception e) {
            log.error("Error executing search stored procedure: {}", e.getMessage(), e);
            throw new RuntimeException("Search operation failed", e);
        }
    }
    
    /**
     * Optimized search suggestions using database function
     */
    public String getSearchSuggestionsOptimized(String query, Integer limit) {
        log.debug("Getting search suggestions for: {}", query);
        
        if (query == null || query.trim().length() < 2) {
            return "[]"; // Return empty JSON array
        }
        
        // For now, we'll implement suggestions logic here
        // Later, this could also be a stored procedure for maximum performance
        String sql = """
            SELECT json_agg(suggestion) as suggestions
            FROM (
                -- Tutor name suggestions
                SELECT DISTINCT CONCAT(u.first_name, ' ', u.last_name) as suggestion
                FROM tutor_profiles tp
                JOIN users u ON tp.user_id = u.id
                WHERE tp.verified = true
                AND (LOWER(u.first_name) LIKE LOWER(:query) OR LOWER(u.last_name) LIKE LOWER(:query))
                
                UNION
                
                -- Subject name suggestions
                SELECT DISTINCT s.name as suggestion
                FROM subjects s
                JOIN tutor_subjects ts ON s.subject_id = ts.subject_id
                JOIN tutor_profiles tp ON ts.tutor_id = tp.tutor_id
                WHERE tp.verified = true
                AND LOWER(s.name) LIKE LOWER(:query)
                
                ORDER BY suggestion
                LIMIT :limit
            ) suggestions
            """;
        
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("query", "%" + query.trim() + "%")
                .addValue("limit", limit != null ? limit : 10);
        
        try {
            String result = jdbcTemplate.queryForObject(sql, params, String.class);
            return result != null ? result : "[]";
        } catch (Exception e) {
            log.error("Error getting search suggestions: {}", e.getMessage());
            return "[]";
        }
    }
    
    /**
     * Create empty response for error cases
     */
    private UnifiedSearchResponse createEmptyResponse() {
        return UnifiedSearchResponse.builder()
                .results(java.util.List.of())
                .metadata(UnifiedSearchResponse.SearchMetadata.builder()
                        .totalResults(0)
                        .currentPage(1)
                        .totalPages(0)
                        .searchQuery("")
                        .pageSize(20)
                        .build())
                .build();
    }
}