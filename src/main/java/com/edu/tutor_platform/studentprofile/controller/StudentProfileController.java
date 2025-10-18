package com.edu.tutor_platform.studentprofile.controller;

import com.edu.tutor_platform.studentprofile.dto.StudentDto;
import com.edu.tutor_platform.studentprofile.dto.StudentDtoForAdmin;
import com.edu.tutor_platform.studentprofile.dto.StudentStatsDto;
import com.edu.tutor_platform.studentprofile.dto.StudentsDto;
import com.edu.tutor_platform.studentprofile.dto.StudentAcademicInfoDTO;
import com.edu.tutor_platform.studentprofile.dto.StudentProfileInfoRespondDTO;
import com.edu.tutor_platform.studentprofile.dto.StudentProfileResponse;
import com.edu.tutor_platform.studentprofile.dto.ClasssDetailResponseDto;
import com.edu.tutor_platform.studentprofile.dto.StudentUpcomingClassResponseDto;
import com.edu.tutor_platform.studentprofile.service.StudentProfileService;
import com.edu.tutor_platform.rating.dto.RatingQuickRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("students")
@RequiredArgsConstructor
@Slf4j
public class StudentProfileController {

    private final StudentProfileService studentProfileService;
    // ratingService removed; logic delegated to StudentProfileService

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

