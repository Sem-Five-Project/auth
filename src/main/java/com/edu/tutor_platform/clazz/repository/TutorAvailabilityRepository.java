// package com.edu.tutor_platform.clazz.repository;

// import com.edu.tutor_platform.booking.entity.TutorAvailability;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository;

// import java.util.List;

// @Repository
// public interface TutorAvailabilityRepository extends JpaRepository<TutorAvailability, Long> {
//     List<TutorAvailability> findByTutorId(Integer tutorId);
// }

package com.edu.tutor_platform.clazz.repository;

import com.edu.tutor_platform.booking.entity.TutorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TutorAvailabilityRepository extends JpaRepository<TutorAvailability, Long> {
    List<TutorAvailability> findByTutorProfile_TutorId(Integer tutorId);
}