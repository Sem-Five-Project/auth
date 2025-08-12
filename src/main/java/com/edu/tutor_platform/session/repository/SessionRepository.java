package com.edu.tutor_platform.session.repository;

import com.edu.tutor_platform.session.entity.Session;
import com.edu.tutor_platform.session.entity.SessionStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface SessionRepository extends JpaRepository<Session, Long> {

    @Query("SELECT s.tutorId FROM Session s WHERE s.sessionId = :sessionId")
    Long findTutorIdBySessionId(@Param("sessionId") Long sessionId);

    @Modifying
    @Transactional
    @Query("UPDATE Session s SET s.studentCount = s.studentCount + 1 WHERE s.sessionId = :sessionId")
    void incrementStudentCount(@Param("sessionId") Long sessionId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO session_student (session_id, student_id, payment_id) VALUES (:#{#sessionStudent.sessionId}, :#{#sessionStudent.studentId}, :#{#sessionStudent.paymentId})", nativeQuery = true)
    void saveSessionStudent(@Param("sessionStudent") SessionStudent sessionStudent);
}