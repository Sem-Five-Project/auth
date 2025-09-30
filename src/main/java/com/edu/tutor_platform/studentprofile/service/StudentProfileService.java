package com.edu.tutor_platform.studentprofile.service;


import com.edu.tutor_platform.studentprofile.dto.StudentDto;
import com.edu.tutor_platform.studentprofile.dto.StudentDtoForAdmin;
import com.edu.tutor_platform.studentprofile.dto.StudentStatsDto;
import com.edu.tutor_platform.studentprofile.dto.StudentsDto;
import com.edu.tutor_platform.studentprofile.entity.StudentProfileStatus;
import com.edu.tutor_platform.studentprofile.repository.StudentProfileRepository;
import com.edu.tutor_platform.studentprofile.entity.StudentProfile;
import com.edu.tutor_platform.user.entity.User;
import com.edu.tutor_platform.user.service.RefreshTokenService;
import com.edu.tutor_platform.studentprofile.exception.StudentNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
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

    public List<StudentsDto> getAllStudents() {
        List<StudentProfile> studentProfiles = studentProfileRepository.findAll();
        return studentProfiles.stream()
                .map(profile -> new StudentsDto(
                        profile.getStudentId(),
                        profile.getUser().getFirstName(),
                        profile.getUser().getLastName(),
                        profile.getStatus(),
                        profile.getUser().getUsername()
                ))
                .toList();
    }


    public StudentDto updateStudentProfile(String id, StudentDto studentDto) {
        Long studentId = Long.parseLong(id);
        StudentProfile studentProfile = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));
        studentProfile.setStatus(StudentProfileStatus.valueOf(String.valueOf(studentDto.getStatus())));
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
                updatedProfile.getEducationLevel()
        );
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

    public List<StudentsDto> getStudents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<StudentProfile> studentProfiles = studentProfileRepository.findAll(pageable);
        return studentProfiles.getContent().stream()
                .map(profile -> new StudentsDto(
                        profile.getStudentId(),
                        profile.getUser().getFirstName(),
                        profile.getUser().getLastName(),
                        profile.getStatus(),
                        profile.getUser().getUsername()
                ))
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
                studentProfile.getEducationLevel()
        );
    }

    public StudentDtoForAdmin updateStudentDetailsByIdForAdmin(long l, StudentDtoForAdmin studentDtoForAdmin) {
        StudentProfile studentProfile = studentProfileRepository.findById(l)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + l));

        studentProfile.setStatus(StudentProfileStatus.valueOf(String.valueOf(studentDtoForAdmin.getStatus())));
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
                updatedProfile.getEducationLevel()
        );
    }

    public StudentStatsDto getStudentStats() {
        Long totalStudents = studentProfileRepository.count();
        Long activeStudents = studentProfileRepository.countByStatus(StudentProfileStatus.ACTIVE);
        Long suspendedStudents = studentProfileRepository.countByStatus(StudentProfileStatus.SUSPENDED);
        Long newStudentsThisMonth = studentProfileRepository.countNewStudentsThisMonth();

        return new StudentStatsDto(totalStudents, activeStudents, suspendedStudents, newStudentsThisMonth);
    }

    public List<StudentsDto> searchStudentsByAdmin(String name, String username, String email, Long studentId, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<StudentProfile> studentProfiles = studentProfileRepository.searchByAdmin(
                name != null ? name : "",
                username != null ? username : "",
                email != null ? email : "",
                studentId,
                status != null ? StudentProfileStatus.valueOf(status) : null,
                pageable
        );

        return studentProfiles.getContent().stream()
                .map(profile -> new StudentsDto(
                        profile.getStudentId(),
                        profile.getUser().getFirstName(),
                        profile.getUser().getLastName(),
                        profile.getStatus(),
                        profile.getUser().getUsername()
                ))
                .toList();
    }
}
