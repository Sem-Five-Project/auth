// package com.edu.tutor_platform.search.repository;

// import com.edu.tutor_platform.search.dto.UnifiedSearchResponse;
// import com.edu.tutor_platform.tutorprofile.entity.TutorProfile;
// import com.edu.tutor_platform.tutorprofile.repository.TutorProfileRepository;
// import lombok.RequiredArgsConstructor;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.domain.Sort;
// import org.springframework.stereotype.Repository;

// import java.math.BigDecimal;
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// @Repository
// @RequiredArgsConstructor
// public class UnifiedSearchRepository {
    
//     private final TutorProfileRepository tutorProfileRepository;
    
//     /**
//      * Performs comprehensive unified search across tutors and available slots
//      */
//     public List<UnifiedSearchResponse.SearchResult> performUnifiedSearch(
//             String query, Double minRating, Integer minExperience, Double minCompletionRate,
//             String tutorName, String subject, Double minPrice, Double maxPrice,
//             List<String> classTypes, List<Long> subjectIds, String sortBy, String sortOrder,
//             String searchType, Integer page, Integer size) {
        
//         List<UnifiedSearchResponse.SearchResult> results = new ArrayList<>();
        
//         // Create sort and pageable
//         Sort sort = createSort(sortBy, sortOrder);
//         Pageable pageable = PageRequest.of(page, size, sort);
        
//         // Normalize parameters
//         String searchQuery = query != null ? query.trim() : "";
//         BigDecimal minRatingBd = BigDecimal.valueOf(minRating != null ? minRating : 0.0);
//         BigDecimal minCompletionRateBd = BigDecimal.valueOf(minCompletionRate != null ? minCompletionRate : 0.0);
//         BigDecimal minPriceBd = BigDecimal.valueOf(minPrice != null ? minPrice : 0.0);
//         BigDecimal maxPriceBd = maxPrice != null ? BigDecimal.valueOf(maxPrice) : BigDecimal.valueOf(Double.MAX_VALUE);
//         Integer minExp = minExperience != null ? minExperience : 0;
//         String tutorNameQuery = tutorName != null ? tutorName.trim() : "";
//         String subjectQuery = subject != null ? subject.trim() : "";
//         String searchTypeNorm = searchType != null ? searchType : "both";
        
//         // Search based on search type
//         if ("tutors".equals(searchTypeNorm) || "both".equals(searchTypeNorm)) {
//             List<TutorProfile> tutorResults = searchTutorsWithFilters(
//                 searchQuery, tutorNameQuery, subjectQuery, minRatingBd, minExp,
//                 minCompletionRateBd, minPriceBd, maxPriceBd, pageable);
            
//             for (TutorProfile tutor : tutorResults) {
//                 results.add(mapTutorToSearchResult(tutor, searchQuery));
//             }
//         }
        
//         // TODO: Add slot searching when slot entities and repositories are ready
//         if ("slots".equals(searchTypeNorm) || "both".equals(searchTypeNorm)) {
//             // This would search available slots with similar filters
//             // List<SlotInstance> slotResults = searchSlotsWithFilters(...);
//         }
        
//         // Sort results by relevance if needed
//         if ("relevance".equals(sortBy)) {
//             results.sort((a, b) -> Double.compare(b.getRelevanceScore(), a.getRelevanceScore()));
//         }
        
//         return results.stream()
//                 .skip((long) page * size)
//                 .limit(size)
//                 .toList();
//     }
    
//     /**
//      * Enhanced tutor search with comprehensive filters
//      */
//     private List<TutorProfile> searchTutorsWithFilters(String query, String tutorName, String subject,
//             BigDecimal minRating, Integer minExperience, BigDecimal minCompletionRate,
//             BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        
//         // For now, use the existing repository method and filter in memory
//         // In a real implementation, you'd create custom JPQL queries for better performance
//         Page<TutorProfile> allTutors = tutorProfileRepository.searchTutors(
//             query.isEmpty() ? tutorName : query, minRating, minExperience, pageable);
        
//         return allTutors.getContent().stream()
//                 .filter(tutor -> matchesTutorFilters(tutor, query, tutorName, subject,
//                         minCompletionRate, minPrice, maxPrice))
//                 .toList();
//     }
    
//     /**
//      * Enhanced filtering for tutors
//      */
//     private boolean matchesTutorFilters(TutorProfile tutor, String query, String tutorName,
//             String subject, BigDecimal minCompletionRate, BigDecimal minPrice, BigDecimal maxPrice) {
        
