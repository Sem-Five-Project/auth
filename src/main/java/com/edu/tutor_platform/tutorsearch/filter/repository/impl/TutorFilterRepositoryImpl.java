
// import com.edu.tutor_platform.tutorsearch.filter.dto.TutorFilterRequestDTO;
// import com.edu.tutor_platform.tutorsearch.filter.dto.TutorFilterResultDTO;
// import com.edu.tutor_platform.tutorsearch.filter.repository.TutorFilterRepository;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import lombok.RequiredArgsConstructor;
// import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
// import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
// import org.springframework.stereotype.Repository;

// import java.sql.Array;
// import java.util.Arrays;
// import java.util.List;

// @Repository
// @RequiredArgsConstructor
// public class TutorFilterRepositoryImpl implements TutorFilterRepository {

//     private final NamedParameterJdbcTemplate jdbcTemplate;
//     private final ObjectMapper objectMapper = new ObjectMapper();

//     @Override
//     public List<TutorFilterResultDTO> searchByFilters(TutorFilterRequestDTO request) {
//         try {
//             String json = objectMapper.writeValueAsString(request);
//             String sql = "SELECT * FROM public.search_tutors_by_filters(:filters::jsonb)";
//             var params = new MapSqlParameterSource().addValue("filters", json);

//             return jdbcTemplate.query(sql, params, (rs, i) -> {
//                 String[] subj = null;
//                 Array arr = rs.getArray("matched_subjects");
//                 if (arr != null) subj = (String[]) arr.getArray();
//                 return TutorFilterResultDTO.builder()
//                         .tutorId(rs.getLong("tutor_id"))
//                         .userId(rs.getLong("user_id"))
//                         .bio(rs.getString("bio"))
//                         .hourlyRate(rs.getBigDecimal("hourly_rate"))
//                         .rating(rs.getObject("rating") == null ? null : rs.getDouble("rating"))
//                         .experienceMonths(rs.getObject("experience_months") == null ? null : rs.getInt("experience_months"))
//                         .verified(rs.getObject("verified") != null && rs.getBoolean("verified"))
//                         .matchedSubjects(subj == null ? List.of() : Arrays.asList(subj))
//                         .matchedMinHourlyRate(rs.getBigDecimal("matched_min_hourly_rate"))
//                         .build();
//             });
//         } catch (Exception e) {
//             throw new RuntimeException("Failed executing tutor filter search", e);
//         }
//     }
// }package com.edu.tutor_platform.tutorsearch.filter.repository.impl;
package com.edu.tutor_platform.tutorsearch.filter.repository.impl;

import com.edu.tutor_platform.tutorsearch.filter.dto.TutorFilterRequestDTO;
import com.edu.tutor_platform.tutorsearch.filter.dto.TutorFilterResultDTO;
import com.edu.tutor_platform.tutorsearch.filter.repository.TutorFilterRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Array;
import java.util.Arrays;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TutorFilterRepositoryImpl implements TutorFilterRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper; // Spring-managed (JavaTimeModule registered)

    @Override
    public List<TutorFilterResultDTO> searchByFilters(TutorFilterRequestDTO request) {
        try {
            String json = objectMapper.writeValueAsString(request);
            log.debug("Tutor filter JSON payload: {}", json);

            String sql = "SELECT * FROM public.search_tutors_by_filters(:filters::jsonb)";
            var params = new MapSqlParameterSource().addValue("filters", json);

            return jdbcTemplate.query(sql, params, (rs, i) -> {
                Array arr = rs.getArray("matched_subjects");
                List<String> subjects = (arr == null) ? List.of() : Arrays.asList((String[]) arr.getArray());

                return TutorFilterResultDTO.builder()
                        .tutorId(rs.getLong("tutor_id"))
                        .userId(rs.getLong("user_id"))
                        .bio(rs.getString("bio"))
                        .hourlyRate(rs.getBigDecimal("hourly_rate"))
                        .rating(rs.getObject("rating") == null ? null : rs.getDouble("rating"))
                        .experienceMonths(rs.getObject("experience_months") == null ? null : rs.getInt("experience_months"))
                        .verified(rs.getObject("verified") != null && rs.getBoolean("verified"))
                        .matchedSubjects(subjects)
                        .matchedMinHourlyRate(rs.getBigDecimal("matched_min_hourly_rate"))
                        .build();
            });
        } catch (Exception e) {
            log.error("Tutor filter search failed", e);
            throw new RuntimeException("Failed executing tutor filter search: " + e.getMessage(), e);
        }
    }
}