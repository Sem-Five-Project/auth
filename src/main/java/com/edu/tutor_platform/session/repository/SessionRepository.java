package com.edu.tutor_platform.session.repository;

import com.edu.tutor_platform.session.entity.Session;
import com.edu.tutor_platform.studentprofile.entity.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;


public interface SessionRepository extends JpaRepository<Session, Long> {

    List<Session> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);


    List<Session> findByStartTimeBeforeAndEndTimeAfter(LocalDateTime before, LocalDateTime after);;
}
