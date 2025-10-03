package com.edu.tutor_platform.studentprofile.service;

import com.edu.tutor_platform.studentprofile.repository.StudentProfileRepository;
import com.edu.tutor_platform.studentprofile.entity.StudentProfile;
import com.edu.tutor_platform.studentprofile.enums.StudentProfileStatus;
import com.edu.tutor_platform.studentprofile.dto.StudentProfileResponse;
import com.edu.tutor_platform.studentprofile.entity.Membership;
import com.edu.tutor_platform.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.edu.tutor_platform.studentprofile.dto.StudentAcademicInfoDTO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentProfileService {

    private final StudentProfileRepository studentProfileRepository;

    @Transactional
    public void createStudentProfile(User user) {
        StudentProfile studentProfile = StudentProfile.builder()
                .user(user)
                .membership(null)
                .build();

        studentProfileRepository.save(studentProfile);
    }
    public StudentAcademicInfoDTO getAcademicInfo(Long studentId) {
        var profile = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        // Adjust field access (profile.getStream() if exists, else null)
        return StudentAcademicInfoDTO.builder()
                .educationLevel(profile.getEducationLevel() != null
                        ? profile.getEducationLevel().toString()
                        : null)
                .stream(profile.getStream() != null ? profile.getStream().toString() : null)
                .build();
    }
    /**
     * Get student profile by student ID
     */
    public StudentProfileResponse getStudentProfileById(Long studentId) {
        log.info("Fetching student profile for ID: {}", studentId);
        
        Optional<StudentProfile> profileOpt = studentProfileRepository.findById(studentId);
        if (profileOpt.isEmpty()) {
            log.error("Student profile not found for ID: {}", studentId);
            throw new RuntimeException("Student profile not found");
        }
        
        return convertToResponse(profileOpt.get());
    }
    
    /**
     * Get student profile by user ID
     */
    public StudentProfileResponse getStudentProfileByUserId(Long userId) {
        log.info("Fetching student profile for user ID: {}", userId);
        
        Optional<StudentProfile> profileOpt = studentProfileRepository.findByUser_Id(userId);
        if (profileOpt.isEmpty()) {
            log.error("Student profile not found for user ID: {}", userId);
            throw new RuntimeException("Student profile not found");
        }
        
        return convertToResponse(profileOpt.get());
    }
    
    /**
     * Get student profile by email
     */
    public StudentProfileResponse getStudentProfileByEmail(String email) {
        log.info("Fetching student profile for email: {}", email);
        
        Optional<StudentProfile> profileOpt = studentProfileRepository.findByUser_Email(email);
        if (profileOpt.isEmpty()) {
            log.error("Student profile not found for email: {}", email);
            throw new RuntimeException("Student profile not found");
        }
        
        return convertToResponse(profileOpt.get());
    }
    
    /**
     * Get all student profiles with pagination
     */
    public List<StudentProfileResponse> getAllStudentProfiles() {
        log.info("Fetching all student profiles");
        
        List<StudentProfile> profiles = studentProfileRepository.findAll();
        return profiles.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get active student profiles only
     */
    public List<StudentProfileResponse> getActiveStudentProfiles() {
        log.info("Fetching active student profiles");
        
        List<StudentProfile> profiles = studentProfileRepository.findByStatus(StudentProfileStatus.ACTIVE); 

        return profiles.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Update student profile
     */
    @Transactional
    public StudentProfileResponse updateStudentProfile(Long studentId, StudentProfileResponse updateRequest) {
        log.info("Updating student profile for ID: {}", studentId);
        
        Optional<StudentProfile> profileOpt = studentProfileRepository.findById(studentId);
        if (profileOpt.isEmpty()) {
            throw new RuntimeException("Student profile not found");
        }
        
        StudentProfile profile = profileOpt.get();
        
        // Update only the allowed fields
        if (updateRequest.getEducationLevel() != null) {
            profile.setEducationLevel(updateRequest.getEducationLevel());
        }
        // if (updateRequest.getEducationalLevel() != null) {
        //     profile.setEducationalLevel(updateRequest.getEducationalLevel());
        // }
        if (updateRequest.getMembership() != null) {
            profile.setMembership(Membership.valueOf(updateRequest.getMembership()));
        }
        
        StudentProfile savedProfile = studentProfileRepository.save(profile);
        log.info("Successfully updated student profile: {}", studentId);
        
        return convertToResponse(savedProfile);
    }
    
    /**
     * Convert entity to response DTO
     */
    private StudentProfileResponse convertToResponse(StudentProfile profile) {
        User user = profile.getUser();
        
        return StudentProfileResponse.builder()
                .studentId(profile.getStudentId())
                .userId(user.getId())
                .adminNotes(profile.getAdminNotes())
                .status(profile.getStatus())
                // .educationalLevel(profile.getEducationalLevel())
                .educationLevel(profile.getEducationLevel())
                .membership(profile.getMembership() != null ? profile.getMembership().toString() : null)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .username(user.getUsername())
                .profileImage(user.getProfileImage())
                .fullName(user.getFirstName() + " " + user.getLastName())
                //below old
                //.statusDescription(getStatusDescription(profile.getStatus()))
                .isActive(profile.getStatus() != null && profile.getStatus() == StudentProfileStatus.ACTIVE)
                .build();
    }
    
    /**
     * Get human-readable status description
     */
    private String getStatusDescription(Short status) {
        if (status == null) return "Unknown";
        
        return switch (status) {
            case 0 -> "Inactive";
            case 1 -> "Active";
            case 2 -> "Suspended";
            case 3 -> "Pending";
            default -> "Unknown";
        };
    }
}

