package com.edu.tutor_platform.clazz.controller;
// import com.example.demo.dto.SubjectDto;
// import com.example.demo.service.SubjectService;
import com.edu.tutor_platform.clazz.dto.TutorSubjectResponse;
import com.edu.tutor_platform.clazz.dto.TutorSubjectRequest;
import com.edu.tutor_platform.clazz.service.TutorSubjectService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tutors")
public class SubjectController {
    
    private final TutorSubjectService subjectService;

    public SubjectController(TutorSubjectService subjectService) {
        this.subjectService = subjectService;
    }


    @GetMapping("/{tutorId}/subjects")
    public List<TutorSubjectResponse> getTutorSubjects(@PathVariable Long tutorId) {
        return subjectService.getTutorSubjects(tutorId);
    }


    @PostMapping("/add")
    public void addTutorSubject(@RequestBody TutorSubjectRequest request) {
        subjectService.addTutorSubject(request);
    }


}
