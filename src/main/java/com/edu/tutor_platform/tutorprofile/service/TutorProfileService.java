package com.edu.tutor_platform.tutorprofile.service;

import com.edu.tutor_platform.tutorprofile.dto.*;
import com.edu.tutor_platform.tutorprofile.entity.TutorProfile;
import com.edu.tutor_platform.tutorprofile.entity.TutorProfileStatus;
import com.edu.tutor_platform.tutorprofile.exception.TutorNotFoundException;
import com.edu.tutor_platform.tutorprofile.repository.TutorProfileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TutorProfileService {
    private final TutorProfileRepository tutorProfileRepository;

    // public List<TutorDto> getAllTutors(int page, int size) {
    // List<TutorProfile> tutorProfiles = tutorProfileRepository.findAll();
    // return tutorProfiles.stream().map(tutor -> {
    // TutorDto dto = new TutorDto();
    // dto.setTutorId(tutor.getTutorId());
    // dto.setBio(tutor.getBio());
    // dto.setHourlyRate(tutor.getHourlyRate());
    // dto.setVerified(tutor.isVerified());
    // return dto;
    // }).toList();
    //
    // }

    public TutorDto adminUpdateTutorProfile(String id, TutorDto tutorDto) {
        TutorProfile tutorProfile = tutorProfileRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new TutorNotFoundException("Tutor not found"));

        tutorProfile.setAdminNotes(tutorDto.getAdminNotes());
        tutorProfile.setVerified(tutorDto.getVerified());
        tutorProfile.setRating(tutorDto.getRating());
        tutorProfile.setStatus(tutorDto.getStatus());

        tutorProfile.setBio(tutorDto.getBio());
        tutorProfile.setHourlyRate(tutorDto.getHourlyRate());
        tutorProfile.setVerified(tutorDto.getVerified());

        TutorProfile updatedTutor = tutorProfileRepository.save(tutorProfile);

        TutorDto updatedTutorDto = new TutorDto();
        updatedTutorDto.setTutorId(updatedTutor.getTutorId());
        updatedTutorDto.setUserId(updatedTutor.getUser().getId());
        updatedTutorDto.setFirstName(updatedTutor.getUser().getFirstName());
        updatedTutorDto.setLastName(updatedTutor.getUser().getLastName());
        updatedTutorDto.setEmail(updatedTutor.getUser().getEmail());
        updatedTutorDto.setUserName(updatedTutor.getUser().getUsername());
        updatedTutorDto.setProfilePictureUrl(updatedTutor.getUser().getProfileImage());
        updatedTutorDto.setHourlyRate(updatedTutor.getHourlyRate());
        updatedTutorDto.setVerified(updatedTutor.isVerified());
        updatedTutorDto.setStatus(updatedTutor.getStatus());
        updatedTutorDto.setAccountLocked(!updatedTutor.getUser().isAccountNonLocked());
        updatedTutorDto.setAdminNotes(updatedTutor.getAdminNotes());
        updatedTutorDto.setRating(updatedTutor.getRating());
        updatedTutorDto.setExperienceInMonths(updatedTutor.getExperienceInMonths());
        updatedTutorDto.setClassCompletionRate(updatedTutor.getClassCompletionRate());

        updatedTutorDto.setBio(updatedTutor.getBio());

        return updatedTutorDto;
    }

    public void deleteTutorProfile(String id) {
        TutorProfile tutorProfile = tutorProfileRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new TutorNotFoundException("Tutor not found with id: " + id));
        tutorProfileRepository.delete(tutorProfile);
    }

    public TutorProfile getTutorById(Long tutorId) {
        return tutorProfileRepository.findById(tutorId)
                .orElseThrow(() -> new TutorNotFoundException("Tutor not found with id: " + tutorId));
    }

    public List<TutorsDto> getAllTutors(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TutorProfile> tutorPage = tutorProfileRepository.findAll(pageable);
        return getTutorsDtos(tutorPage);
    }

    public TutorDto getTutorDetailsById(long l) {
        TutorProfile tutor = tutorProfileRepository.findById(l)
                .orElseThrow(() -> new TutorNotFoundException("Tutor not found with id: " + l));

        TutorDto dto = new TutorDto();
        dto.setTutorId(tutor.getTutorId());
        dto.setUserId(tutor.getUser().getId());
        dto.setFirstName(tutor.getUser().getFirstName());
        dto.setLastName(tutor.getUser().getLastName());
        dto.setEmail(tutor.getUser().getEmail());
        dto.setUserName(tutor.getUser().getUsername());
        dto.setProfilePictureUrl(tutor.getUser().getProfileImage());
        dto.setBio(tutor.getBio());
        dto.setHourlyRate(tutor.getHourlyRate());

        // ✅ Convert TutorSubject → TutorSubjectDto
        dto.setSubjects(
                tutor.getTutorSubjects().stream()
                        .map(ts -> TutorSubjectDto.builder()
                                .id(ts.getId())
                                .subjectName(ts.getSubject().getName())
                                .hourlyRate(ts.getHourlyRate())
                                .verification(ts.getVerification() != null ? ts.getVerification().name() : null)
                                .build())
                        .toList()
        );

        dto.setStatus(tutor.getStatus());
        dto.setVerified(tutor.isVerified());
        dto.setAccountLocked(!tutor.getUser().isAccountNonLocked());
        dto.setAdminNotes(tutor.getAdminNotes());
        dto.setRating(tutor.getRating());
        dto.setExperienceInMonths(tutor.getExperienceInMonths());
        dto.setClassCompletionRate(tutor.getClassCompletionRate());
        dto.setCreatedAt(tutor.getUser().getCreatedAt());
        dto.setLastLogin(tutor.getUser().getLastLogin());
        dto.setUpdatedAt(tutor.getUser().getUpdatedAt());
        return dto;
    }


    public List<TutorsDto> searchTutorsByAdmin(String name, String username, String email,
            Long tutorId, String status,
            Boolean verified, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("tutorId").descending());

        String namePattern = (name == null || name.isBlank()) ? null : "%" + name.toLowerCase() + "%";
        String usernamePattern = (username == null || username.isBlank()) ? null : "%" + username.toLowerCase() + "%";
        String emailPattern = (email == null || email.isBlank()) ? null : "%" + email.toLowerCase() + "%";

        Page<TutorProfile> tutorPage = tutorProfileRepository.searchByAdmin(
                namePattern, usernamePattern, emailPattern, tutorId, status, verified, pageable);

        return getTutorsDtos(tutorPage);
    }

    private List<TutorsDto> getTutorsDtos(Page<TutorProfile> tutorPage) {
        return tutorPage.stream().map(tutor -> {
            TutorsDto dto = new TutorsDto();
            dto.setTutorId(tutor.getTutorId());
            dto.setHourlyRate(tutor.getHourlyRate());
            dto.setVerified(tutor.isVerified());
            dto.setFirstName(tutor.getUser().getFirstName());
            dto.setLastName(tutor.getUser().getLastName());
            dto.setStatus(tutor.getStatus());
            return dto;
        }).toList();
    }

    public TutorStatsDto getTutorStatistics() {

        long totalTutors = tutorProfileRepository.count();
        long verifiedTutors = tutorProfileRepository.count((root, query, cb) -> cb.isTrue(root.get("verified")));
        long activeTutors = tutorProfileRepository.count((root, query, cb) -> cb.equal(root.get("status"), "ACTIVE"));
        double avarageRating = tutorProfileRepository.findAll().stream()
                .map(TutorProfile::getRating)
                .filter(rating -> rating != null)
                .mapToDouble(rating -> rating.floatValue())
                .average()
                .orElse(0.0);

        long newTutorsLastMonth = tutorProfileRepository.findAll().stream()
                .filter(tutor -> tutor.getUser().getCreatedAt() != null &&
                        tutor.getUser().getCreatedAt().isAfter(java.time.LocalDateTime.now().minusMonths(1)))
                .count();

        TutorStatsDto stats = new TutorStatsDto();
        stats.setTotalTutors(totalTutors);
        stats.setVerifiedTutors(verifiedTutors);
        stats.setActiveTutors(activeTutors);
        stats.setAverageRating(avarageRating);
        stats.setNewTutorsThisMonth(newTutorsLastMonth);

        return stats;
    }

    public List<TutorApprovalsDto> getPendingTutorApprovals() {
        List<TutorProfile> pendingTutors = tutorProfileRepository.findByStatusIsNull();
        return pendingTutors.stream().map(tutor -> {
            TutorApprovalsDto dto = new TutorApprovalsDto();
            dto.setTutorId(tutor.getTutorId());
            dto.setUserName(tutor.getUser().getUsername());
            List<SubjectInfoDto> subjectNames = tutor.getTutorSubjects().stream()
                    .map(subject -> new SubjectInfoDto(subject.getSubject().getSubjectId(),
                            subject.getSubject().getName(), subject.getVerificationDocs()))
                    .toList();
            dto.setSubjects(subjectNames);
            dto.setSubmissionDate(tutor.getUser().getUpdatedAt());
            return dto;
        }).toList();
    }

    public List<ReverificationsDto> getReverificationRequests() {
        List<TutorProfile> reverifyTutors = tutorProfileRepository.findByTutorsHavePendingSubjects();
        return reverifyTutors.stream().map(tutor -> {
            ReverificationsDto dto = new ReverificationsDto();
            dto.setTutorId(tutor.getTutorId());
            dto.setUserName(tutor.getUser().getUsername());
            List<SubjectInfoDto> subjects = tutor.getTutorSubjects().stream()
                    .filter(subject -> "PENDING".equals(subject.getVerification()))
                    .map(subject -> new SubjectInfoDto(
                            subject.getSubject().getSubjectId(),
                            subject.getSubject().getName(),
                            subject.getVerificationDocs()))
                    .toList();

            dto.setSubjectInfo(subjects);
            return dto;
        }).toList();
    }

    public TutorProfile getTutorProfileById(Long tutorId) {
        return tutorProfileRepository.findById(tutorId)
                .orElseThrow(() -> new TutorNotFoundException("Tutor not found with id: " + tutorId));
    }

    @Transactional
    public TutorProfile save(TutorProfile tutorProfile) {
        return tutorProfileRepository.save(tutorProfile);
    }
}
