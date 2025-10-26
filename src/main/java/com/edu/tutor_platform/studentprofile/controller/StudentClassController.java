// package com.edu.tutor_platform.studentprofile.controller;

// import com.edu.tutor_platform.studentprofile.dto.ClasssDetailResponseDto;
// import com.edu.tutor_platform.studentprofile.service.StudentProfileService;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import java.util.Map;

// @RestController
// @RequestMapping("/student/class-details")
// @RequiredArgsConstructor
// public class StudentClassController {

//     private final StudentProfileService studentProfileService;

//     @GetMapping("/{studentId}")
//     public ResponseEntity<?> getAllClassDetails(@PathVariable Long studentId) {
        
//         System.out.println("Fetching all class details for studentId: " + studentId + " from NEW controller");
//         try {
//             ClasssDetailResponseDto dto = studentProfileService.getAllClassDetails(studentId);
//             return ResponseEntity.ok(dto);
//         } catch (RuntimeException e) {
//             return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
//         } catch (Exception e) {
//             return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage()));
//         }
//     }
// }
