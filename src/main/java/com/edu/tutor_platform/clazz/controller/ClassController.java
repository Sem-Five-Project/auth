package com.edu.tutor_platform.clazz.controller;

import com.edu.tutor_platform.clazz.dto.CreateClassRequest;
import com.edu.tutor_platform.clazz.entity.ClassEntity;
import com.edu.tutor_platform.clazz.service.ClassService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/classes")
public class ClassController {
    @Autowired
    private ClassService classService;

    @PostMapping("/create")
    public ResponseEntity<ClassEntity> createClass(@RequestBody CreateClassRequest request) {
        ClassEntity createdClass = classService.createClass(request);
        return ResponseEntity.ok(createdClass);
    }

    // Get class by ID
    @GetMapping("/{id}")
    public ResponseEntity<ClassEntity> getClassById(@PathVariable Long id) {
        ClassEntity classEntity = classService.getClassById(id);
        return ResponseEntity.ok(classEntity);
    }

    //get classes by tutor ID
    @GetMapping("/tutor/{tutorId}")
    public ResponseEntity<List<ClassEntity>> getClassesByTutorId(@PathVariable Long tutorId) {
        List<ClassEntity> classes = classService.getClassesByTutorId(tutorId);
        return ResponseEntity.ok(classes);
    }

    //delete a class by tutior ID and class ID
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteClass(@RequestParam Long tutorId, @RequestParam Long classId) {
        classService.deleteClass(tutorId, classId);
        return ResponseEntity.noContent().build();
    }
}
