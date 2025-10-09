package com.edu.tutor_platform.studentprofile.controller;


import com.edu.tutor_platform.studentprofile.dto.StudentDto;
import com.edu.tutor_platform.studentprofile.dto.StudentDtoForAdmin;
import com.edu.tutor_platform.studentprofile.dto.StudentStatsDto;
import com.edu.tutor_platform.studentprofile.dto.StudentsDto;
import com.edu.tutor_platform.studentprofile.service.StudentProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentProfileController {

    private final StudentProfileService studentProfileService;

    public StudentProfileController(StudentProfileService studentProfileService) {
        this.studentProfileService = studentProfileService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("")
    public ResponseEntity<List<StudentsDto>> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<StudentsDto> students = studentProfileService.getStudents(page, size);
        return ResponseEntity.ok(students);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/admin")
    public ResponseEntity<StudentDtoForAdmin> getStudentById(@PathVariable String id) {
        StudentDtoForAdmin student = studentProfileService.getStudentDetailsByIdForAdmin(Long.parseLong(id));
        return ResponseEntity.ok(student);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/admin")
    public ResponseEntity<StudentDtoForAdmin> updateStudentByIdForAdmin(@PathVariable String id, @RequestBody StudentDtoForAdmin studentDtoForAdmin) {
        StudentDtoForAdmin student = studentProfileService.updateStudentDetailsByIdForAdmin(Long.parseLong(id), studentDtoForAdmin);
        return ResponseEntity.ok(student);
    }



    @PutMapping("/{id}")
    public ResponseEntity<StudentDto> updateStudent(@PathVariable String id, @RequestBody StudentDto studentDto) {
        StudentDto student = studentProfileService.updateStudentProfile(id, studentDto);
        return new ResponseEntity<>(student, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stats")
    public ResponseEntity<StudentStatsDto> getStudentStats() {
        StudentStatsDto stats = studentProfileService.getStudentStats();
        return ResponseEntity.ok(stats);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/searchByAdmin")
    public ResponseEntity<List<StudentsDto>> searchStudentsByAdmin(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<StudentsDto> students = studentProfileService.searchStudentsByAdmin(
                name, username, email, studentId, status, page, size
        );
        return ResponseEntity.ok(students);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable String id) {
        studentProfileService.deleteStudentProfile(id);
        return ResponseEntity.noContent().build();
    }
}
