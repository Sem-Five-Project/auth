package com.edu.tutor_platform.studentprofile.service;

import com.edu.tutor_platform.studentprofile.dto.StudentDto;
import com.edu.tutor_platform.studentprofile.dto.StudentDtoForAdmin;
import com.edu.tutor_platform.studentprofile.dto.StudentStatsDto;
import com.edu.tutor_platform.studentprofile.dto.StudentsDto;
import com.edu.tutor_platform.studentprofile.dto.StudentProfileResponse;
import com.edu.tutor_platform.studentprofile.dto.StudentAcademicInfoDTO;
import com.edu.tutor_platform.studentprofile.dto.StudentProfileInfoRespondDTO;
import com.edu.tutor_platform.studentprofile.dto.StudentProfilePaymentRespondDTO;
import com.edu.tutor_platform.studentprofile.dto.ClasssDetailResponseDto;
import com.edu.tutor_platform.studentprofile.dto.StudentUpcomingClassResponseDto;
import com.edu.tutor_platform.studentprofile.entity.StudentProfile;
import com.edu.tutor_platform.studentprofile.enums.StudentProfileStatus;
import com.edu.tutor_platform.studentprofile.entity.Membership;
import com.edu.tutor_platform.studentprofile.repository.StudentProfileRepository;
import com.edu.tutor_platform.user.entity.User;
import com.edu.tutor_platform.user.service.RefreshTokenService;
import com.edu.tutor_platform.studentprofile.exception.StudentNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.edu.tutor_platform.rating.dto.RatingQuickRequest;
import com.edu.tutor_platform.rating.entity.Rating;
import com.edu.tutor_platform.rating.repository.RatingRepository;
import com.edu.tutor_platform.tutorprofile.service.TutorProfileService;
import com.edu.tutor_platform.tutorprofile.entity.TutorProfile;
import com.edu.tutor_platform.session.service.SessionService;
import com.edu.tutor_platform.session.entity.Session;
import java.math.BigDecimal;

@Service
@Slf4j
public class StudentProfileService {

    private final StudentProfileRepository studentProfileRepository;
    private final RefreshTokenService refreshTokenService;
    private final ObjectMapper objectMapper;
    private final SessionService sessionService;
    private final TutorProfileService tutorProfileService;
    // participantsService removed (no participant check required per request)
    private final RatingRepository ratingRepository;

    public StudentProfileService(StudentProfileRepository studentProfileRepository, RefreshTokenService refreshTokenService,
                                 SessionService sessionService, TutorProfileService tutorProfileService,
                                 RatingRepository ratingRepository) {
        this.studentProfileRepository = studentProfileRepository;
        this.refreshTokenService = refreshTokenService;
        this.objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.sessionService = sessionService;
        this.tutorProfileService = tutorProfileService;
        this.ratingRepository = ratingRepository;
    }

    /**
     * Add a rating for a student after ensuring the student has a participant record
     * for the class associated with the provided session/class_id.
     * If no participant/session exists, does not insert and returns a message.
     */
    @Transactional
    public java.util.Map<String, Object> addRatingForStudent(Long studentId, RatingQuickRequest request) {
        java.util.Map<String, Object> result = new java.util.HashMap<>();

        // Validate rating value
        if (request.getRatingValue() == null || request.getRatingValue() < 1 || request.getRatingValue() > 5) {
            result.put("success", false);
            result.put("message", "ratingValue must be between 1 and 5");
            return result;
        }

        // Resolve student
        StudentProfile student = studentProfileRepository.findById(studentId).orElse(null);
        if (student == null) {
            result.put("success", false);
            result.put("message", "Student profile not found for id: " + studentId);
            return result;
        }

        // class_id is required
        Long classId = request.getClass_id();
        if (classId == null) {
            result.put("success", false);
            result.put("message", "class_id is required");
            return result;
        }

        // Find any sessions for this class using classId lookup
        java.util.List<Session> sessions = sessionService.getSessionsByClassId(classId);
        if (sessions == null || sessions.isEmpty()) {
            result.put("success", false);
            result.put("message", "first need to attend the class");
            return result;
        }

        // Resolve tutor
        TutorProfile tutor;
        try {
            tutor = tutorProfileService.getTutorProfileById(request.getTutorId());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Invalid or missing tutorId");
            return result;
        }

        // Use the first session found for this class
        Session session = sessions.get(0);
        com.edu.tutor_platform.clazz.entity.ClassEntity classEntity = session.getClassEntity();

        // Check duplicate rating for the same session+student; if exists, update it
        java.util.Optional<Rating> existingOpt = ratingRepository.findBySessionAndStudent(session, student);
        if (existingOpt.isPresent()) {
            Rating existing = existingOpt.get();
            existing.setRatingValue(BigDecimal.valueOf(request.getRatingValue()));
            existing.setReviewText(request.getFeedback());
            try {
                ratingRepository.save(existing);
                // update tutor average
                tutor.setRating(ratingRepository.findAverageRatingByTutor(tutor));
                tutorProfileService.save(tutor);

                result.put("success", true);
                result.put("message", "successfully updated rating");
                return result;
            } catch (Exception e) {
                result.put("success", false);
                result.put("message", "Failed to update rating: " + e.getMessage());
                return result;
            }
        }

        // Build and save rating (only store required relations and fields)
        Rating rating = Rating.builder()
                .student(student)
                .tutor(tutor)
                .session(session)
                .classEntity(classEntity)
                .ratingValue(BigDecimal.valueOf(request.getRatingValue()))
                .reviewText(request.getFeedback())
                .build();

        try {
            ratingRepository.save(rating);
            // update tutor average
            tutor.setRating(ratingRepository.findAverageRatingByTutor(tutor));
            tutorProfileService.save(tutor);

            result.put("success", true);
            result.put("message", "successfully added rating");
            return result;
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Failed to save rating: " + e.getMessage());
            return result;
        }
    }