//         // Check completion rate
//         if (tutor.getClassCompletionRate() != null &&
//             tutor.getClassCompletionRate().compareTo(minCompletionRate) < 0) {
//             return false;
//         }
        
//         // Check price range
//         if (tutor.getHourlyRate() != null) {
//             if (tutor.getHourlyRate().compareTo(minPrice) < 0 ||
//                 tutor.getHourlyRate().compareTo(maxPrice) > 0) {
//                 return false;
//             }
//         }
        
//         // Check tutor name match
//         if (!tutorName.isEmpty()) {
//             String fullName = (tutor.getUser().getFirstName() + " " + tutor.getUser().getLastName()).toLowerCase();
//             if (!fullName.contains(tutorName.toLowerCase())) {
//                 return false;
//             }
//         }
        
//         // Check subject match - search in tutor's subjects
//         if (!subject.isEmpty() && tutor.getTutorSubjects() != null) {
//             boolean subjectMatch = tutor.getTutorSubjects().stream()
//                     .anyMatch(ts -> ts.getSubject().getName().toLowerCase().contains(subject.toLowerCase()));
//             if (!subjectMatch) {
//                 return false;
//             }
//         }
        
//         // Enhanced keyword matching - check if query matches subject names or partial names
//         if (!query.isEmpty()) {
//             return matchesKeyword(tutor, query);
//         }
        
//         return true;
//     }
    
//     /**
//      * Enhanced keyword matching for queries like "his" -> "history" or tutor names containing "hisroma"
//      */
//     private boolean matchesKeyword(TutorProfile tutor, String query) {
//         String queryLower = query.toLowerCase();
//         String fullName = (tutor.getUser().getFirstName() + " " + tutor.getUser().getLastName()).toLowerCase();
//         String bio = tutor.getBio() != null ? tutor.getBio().toLowerCase() : "";
        
//         // Direct name match
//         if (fullName.contains(queryLower)) {
//             return true;
//         }
        
//         // Bio match
//         if (bio.contains(queryLower)) {
//             return true;
//         }
        
//         // Subject name matching
//         if (tutor.getTutorSubjects() != null) {
//             for (var tutorSubject : tutor.getTutorSubjects()) {
//                 String subjectName = tutorSubject.getSubject().getName().toLowerCase();
//                 if (subjectName.contains(queryLower) || subjectName.startsWith(queryLower)) {
//                     return true;
//                 }
                
//                 // Special cases for partial matching
//                 if (queryLower.equals("his") && subjectName.contains("history")) {
//                     return true;
//                 }
//                 if (queryLower.equals("math") && subjectName.contains("mathematics")) {
//                     return true;
//                 }
//                 if (queryLower.equals("phys") && subjectName.contains("physics")) {
//                     return true;
//                 }
//                 if (queryLower.equals("chem") && subjectName.contains("chemistry")) {
//                     return true;
//                 }
//             }
//         }
        
//         return false;
//     }
    
//     /**
//      * Creates appropriate sort for the enhanced query
//      */
//     private Sort createSort(String sortBy, String sortOrder) {
//         Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
        
//         switch (sortBy.toLowerCase()) {
//             case "rating":
//                 return Sort.by(direction, "rating");
//             case "experience":
//                 return Sort.by(direction, "experienceInMonths");
//             case "price":
//                 return Sort.by(direction, "hourlyRate");
//             case "completion_rate":
//                 return Sort.by(direction, "classCompletionRate");
//             case "name":
//             case "class_name":
//                 return Sort.by(direction, "user.firstName", "user.lastName");
//             case "relevance":
//             default:
//                 return Sort.by(Sort.Direction.DESC, "rating", "experienceInMonths"); // Default sort
//         }
//     }
    
//     /**
//      * Maps TutorProfile entity to SearchResult DTO
//      */
//     private UnifiedSearchResponse.SearchResult mapTutorToSearchResult(TutorProfile tutor, String query) {
//         double relevanceScore = calculateTutorRelevanceScore(tutor, query);
        
