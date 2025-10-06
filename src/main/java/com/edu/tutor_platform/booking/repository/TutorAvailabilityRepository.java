// package com.edu.tutor_platform.booking.repository;

// import com.edu.tutor_platform.booking.entity.TutorAvailability;
// import com.edu.tutor_platform.booking.enums.DayOfWeek;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
// import org.springframework.stereotype.Repository;

// import java.util.List;

// @Repository
// public interface TutorAvailabilityRepository extends JpaRepository<TutorAvailability, Long> {

//     // Find all availability for a specific tutor
//     List<TutorAvailability> findByTutorProfileTutorId(Long tutorId);

//     // Find availability by tutor and day of week
//     List<TutorAvailability> findByTutorProfileTutorIdAndDayOfWeek(Long tutorId, DayOfWeek dayOfWeek);

//     // Find recurring slots for all tutors (for automatic generation)
//     List<TutorAvailability> findByRecurringTrue();

//     // Find recurring slots for a specific tutor
//     List<TutorAvailability> findByTutorProfileTutorIdAndRecurringTrue(Long tutorId);

//     // Check if tutor has availability on a specific day and time range
//     @Query("SELECT ta FROM TutorAvailability ta WHERE ta.tutorProfile.tutorId = :tutorId " +
//            "AND ta.dayOfWeek = :dayOfWeek " +
//            "AND ta.startTime <= :endTime AND ta.endTime >= :startTime")
//     List<TutorAvailability> findOverlappingAvailability(
//         @Param("tutorId") Long tutorId,
//         @Param("dayOfWeek") DayOfWeek dayOfWeek,
//         @Param("startTime") java.time.LocalTime startTime,
//         @Param("endTime") java.time.LocalTime endTime
//     );

//     // Delete availability by tutor ID
//     void deleteByTutorProfileTutorId(Long tutorId);

//     // Find availability that needs slot generation (recurring slots)
//     @Query("SELECT ta FROM TutorAvailability ta WHERE ta.recurring = true " +
//            "AND ta.tutorProfile.tutorId = :tutorId")
//     List<TutorAvailability> findRecurringAvailabilityForTutor(@Param("tutorId") Long tutorId);
// }