package com.edu.tutor_platform.clazz.repository;

import com.edu.tutor_platform.clazz.entity.ClassEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, Long> {

    List<ClassEntity> findByTutorId(Long tutorId);
}
