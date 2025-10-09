
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

import com.edu.tutor_platform.tutorsearch.filter.dto.*;
import com.edu.tutor_platform.tutorsearch.filter.repository.TutorFilterRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.codec.language.bm.Lang;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Array;
import java.sql.ResultSet;
import java.util.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TutorFilterRepositoryImpl implements TutorFilterRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    private static final TypeReference<List<SubjectWithRateDTO>> SUBJECT_LIST_TYPE =new TypeReference<>() {};
    private static final TypeReference<List<LanguageWithIdDTO>> LANGUAGE_LIST_TYPE =new TypeReference<>() {};


    @Override
    public List<TutorFilterResultDTO> searchByFilters(TutorFilterRequestDTO request) {
        try {
            String json = objectMapper.writeValueAsString(request);
            log.debug("Tutor filter JSON payload: {}", json);

            String sql = "SELECT * FROM public.search_tutors_by_filters(:filters::jsonb)";
            var params = new MapSqlParameterSource().addValue("filters", json);

            return jdbcTemplate.query(sql, params, (rs, rowNum) -> {
                if (rowNum == 0) logColumns(rs);
                List<SubjectWithRateDTO> subjects = parseSubjects(rs);
                List<LanguageWithIdDTO> languages = parseLanguages(rs);

                return TutorFilterResultDTO.builder()
                        .tutorId(getLong(rs, "tutor_id"))
                        .bio(getString(rs, "bio"))
                        .rating(getDouble(rs, "rating"))
                        .experienceMonths(getInt(rs, "experience_months"))
                        .subjects(subjects)
                        .languages(languages)
                        .build();
            });
        } catch (Exception e) {
            log.error("Tutor filter search failed", e);
            throw new RuntimeException("Failed executing tutor filter search: " + e.getMessage(), e);
        }
    }

    private void logColumns(ResultSet rs) {
        try {
            var md = rs.getMetaData();
            StringBuilder sb = new StringBuilder("search_tutors_by_filters columns: ");
            for (int i = 1; i <= md.getColumnCount(); i++) {
                sb.append(md.getColumnLabel(i)).append("(").append(md.getColumnTypeName(i)).append(") ");
            }
            log.debug(sb.toString());
        } catch (Exception ignore) {}
    }

    private List<SubjectWithRateDTO> parseSubjects(ResultSet rs) {
        try {
            String json = rs.getString("subjects");
            if (json == null) return List.of();
            return objectMapper.readValue(json, SUBJECT_LIST_TYPE);
        } catch (Exception e) {
            log.warn("Failed parsing subjects json", e);
            return List.of();
        }
    }

    private List<LanguageWithIdDTO> parseLanguages(ResultSet rs) {
        try {
            String json = rs.getString("languages");
            if(json==null) return List.of();
            return objectMapper.readValue(json, LANGUAGE_LIST_TYPE);

        } catch (Exception e) {
            log.warn("Failed parsing languages array", e);
            return List.of();
        }
    }

    private Long getLong(ResultSet rs, String c) {
        try { return rs.getObject(c) == null ? null : rs.getLong(c); } catch (Exception e) { return null; }
    }
    private String getString(ResultSet rs, String c) {
        try { return rs.getString(c); } catch (Exception e) { return null; }
    }
    private Double getDouble(ResultSet rs, String c) {
        try { return rs.getObject(c) == null ? null : rs.getDouble(c); } catch (Exception e) { return null; }
    }
    private Integer getInt(ResultSet rs, String c) {
        try { return rs.getObject(c) == null ? null : rs.getInt(c); } catch (Exception e) { return null; }
    }
}