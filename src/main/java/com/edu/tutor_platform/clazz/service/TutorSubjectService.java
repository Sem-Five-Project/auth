package com.edu.tutor_platform.clazz.service;
import com.edu.tutor_platform.clazz.dto.SubjectRequest;
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

    public List<SubjectRequest> getVerifiedSubjects(Long tutorId) {
        String sql = "SELECT * FROM get_verified_subjects(?)";

        return jdbcTemplate.query(sql, new Object[]{tutorId}, new RowMapper<SubjectRequest>() {
            @Override
            public SubjectRequest mapRow(ResultSet rs, int rowNum) throws SQLException {
                SubjectRequest subject = new SubjectRequest();
                subject.setSubjectId(rs.getLong("subject_id"));
                subject.setSubjectName(rs.getString("subject_name"));
                return subject;
            }
        });
    }

    public void addSubjectForTutor(Long tutorId, Long subjectId) {
    String sql = "INSERT INTO tutor_subjects (tutor_id, subject_id) VALUES (?, ?)";
    jdbcTemplate.update(sql, tutorId, subjectId);
}
}
