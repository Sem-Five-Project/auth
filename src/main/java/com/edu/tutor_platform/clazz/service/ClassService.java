package com.edu.tutor_platform.clazz.service;

import com.edu.tutor_platform.clazz.dto.CreateClassRequest;
import com.edu.tutor_platform.clazz.entity.ClassEntity;
import com.edu.tutor_platform.clazz.repository.ClassRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClassService {
    @Autowired
    private ClassRepository classRepository;

    public ClassEntity createClass(CreateClassRequest request) {
        ClassEntity classEntity = new ClassEntity();
        classEntity.setClassName(request.getClassName());
        classEntity.setTutorId(request.getTutorId());
        classEntity.setSubjectId(request.getSubjectId());
        classEntity.setClassTypeId(request.getClassTypeId());
        classEntity.setComment(request.getComment());
        classEntity.setDate(request.getDate());
        classEntity.setStartTime(request.getStartTime());
        classEntity.setEndTime(request.getEndTime());
        return classRepository.save(classEntity);
    }

    public ClassEntity getClassById(Long id) {
        return classRepository.findById(id).orElseThrow(() -> new RuntimeException("Class not found"));
    }

    public List<ClassEntity> getClassesByTutorId(Long tutorId) {

        return classRepository.findByTutorId(tutorId);
    }

    public void deleteClass(Long tutorId, Long classId) {
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));
        if (!classEntity.getTutorId().equals(tutorId)) {
            throw new RuntimeException("Tutor ID does not match");
        }
        classRepository.deleteById(classId);
    }

    public List<ClassEntity> getAllClasses() {
        return classRepository.findAll();
    }
}
