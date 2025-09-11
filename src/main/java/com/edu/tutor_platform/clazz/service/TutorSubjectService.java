package com.edu.tutor_platform.clazz.service;

import com.edu.tutor_platform.clazz.dto.TutorSubjectRequest;
import com.edu.tutor_platform.clazz.dto.TutorSubjectResponse;
import com.edu.tutor_platform.clazz.enums.VerificationEnum;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class TutorSubjectService {
    private final JdbcTemplate jdbcTemplate;

    public TutorSubjectService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<TutorSubjectResponse> getTutorSubjects(Long tutorId) {
        String sql = "SELECT ts.*, s.name AS subject_name FROM tutor_subjects ts JOIN subject s ON ts.subject_id = s.subject_id WHERE ts.tutor_id = ?";
        return jdbcTemplate.query(sql, new Object[]{tutorId}, new RowMapper<TutorSubjectResponse>() {
            @Override
            public TutorSubjectResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
                TutorSubjectResponse response = new TutorSubjectResponse();
                response.setTutorSubjectId(rs.getLong("tutor_subject_id"));
                response.setTutorId(rs.getLong("tutor_id"));
                response.setSubjectId(rs.getLong("subject_id"));
                response.setSubjectName(rs.getString("subject_name"));
                String verification = rs.getString("verification");
                response.setVerification(verification != null ? VerificationEnum.valueOf(verification) : null);
                response.setVerificationDocs(rs.getString("verification_docs"));
                response.setHourlyRate(rs.getBigDecimal("hourly_rate"));
                return response;
            }
        });
    }

    public void addTutorSubject(TutorSubjectRequest request) {
        String sql = "INSERT INTO tutor_subjects (tutor_id, subject_id, verification, verification_docs, hourly_rate) VALUES (?, ?, ?::verification_enum, ?, ?)";
        jdbcTemplate.update(sql,
                request.getTutorId(),
                request.getSubjectId(),
                request.getVerification() != null ? request.getVerification().name() : null,
                request.getVerificationDocs(),
                request.getHourlyRate()
        );
    }
}