//         return UnifiedSearchResponse.SearchResult.builder()
//                 .resultType("tutor")
//                 .id(tutor.getTutorId())
//                 .title(tutor.getUser().getFirstName() + " " + tutor.getUser().getLastName())
//                 .description(tutor.getBio())
//                 .rating(tutor.getRating())
//                 .experienceMonths(tutor.getExperienceInMonths())
//                 .subjectName(null)
//                 .tutorName(tutor.getUser().getFirstName() + " " + tutor.getUser().getLastName())
//                 .tutorFirstName(tutor.getUser().getFirstName())
//                 .tutorLastName(tutor.getUser().getLastName())
//                 .maxDays(null)
//                 .schedule(null)
//                 .hourlyRate(tutor.getHourlyRate())
//                 .classType("tutor")
//                 .relevanceScore(relevanceScore)
//                 .bio(tutor.getBio())
//                 .classCompletionRate(tutor.getClassCompletionRate())
//                 .build();
//     }
    
//     /**
//      * Enhanced relevance scoring for tutor based on multiple criteria
//      */
//     private double calculateTutorRelevanceScore(TutorProfile tutor, String query) {
//         if (query == null || query.trim().isEmpty()) {
//             return 50.0; // Default score when no query
//         }
        
//         String queryLower = query.toLowerCase();
//         String fullName = (tutor.getUser().getFirstName() + " " + tutor.getUser().getLastName()).toLowerCase();
//         String bio = tutor.getBio() != null ? tutor.getBio().toLowerCase() : "";
//         double score = 0.0;
        
//         // Name matching scores
//         if (fullName.equals(queryLower)) {
//             score = 100.0; // Exact name match
//         } else if (fullName.contains(queryLower)) {
//             score = 90.0; // Partial name match
//         } else if (fullName.split(" ")[0].startsWith(queryLower) ||
//                    fullName.split(" ").length > 1 && fullName.split(" ")[1].startsWith(queryLower)) {
//             score = 85.0; // Name starts with query
//         }
        
//         // Subject matching scores
//         if (tutor.getTutorSubjects() != null) {
//             for (var tutorSubject : tutor.getTutorSubjects()) {
//                 String subjectName = tutorSubject.getSubject().getName().toLowerCase();
//                 if (subjectName.equals(queryLower)) {
//                     score = Math.max(score, 95.0); // Exact subject match
//                 } else if (subjectName.contains(queryLower)) {
//                     score = Math.max(score, 80.0); // Partial subject match
//                 } else if (subjectName.startsWith(queryLower)) {
//                     score = Math.max(score, 85.0); // Subject starts with query
//                 }
                
//                 // Special partial matching cases
//                 if (queryLower.equals("his") && subjectName.contains("history")) {
//                     score = Math.max(score, 88.0);
//                 }
//                 if (queryLower.equals("math") && subjectName.contains("mathematics")) {
//                     score = Math.max(score, 88.0);
//                 }
//                 if (queryLower.equals("phys") && subjectName.contains("physics")) {
//                     score = Math.max(score, 88.0);
//                 }
//                 if (queryLower.equals("chem") && subjectName.contains("chemistry")) {
//                     score = Math.max(score, 88.0);
//                 }
//             }
//         }
        
//         // Bio matching
//         if (bio.contains(queryLower)) {
//             score = Math.max(score, 70.0); // Bio match
//         }
        
//         // Default score if no matches found
//         if (score == 0.0) {
//             score = 40.0;
//         }
        
//         // Boost score based on tutor quality metrics
//         if (tutor.getRating() != null && tutor.getRating().doubleValue() >= 4.5) {
//             score += 5.0; // High rating boost
//         }
//         if (tutor.getClassCompletionRate() != null && tutor.getClassCompletionRate().doubleValue() >= 90.0) {
//             score += 3.0; // High completion rate boost
//         }
        
//         return Math.min(score, 100.0); // Cap at 100
//     }
    
//     /**
//      * Gets total count for pagination with enhanced filters
//      */
//     public Integer getTotalSearchResults(String query, Double minRating, Integer minExperience,
//                                        Double minCompletionRate, String tutorName, String subject,
//                                        Double minPrice, Double maxPrice, List<String> classTypes,
//                                        List<Long> subjectIds, String searchType) {
        
//         // For now, use a simplified count - in production you'd want an optimized count query
//         String searchQuery = query != null ? query.trim() : "";
//         BigDecimal minRatingBd = BigDecimal.valueOf(minRating != null ? minRating : 0.0);
//         Integer minExp = minExperience != null ? minExperience : 0;
        
//         Long count = tutorProfileRepository.countSearchResults(searchQuery, minRatingBd, minExp);
        
//         // Apply additional filters (this is simplified - in production use database-level counting)
//         if (count > 0 && (minCompletionRate != null || tutorName != null || subject != null ||
//                           minPrice != null || maxPrice != null)) {
//             // For now, return approximate count. In production, create specific count queries.
//             count = Math.round(count * 0.8); // Rough estimate after filtering
//         }
        
