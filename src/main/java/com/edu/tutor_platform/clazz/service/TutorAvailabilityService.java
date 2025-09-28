package com.edu.tutor_platform.clazz.service;

import com.edu.tutor_platform.booking.entity.TutorAvailability;
import com.edu.tutor_platform.clazz.repository.TutorAvailabilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TutorAvailabilityService {
    @Autowired
    private TutorAvailabilityRepository repository;

    public TutorAvailability createAvailability(TutorAvailability availability) {
        return repository.save(availability);
    }

    public void deleteAvailability(Long availabilityId) {
        repository.deleteById(availabilityId);
    }

    public TutorAvailability updateAvailability(Long availabilityId, TutorAvailability updated) {
        Optional<TutorAvailability> optional = repository.findById(availabilityId);
        if (optional.isPresent()) {
            TutorAvailability existing = optional.get();
            existing.setDayOfWeek(updated.getDayOfWeek());
            existing.setStartTime(updated.getStartTime());
            existing.setEndTime(updated.getEndTime());
            existing.setRecurring(updated.getRecurring());
            return repository.save(existing);
        }
        return null;
    }

    public List<TutorAvailability> getByTutorId(Integer tutorId) {
        
        return repository.findByTutorProfile_TutorId(tutorId);
    }
}
