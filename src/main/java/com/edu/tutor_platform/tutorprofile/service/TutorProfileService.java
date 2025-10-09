package com.edu.tutor_platform.tutorprofile.service;

import com.edu.tutor_platform.tutorprofile.dto.TutorDto;
import com.edu.tutor_platform.tutorprofile.entity.TutorProfile;
import com.edu.tutor_platform.tutorprofile.exception.TutorNotFoundException;
import com.edu.tutor_platform.tutorprofile.repository.TutorProfileRepository;
import com.edu.tutor_platform.user.entity.RefreshToken;
import com.edu.tutor_platform.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TutorProfileService {
    private final TutorProfileRepository tutorProfileRepository;

    public List<TutorDto> getAllTutors() {
        List<TutorProfile> tutorProfiles = tutorProfileRepository.findAll();
        return tutorProfiles.stream().map(tutor -> {
            TutorDto dto = new TutorDto();
            dto.setTutorId(tutor.getTutorId());
            dto.setBio(tutor.getBio());
            dto.setHourlyRate(tutor.getHourlyRate());
            dto.setVerified(tutor.isVerified());
            return dto;
        }).toList();

    }

    public TutorDto updateTutorProfile(String id, TutorDto tutorDto) {
        TutorProfile tutorProfile = tutorProfileRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new TutorNotFoundException("Tutor not found"));

        tutorProfile.setBio(tutorDto.getBio());
        tutorProfile.setHourlyRate(tutorDto.getHourlyRate());
        tutorProfile.setVerified(tutorDto.isVerified());

        TutorProfile updatedTutor = tutorProfileRepository.save(tutorProfile);

        TutorDto updatedTutorDto = new TutorDto();
        updatedTutorDto.setTutorId(updatedTutor.getTutorId());
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

    public TutorProfile getTutorProfileById(Long tutorId) {
        return tutorProfileRepository.findById(tutorId)
                .orElseThrow(() -> new TutorNotFoundException("Tutor not found with id: " + tutorId));
    }

    @Transactional
    public TutorProfile save(TutorProfile tutorProfile) {
        return tutorProfileRepository.save(tutorProfile);
    }
}