//         return count.intValue();
//     }
    
//     /**
//      * Enhanced search suggestions with keyword matching
//      */
//     public List<String> getSearchSuggestions(String query, Integer limit) {
//         if (query == null || query.trim().length() < 2) {
//             return List.of();
//         }
        
//         List<String> allSuggestions = new ArrayList<>();
//         String queryLower = query.toLowerCase();
        
//         // Get tutor name suggestions
//         Pageable pageable = PageRequest.of(0, limit / 2);
//         List<String> tutorSuggestions = tutorProfileRepository.findTutorNameSuggestions(query, pageable);
//         allSuggestions.addAll(tutorSuggestions);
        
//         // Enhanced subject suggestions with partial matching
//         if (queryLower.startsWith("his") || queryLower.contains("hist")) {
//             allSuggestions.add("History");
//             allSuggestions.add("History - World Wars");
//             allSuggestions.add("Ancient History");
//         }
//         if (queryLower.startsWith("chem") || queryLower.contains("chem")) {
//             allSuggestions.add("Chemistry");
//             allSuggestions.add("Organic Chemistry");
//             allSuggestions.add("Chemical Bonds Lesson");
//         }
//         if (queryLower.startsWith("phys") || queryLower.contains("phys")) {
//             allSuggestions.add("Physics");
//             allSuggestions.add("Quantum Physics");
//             allSuggestions.add("Physics Sound Waves Class");
//         }
//         if (queryLower.startsWith("math") || queryLower.contains("math")) {
//             allSuggestions.add("Mathematics");
//             allSuggestions.add("Advanced Mathematics");
//             allSuggestions.add("Calculus Class");
//         }
//         if (queryLower.startsWith("eng") || queryLower.contains("eng")) {
//             allSuggestions.add("English");
//             allSuggestions.add("English Literature");
//             allSuggestions.add("English Grammar");
//         }
//         if (queryLower.startsWith("bio") || queryLower.contains("bio")) {
//             allSuggestions.add("Biology");
//             allSuggestions.add("Cell Biology");
//             allSuggestions.add("Human Biology");
//         }
        
//         // Add subject suggestions based on partial matches
//         addPartialMatchSuggestions(queryLower, allSuggestions);
        
//         return allSuggestions.stream()
//                 .distinct()
//                 .limit(limit)
//                 .toList();
//     }
    
//     /**
//      * Add suggestions for partial keyword matches
//      */
//     private void addPartialMatchSuggestions(String query, List<String> suggestions) {
//         // Add suggestions for common partial searches
//         if (query.length() >= 2) {
//             if ("history".startsWith(query)) suggestions.add("History");
//             if ("mathematics".startsWith(query)) suggestions.add("Mathematics");
//             if ("physics".startsWith(query)) suggestions.add("Physics");
//             if ("chemistry".startsWith(query)) suggestions.add("Chemistry");
//             if ("biology".startsWith(query)) suggestions.add("Biology");
//             if ("english".startsWith(query)) suggestions.add("English");
//             if ("science".startsWith(query)) suggestions.add("Science");
//             if ("computer".startsWith(query)) suggestions.add("Computer Science");
//             if ("programming".startsWith(query)) suggestions.add("Programming");
//             if ("calculus".startsWith(query)) suggestions.add("Calculus");
//             if ("algebra".startsWith(query)) suggestions.add("Algebra");
//             if ("geometry".startsWith(query)) suggestions.add("Geometry");
//         }
//     }
    
//     /**
//      * Gets available subjects for filter options
//      */
//     public List<Map<String, Object>> getAvailableSubjects() {
//         // Sample data - replace with actual subject repository when available
//         List<Map<String, Object>> subjects = new ArrayList<>();
        
//         Map<String, Object> math = new HashMap<>();
//         math.put("subject_id", 1L);
//         math.put("name", "Mathematics");
//         math.put("class_count", 15);
//         subjects.add(math);
        
//         Map<String, Object> physics = new HashMap<>();
//         physics.put("subject_id", 2L);
//         physics.put("name", "Physics");
//         physics.put("class_count", 12);
//         subjects.add(physics);
        
//         Map<String, Object> chemistry = new HashMap<>();
//         chemistry.put("subject_id", 3L);
//         chemistry.put("name", "Chemistry");
//         chemistry.put("class_count", 8);
//         subjects.add(chemistry);
        
//         return subjects;
//     }
// }