    @Transactional
    public void createStudentProfile(User user) {
        StudentProfile studentProfile = StudentProfile.builder()
                .user(user)
                .membership(null)
                .status(StudentProfileStatus.ACTIVE)
                .build();
        studentProfileRepository.save(studentProfile);
    }

    public List<StudentsDto> getAllStudents() {
        List<StudentProfile> studentProfiles = studentProfileRepository.findAll();
        return studentProfiles.stream()
                .map(profile -> new StudentsDto(
                        profile.getStudentId(),
                        profile.getUser().getFirstName(),
                        profile.getUser().getLastName(),
                        profile.getStatus(),
                        profile.getUser().getUsername()))
                .toList();
    }

    public StudentDto updateStudentProfile(String id, StudentDto studentDto) {
        Long studentId = Long.parseLong(id);
        StudentProfile studentProfile = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));
        studentProfile.setStatus(studentDto.getStatus());
        studentProfile.setAdminNotes(studentDto.getAdminNotes());
        studentProfile.setEducationLevel(studentDto.getEducationLevel());
        StudentProfile updatedProfile = studentProfileRepository.save(studentProfile);
        return new StudentDto(
                updatedProfile.getStudentId(),
                updatedProfile.getUser().getFirstName(),
                updatedProfile.getUser().getLastName(),
                updatedProfile.getUser().getEmail(),
                updatedProfile.getStatus(),
                updatedProfile.getUser().getCreatedAt(),
                updatedProfile.getUser().getLastLogin(),
                updatedProfile.getAdminNotes(),
                updatedProfile.getEducationLevel());
    }

    public void deleteStudentProfile(String id) {
        Long studentId = Long.parseLong(id);
        StudentProfile studentProfile = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));
        refreshTokenService.deleteByUser(studentProfile.getUser());
        studentProfileRepository.delete(studentProfile);
    }

    public List<StudentsDto> getStudents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<StudentProfile> studentProfiles = studentProfileRepository.findAll(pageable);
        return studentProfiles.getContent().stream()
                .map(profile -> new StudentsDto(
                        profile.getStudentId(),
                        profile.getUser().getFirstName(),
                        profile.getUser().getLastName(),
                        profile.getStatus(),
                        profile.getUser().getUsername()))
                .toList();
    }

    public StudentDtoForAdmin getStudentDetailsByIdForAdmin(long l) {
        StudentProfile studentProfile = studentProfileRepository.findById(l)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + l));
        return new StudentDtoForAdmin(
                studentProfile.getStudentId(),
                studentProfile.getUser().getId(),
                studentProfile.getUser().getFirstName(),
                studentProfile.getUser().getLastName(),
                studentProfile.getUser().getEmail(),
                studentProfile.getUser().getUsername(),
                studentProfile.getUser().getProfileImage(),
                studentProfile.getStatus(),
                !studentProfile.getUser().isAccountNonLocked(),
                studentProfile.getAdminNotes(),
                studentProfile.getUser().getCreatedAt(),
                studentProfile.getUser().getLastLogin(),
                studentProfile.getUser().getUpdatedAt(),
                studentProfile.getEducationLevel());
    }

    public StudentDtoForAdmin updateStudentDetailsByIdForAdmin(long l, StudentDtoForAdmin studentDtoForAdmin) {
        StudentProfile studentProfile = studentProfileRepository.findById(l)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + l));
        studentProfile.setStatus(studentDtoForAdmin.getStatus());
        studentProfile.setAdminNotes(studentDtoForAdmin.getAdminNotes());
        StudentProfile updatedProfile = studentProfileRepository.save(studentProfile);
        return new StudentDtoForAdmin(
                updatedProfile.getStudentId(),
                updatedProfile.getUser().getId(),
                updatedProfile.getUser().getFirstName(),
                updatedProfile.getUser().getLastName(),
                updatedProfile.getUser().getEmail(),
                updatedProfile.getUser().getUsername(),
                updatedProfile.getUser().getProfileImage(),
                updatedProfile.getStatus(),
                !updatedProfile.getUser().isAccountNonLocked(),
                updatedProfile.getAdminNotes(),
                updatedProfile.getUser().getCreatedAt(),
                updatedProfile.getUser().getLastLogin(),
                updatedProfile.getUser().getUpdatedAt(),
                updatedProfile.getEducationLevel());
    }

    public StudentStatsDto getStudentStats() {
        Long totalStudents = studentProfileRepository.count();
        Long activeStudents = studentProfileRepository.countByStatus(StudentProfileStatus.ACTIVE);
        Long suspendedStudents = studentProfileRepository.countByStatus(StudentProfileStatus.SUSPENDED);
        Long newStudentsThisMonth = studentProfileRepository.countNewStudentsThisMonth();
        return new StudentStatsDto(totalStudents, activeStudents, suspendedStudents, newStudentsThisMonth);
    }

    public List<StudentsDto> searchStudentsByAdmin(String name, String username, String email, Long studentId,
                                                   String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<StudentProfile> studentProfiles = studentProfileRepository.searchByAdmin(
                name,
                username,
                email,
                studentId,
                status != null ? StudentProfileStatus.valueOf(status) : null,
                pageable);

        return studentProfiles.getContent().stream()
                .map(profile -> new StudentsDto(
                        profile.getStudentId(),
                        profile.getUser().getFirstName(),
                        profile.getUser().getLastName(),
                        profile.getStatus(),
                        profile.getUser().getUsername()))
                .toList();
    }

    public StudentProfile getStudentProfileByUserId(Long userId) {
        return studentProfileRepository.findByUserIdAndStatus(userId, StudentProfileStatus.ACTIVE)
                .orElseThrow(() -> new StudentNotFoundException(
                        "Active student profile not found for user ID: " + userId));
    }

    public StudentAcademicInfoDTO getAcademicInfo(Long studentId) {
        var profile = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));
        return StudentAcademicInfoDTO.builder()
                .educationLevel(profile.getEducationLevel() != null
                        ? profile.getEducationLevel().toString()
                        : null)
                .stream(profile.getStream() != null ? profile.getStream().toString() : null)
                .build();
    }

    public StudentProfileInfoRespondDTO getProfileInfo(Long studentId) {
        var profile = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));
        return StudentProfileInfoRespondDTO.builder()
                .educationLevel(profile.getEducationLevel() != null
                        ? profile.getEducationLevel().toString()
                        : null)
                .stream(profile.getStream() != null ? profile.getStream().toString() : null)
                .classCount(profile.getClassCount() != null ? profile.getClassCount() : 0)
                .sessionCount(profile.getSessionCount() != null ? profile.getSessionCount() : 0)
                .build();
    }

    public List<StudentProfilePaymentRespondDTO> getProfilePayment(Long studentId, String period) {
        if (!studentProfileRepository.existsById(studentId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Student profile not found with ID: " + studentId);
        }
        String validatedPeriod = "all".equalsIgnoreCase(period) ? "all" : (period != null ? period.toLowerCase() : "all");
        return studentProfileRepository.findPaymentHistoryByStudentId(studentId, validatedPeriod);
    }

    public StudentProfileResponse getStudentProfileById(Long studentId) {
        log.info("Fetching student profile for ID: {}", studentId);
        Optional<StudentProfile> profileOpt = studentProfileRepository.findById(studentId);
        if (profileOpt.isEmpty()) {
            log.error("Student profile not found for ID: {}", studentId);
            throw new RuntimeException("Student profile not found");
        }
        return convertToResponse(profileOpt.get());
    }

    public StudentProfileResponse getStudentProfileByUserId2(Long userId) {
        log.info("Fetching student profile for user ID: {}", userId);
        Optional<StudentProfile> profileOpt = studentProfileRepository.findByUser_Id(userId);
        if (profileOpt.isEmpty()) {
            log.error("Student profile not found for user ID: {}", userId);
            throw new RuntimeException("Student profile not found");
        }
        return convertToResponse(profileOpt.get());
    }

    public StudentProfileResponse getStudentProfileByEmail(String email) {
        log.info("Fetching student profile for email: {}", email);
        Optional<StudentProfile> profileOpt = studentProfileRepository.findByUser_Email(email);
        if (profileOpt.isEmpty()) {
            log.error("Student profile not found for email: {}", email);
            throw new RuntimeException("Student profile not found");
        }
        return convertToResponse(profileOpt.get());
    }

    public List<StudentProfileResponse> getAllStudentProfiles() {
        log.info("Fetching all student profiles");
        List<StudentProfile> profiles = studentProfileRepository.findAll();
        return profiles.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<StudentProfileResponse> getActiveStudentProfiles() {
        log.info("Fetching active student profiles");
        List<StudentProfile> profiles = studentProfileRepository.findByStatus(StudentProfileStatus.ACTIVE);
        return profiles.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Calls DB function get_student_classes_with_details(studentId) and returns mapped DTO.
     */
    public ClasssDetailResponseDto getAllClassDetails(Long studentId) {
        String jsonText = studentProfileRepository.getStudentClassesWithDetailsJson(studentId);
        if (jsonText == null || jsonText.isBlank() || "[]".equals(jsonText.trim())) {
            ClasssDetailResponseDto empty = new ClasssDetailResponseDto();
            empty.setGet_student_classes_with_details(List.of());
            empty.setGet_student_classes_with_details2(List.of());
            return empty;
        }
        try {
            // Since the DB returns a raw array, we parse it into a List of the nested type
            JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, ClasssDetailResponseDto.ClassDetail.class);
            List<ClasssDetailResponseDto.ClassDetail> details = objectMapper.readValue(jsonText, type);
            
            // Then, we wrap this list in our main response DTO. Set both fields so callers
            // reading either root key will find the parsed list.
            ClasssDetailResponseDto dto = new ClasssDetailResponseDto();
            dto.setGet_student_classes_with_details(details);
            dto.setGet_student_classes_with_details2(details);
            return dto;
        } catch (Exception e) {
            log.error("Failed to parse get_student_classes_with_details JSON for student {}: {}", studentId, e.getMessage());
            throw new RuntimeException("Failed to parse class details response");
        }
    }

    public StudentUpcomingClassResponseDto getUpcomingClasses(Long studentId) {
        String jsonText = studentProfileRepository.getStudentUpcomingClassesJson(studentId);

        if (jsonText == null || jsonText.isBlank() || "[]".equals(jsonText.trim())) {
            return StudentUpcomingClassResponseDto.builder().upcoming(false).build();
        }

        try {
            JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, StudentUpcomingClassResponseDto.UpcomingClassDetail.class);
            List<StudentUpcomingClassResponseDto.UpcomingClassDetail> details = objectMapper.readValue(jsonText, type);

            if (details.isEmpty()) {
                return StudentUpcomingClassResponseDto.builder().upcoming(false).build();
            }

            return StudentUpcomingClassResponseDto.builder()
                    .upcoming(true)
                    .upcomingClasses(details)
                    .build();
        } catch (Exception e) {
            log.error("Failed to parse get_student_upcoming_classes JSON for student {}: {}", studentId, e.getMessage());
            throw new RuntimeException("Failed to parse upcoming class details response");
        }
    }

    @Transactional
    public StudentProfileResponse updateStudentProfile(Long studentId, StudentProfileResponse updateRequest) {
        log.info("Updating student profile for ID: {}", studentId);
        Optional<StudentProfile> profileOpt = studentProfileRepository.findById(studentId);
        if (profileOpt.isEmpty()) {
            throw new RuntimeException("Student profile not found");
        }
        StudentProfile profile = profileOpt.get();
        if (updateRequest.getEducationLevel() != null) {
            profile.setEducationLevel(updateRequest.getEducationLevel());
        }
        if (updateRequest.getMembership() != null) {
            profile.setMembership(Membership.valueOf(updateRequest.getMembership()));
        }
        StudentProfile savedProfile = studentProfileRepository.save(profile);
        log.info("Successfully updated student profile: {}", studentId);
        return convertToResponse(savedProfile);
    }

    private StudentProfileResponse convertToResponse(StudentProfile profile) {
        User user = profile.getUser();
        return StudentProfileResponse.builder()
                .studentId(profile.getStudentId())
                .userId(user.getId())
                .adminNotes(profile.getAdminNotes())
                .status(profile.getStatus())
                .educationLevel(profile.getEducationLevel())
                .membership(profile.getMembership() != null ? profile.getMembership().toString() : null)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .username(user.getUsername())
                .profileImage(user.getProfileImage())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .isActive(profile.getStatus() != null && profile.getStatus() == StudentProfileStatus.ACTIVE)
                .build();
    }
}
