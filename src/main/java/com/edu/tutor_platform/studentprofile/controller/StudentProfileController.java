package com.edu.tutor_platform.studentprofile.controller;

import com.edu.tutor_platform.studentprofile.dto.StudentAcademicInfoDTO;
import com.edu.tutor_platform.studentprofile.dto.StudentProfileResponse;
import com.edu.tutor_platform.studentprofile.service.StudentProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/student/profile")
@RequiredArgsConstructor
@Slf4j
public class StudentProfileController {

    private final StudentProfileService studentProfileService;

    /**
     * Get student profile by student ID
     */
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

    /**
     * Get student profile by user ID
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<StudentProfileResponse> getStudentProfileByUserId(@PathVariable Long userId) {
        log.info("Fetching student profile for user ID: {}", userId);
        
        try {
            StudentProfileResponse profile = studentProfileService.getStudentProfileByUserId(userId);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            log.error("Error fetching student profile for user {}: {}", userId, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Unexpected error fetching student profile for user {}: {}", userId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get student profile by email
     */
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

    /**
     * Get all student profiles (for admin use)
     */
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

    /**
     * Get active student profiles only
     */
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

    /**
     * Update student profile
     */
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

    /**
     * Get student profile summary/stats
     */
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

    /**
     * Check if student profile exists for user
     */
    @GetMapping("/exists/user/{userId}")
    public ResponseEntity<Map<String, Object>> checkStudentProfileExists(@PathVariable Long userId) {
        log.info("Checking if student profile exists for user ID: {}", userId);
        
        try {
            StudentProfileResponse profile = studentProfileService.getStudentProfileByUserId(userId);
            
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

    /**
     * Get student profiles by membership type
     */
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

    /**
     * Get student profile statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStudentProfileStats() {
        log.info("Fetching student profile statistics");
        
        try {
            List<StudentProfileResponse> allProfiles = studentProfileService.getAllStudentProfiles();
            List<StudentProfileResponse> activeProfiles = studentProfileService.getActiveStudentProfiles();
            
            long totalStudents = allProfiles.size();
            long activeStudents = activeProfiles.size();
            long inactiveStudents = totalStudents - activeStudents;
            
            // Count by membership
            long plusMembers = allProfiles.stream().filter(p -> "PLUS".equals(p.getMembership())).count();
            long proMembers = allProfiles.stream().filter(p -> "PRO".equals(p.getMembership())).count();
            long freeMembers = allProfiles.stream().filter(p -> p.getMembership() == null || "NULL".equals(p.getMembership())).count();
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalStudents", totalStudents);
            stats.put("activeStudents", activeStudents);
            stats.put("inactiveStudents", inactiveStudents);
            stats.put("activationRate", totalStudents > 0 ? (double) activeStudents / totalStudents * 100 : 0);
            
            Map<String, Long> membershipStats = new HashMap<>();
            membershipStats.put("FREE", freeMembers);
            membershipStats.put("PLUS", plusMembers);
            membershipStats.put("PRO", proMembers);
            stats.put("membershipDistribution", membershipStats);
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error fetching student profile statistics: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    @GetMapping("/{studentId}/academic-info")
    public ResponseEntity<StudentAcademicInfoDTO> getAcademicInfo(@PathVariable Long studentId) {
        log.info("Fetching academic info for studenttt {}", studentId);
        try {
            return ResponseEntity.ok(studentProfileService.getAcademicInfo(studentId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}