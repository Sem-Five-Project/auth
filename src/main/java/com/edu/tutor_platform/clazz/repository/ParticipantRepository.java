package com.edu.tutor_platform.clazz.repository;

import com.edu.tutor_platform.clazz.entity.ClassEntity;
import com.edu.tutor_platform.clazz.entity.Participants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantRepository extends JpaRepository<Participants, Long> {
    java.util.List<Participants> findByClassEntity(ClassEntity classEntity);

    java.util.List<Participants> findByStudent(com.edu.tutor_platform.studentprofile.entity.StudentProfile student);

    java.util.List<Participants> findByStudentStudentId(Long studentId);

}
