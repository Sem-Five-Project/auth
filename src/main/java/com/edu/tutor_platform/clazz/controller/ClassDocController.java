package com.edu.tutor_platform.clazz.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.edu.tutor_platform.clazz.entity.ClassDoc;
import com.edu.tutor_platform.clazz.repository.ClassDocRepository;

@RestController
@RequestMapping("/class-docs")
public class ClassDocController  {
    
    @Autowired
    private ClassDocRepository classDocRepository;

    @PostMapping("/add")
    public ResponseEntity<ClassDoc> addClassDoc(@RequestBody ClassDoc classDoc) {
        ClassDoc savedDoc = classDocRepository.save(classDoc);
        return ResponseEntity.ok(savedDoc);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteClassDoc(@PathVariable Long id) {
        classDocRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    //get documents by class_id
    @GetMapping("/class/{classId}")
    public ResponseEntity<List<ClassDoc>> getClassDocsByClassId(@PathVariable Long classId) {
        List<ClassDoc> classDocs = classDocRepository.findByClassId(classId);
        return ResponseEntity.ok(classDocs);
    }
}
