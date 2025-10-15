package com.edu.tutor_platform.clazz.service;

import com.edu.tutor_platform.clazz.entity.ClassEntity;
import com.edu.tutor_platform.clazz.entity.Participants;
import com.edu.tutor_platform.clazz.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParticipantsService {
    private final ParticipantRepository participantRepository;

    @Autowired
    public ParticipantsService(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    public List<Participants> findByClassEntity(ClassEntity classEntity) {
        List<Participants> participants = participantRepository.findByClassEntity(classEntity);
        return participants;

    }

    public List<Participants> findByStudent(com.edu.tutor_platform.studentprofile.entity.StudentProfile student) {
        return participantRepository.findByStudent(student);
    }

    public List<com.edu.tutor_platform.clazz.entity.ClassEntity> findClassesByStudentId(Long studentId) {
        List<Participants> participants = participantRepository.findByStudentStudentId(studentId);
        return participants.stream().map(Participants::getClassEntity).distinct().toList();
    }
}
