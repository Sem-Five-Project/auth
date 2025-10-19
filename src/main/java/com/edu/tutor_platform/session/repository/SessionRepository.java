package com.edu.tutor_platform.session.repository;

import com.edu.tutor_platform.session.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;


public interface SessionRepository extends JpaRepository<Session, Long> {

    List<Session> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);


    Page<Session> findByStartTimeBeforeAndEndTimeAfter(LocalDateTime start, LocalDateTime end, Pageable pageable);

  // Find all sessions for a given class id
  java.util.List<Session> findByClassEntity_ClassId(Long classId);


    @Query("""
    SELECT DISTINCT s FROM Session s
    JOIN s.classEntity c
    LEFT JOIN c.participants p
    WHERE (:studentId IS NULL OR p.student.studentId = :studentId)
      AND (:tutorId IS NULL OR c.tutorId = :tutorId)
      AND (:subjectId IS NULL OR c.subjectId = :subjectId)
      AND (:status IS NULL OR s.status = :status)
      AND (s.startTime >= COALESCE(:fromTime, s.startTime))
      AND (s.startTime <= COALESCE(:toTime, s.startTime))
    ORDER BY s.startTime DESC
""")
    Page<Session> searchSession(
            @Param("studentId") Long studentId,
            @Param("tutorId") Long tutorId,
            @Param("subjectId") Long subjectId,
            @Param("status") String status,
            @Param("fromTime") LocalDateTime fromTime,
            @Param("toTime") LocalDateTime toTime,
            Pageable pageable
    );


    Long countByStatus(String completed);

}
