package com.edu.tutor_platform.clazz.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edu.tutor_platform.clazz.entity.ClassDoc;

@Repository
public interface ClassDocRepository extends JpaRepository<ClassDoc, Long> {
    // Custom query method to find documents by classId
    List<ClassDoc> findByClassId(Long classId);
}