    @GetMapping("/{studentId}")
    public ResponseEntity<StudentProfileResponse> getStudentProfile(@PathVariable Long studentId) {
        log.info("Fetching student profile for ID: {}", studentId);
        try {
            StudentProfileResponse profile = studentProfileService.getStudentProfileById(studentId);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            log.error("Error fetching student profile {}: {}", studentId, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Unexpected error fetching student profile {}: {}", studentId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<StudentProfileResponse> getStudentProfileByUserId(@PathVariable Long userId) {
        log.info("Fetching student profile for user ID: {}", userId);
        try {
            StudentProfileResponse profile = studentProfileService.getStudentProfileByUserId2(userId);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            log.error("Error fetching student profile for user {}: {}", userId, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Unexpected error fetching student profile for user {}: {}", userId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<StudentProfileResponse> getStudentProfileByEmail(@PathVariable String email) {
        log.info("Fetching student profile for email: {}", email);
        try {
            StudentProfileResponse profile = studentProfileService.getStudentProfileByEmail(email);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            log.error("Error fetching student profile for email {}: {}", email, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Unexpected error fetching student profile for email {}: {}", email, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<StudentProfileResponse>> getAllStudentProfiles() {
        log.info("Fetching all student profiles");
        try {
            List<StudentProfileResponse> profiles = studentProfileService.getAllStudentProfiles();
            log.info("Retrieved {} student profiles", profiles.size());
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            log.error("Error fetching all student profiles: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/active")
    public ResponseEntity<List<StudentProfileResponse>> getActiveStudentProfiles() {
        log.info("Fetching active student profiles");
        try {
            List<StudentProfileResponse> profiles = studentProfileService.getActiveStudentProfiles();
            log.info("Retrieved {} active student profiles", profiles.size());
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            log.error("Error fetching active student profiles: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{studentId}")
    public ResponseEntity<StudentProfileResponse> updateStudentProfile(
            @PathVariable Long studentId,
            @RequestBody StudentProfileResponse updateRequest) {
        log.info("Updating student profile for ID: {}", studentId);
        try {
            StudentProfileResponse updatedProfile = studentProfileService.updateStudentProfile(studentId, updateRequest);
            log.info("Successfully updated student profile: {}", studentId);
            return ResponseEntity.ok(updatedProfile);
        } catch (RuntimeException e) {
            log.error("Error updating student profile {}: {}", studentId, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Unexpected error updating student profile {}: {}", studentId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{studentId}/summary")
    public ResponseEntity<Map<String, Object>> getStudentProfileSummary(@PathVariable Long studentId) {
        log.info("Fetching student profile summary for ID: {}", studentId);
        try {
            StudentProfileResponse profile = studentProfileService.getStudentProfileById(studentId);
            Map<String, Object> summary = new HashMap<>();
            summary.put("studentId", profile.getStudentId());
            summary.put("fullName", profile.getFullName());
            summary.put("email", profile.getEmail());
            summary.put("membership", profile.getMembership());
            summary.put("status", profile.getStatusDescription());
            summary.put("isActive", profile.getIsActive());
            summary.put("educationLevel", profile.getEducationLevel());
            summary.put("educationalLevel", profile.getEducationalLevel());
            return ResponseEntity.ok(summary);
        } catch (RuntimeException e) {
            log.error("Error fetching student profile summary {}: {}", studentId, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Unexpected error fetching student profile summary {}: {}", studentId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/exists/user/{userId}")
    public ResponseEntity<Map<String, Object>> checkStudentProfileExists(@PathVariable Long userId) {
        log.info("Checking if student profile exists for user ID: {}", userId);
        try {
            StudentProfileResponse profile = studentProfileService.getStudentProfileByUserId2(userId);
            Map<String, Object> result = new HashMap<>();
            result.put("exists", true);
            result.put("studentId", profile.getStudentId());
            result.put("status", profile.getStatusDescription());
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            Map<String, Object> result = new HashMap<>();
            result.put("exists", false);
            result.put("studentId", null);
            result.put("status", "Not Found");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Unexpected error checking student profile existence for user {}: {}", userId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/membership/{membershipType}")
    public ResponseEntity<List<StudentProfileResponse>> getStudentsByMembership(@PathVariable String membershipType) {
        log.info("Fetching students with membership: {}", membershipType);
        try {
            List<StudentProfileResponse> allProfiles = studentProfileService.getAllStudentProfiles();
            List<StudentProfileResponse> filteredProfiles = allProfiles.stream()
                    .filter(profile -> membershipType.equalsIgnoreCase(profile.getMembership()))
                    .toList();
            log.info("Found {} students with membership: {}", filteredProfiles.size(), membershipType);
            return ResponseEntity.ok(filteredProfiles);
        } catch (Exception e) {
            log.error("Error fetching students by membership {}: {}", membershipType, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // Removed duplicate /stats mapping

    @GetMapping("/{studentId}/academic-info")
    public ResponseEntity<StudentAcademicInfoDTO> getAcademicInfo(@PathVariable Long studentId) {
        try {
            return ResponseEntity.ok(studentProfileService.getAcademicInfo(studentId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{studentId}/profile-info")
    public ResponseEntity<StudentProfileInfoRespondDTO> getProfileInfo(@PathVariable Long studentId) {
        try {
            return ResponseEntity.ok(studentProfileService.getProfileInfo(studentId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{studentId}/profile-payment")
    public ResponseEntity<?> getProfilePayment(
            @PathVariable Long studentId,
            @RequestParam(name = "period", defaultValue = "all") String period) {
        log.info("Fetching payment history for studentId={}, period={}", studentId, period);
        try {
            var result = studentProfileService.getProfilePayment(studentId, period.toLowerCase());
            log.info("Payment history rows returned: {}", result.size());
            return ResponseEntity.ok(result);
        } catch (org.springframework.web.server.ResponseStatusException rse) {
            log.warn("Payment history lookup failed for studentId {}: {}", studentId, rse.getReason());
            return ResponseEntity.status(rse.getStatusCode())
                    .body(Map.of(
                            "error", "NOT_FOUND",
                            "message", rse.getReason(),
                            "studentId", studentId
                    ));
        } catch (RuntimeException e) {
            log.error("Unexpected error fetching payment history for {}: {}", studentId, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "error", "INTERNAL_ERROR",
                            "message", e.getMessage()
                    ));
        }
    }

    // New endpoint: /students/{studentId}/get-all-class-details
    @GetMapping("/{studentId}/get-all-class-details")
    public ResponseEntity<?> getAllClassDetails(@PathVariable Long studentId) {
        System.out.println("Fetching all class details for studentId: " + studentId);
        try {
            ClasssDetailResponseDto dto = studentProfileService.getAllClassDetails(studentId);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/{studentId}/upcoming-classes")
    public ResponseEntity<?> getUpcomingClasses(@PathVariable Long studentId) {
        try {
            StudentUpcomingClassResponseDto dto = studentProfileService.getUpcomingClasses(studentId);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage()));
        }
    }
    @PostMapping("/{studentId}/set-rating")
    public ResponseEntity<?> setQuickRating(@PathVariable Long studentId, @RequestBody RatingQuickRequest request) {
        try {
            java.util.Map<String, Object> resp = studentProfileService.addRatingForStudent(studentId, request);
            return ResponseEntity.ok(resp);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage()));
        }
    }
}
