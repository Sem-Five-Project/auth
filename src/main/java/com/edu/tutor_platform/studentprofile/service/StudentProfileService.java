package com.edu.tutor_platform.studentprofile.service;

import com.edu.tutor_platform.studentprofile.dto.StudentDto;
import com.edu.tutor_platform.studentprofile.repository.StudentProfileRepository;
import com.edu.tutor_platform.studentprofile.entity.StudentProfile;
import com.edu.tutor_platform.studentprofile.entity.StudentProfileStatus;
import com.edu.tutor_platform.user.entity.RefreshToken;
import com.edu.tutor_platform.user.entity.User;
import com.edu.tutor_platform.user.repository.RefreshTokenRepository;
import com.edu.tutor_platform.user.service.RefreshTokenService;
import com.edu.tutor_platform.studentprofile.exception.StudentNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentProfileService {

        private final StudentProfileRepository studentProfileRepository;
        private final RefreshTokenService refreshTokenService;

        @Transactional
        public void createStudentProfile(User user) {
                StudentProfile studentProfile = StudentProfile.builder()
                                .user(user)
                                .build();

                studentProfileRepository.save(studentProfile);
        }

        public List<StudentDto> getAllStudents() {
                List<StudentProfile> studentProfiles = studentProfileRepository.findAll();
                return studentProfiles.stream()
                                .map(profile -> new StudentDto(
                                                profile.getStudentId(),
                                                profile.getUser().getFirstName(),
                                                profile.getUser().getLastName(),
                                                profile.getUser().getEmail(),
                                                profile.getStatus(),
                                                profile.getUser().getCreatedAt(),
                                                profile.getUser().getLastLogin(),
                                                profile.getAdminNotes(),
                                                profile.getEducationLevel()))
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
                // Delete associated refresh tokens
                refreshTokenService.deleteByUser(studentProfile.getUser());
                // Delete student profile
                studentProfileRepository.delete(studentProfile);
        }

        public StudentProfile getStudentProfileByUserId(Long userId) {
                return studentProfileRepository.findByUserIdAndStatus(userId, StudentProfileStatus.ACTIVE)
                                .orElseThrow(() -> new StudentNotFoundException(
                                                "Active student profile not found for user ID: " + userId));
        }
}
