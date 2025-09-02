package com.edu.tutor_platform.booking.repository;

import com.edu.tutor_platform.booking.entity.SlotInstance;
import com.edu.tutor_platform.booking.enums.SlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SlotInstanceRepository extends JpaRepository<SlotInstance, Long> {

    // Find slots by tutor ID and date
    @Query("SELECT si FROM SlotInstance si JOIN si.tutorAvailability ta " +
           "WHERE ta.tutorProfile.tutorId = :tutorId AND si.slotDate = :date")
    List<SlotInstance> findByTutorIdAndDate(@Param("tutorId") Long tutorId, @Param("date") LocalDate date);

    // Find available slots for a tutor on a specific date
    @Query("SELECT si FROM SlotInstance si JOIN si.tutorAvailability ta " +
           "WHERE ta.tutorProfile.tutorId = :tutorId AND si.slotDate = :date AND si.status = :status")
    List<SlotInstance> findByTutorIdAndDateAndStatus(
        @Param("tutorId") Long tutorId, 
        @Param("date") LocalDate date, 
        @Param("status") SlotStatus status
    );

    // Find slots by tutor ID within date range
    @Query("SELECT si FROM SlotInstance si JOIN si.tutorAvailability ta " +
           "WHERE ta.tutorProfile.tutorId = :tutorId " +
           "AND si.slotDate BETWEEN :startDate AND :endDate")
    List<SlotInstance> findByTutorIdAndDateRange(
        @Param("tutorId") Long tutorId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    // Find available slots for a tutor within date range
    @Query("SELECT si FROM SlotInstance si JOIN si.tutorAvailability ta " +
           "WHERE ta.tutorProfile.tutorId = :tutorId " +
           "AND si.slotDate BETWEEN :startDate AND :endDate " +
           "AND si.status = 'AVAILABLE'")
    List<SlotInstance> findAvailableSlotsByTutorAndDateRange(
        @Param("tutorId") Long tutorId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    // Find slots by availability ID and date (used for checking if slot already exists)
    List<SlotInstance> findByTutorAvailabilityAvailabilityIdAndSlotDate(Long availabilityId, LocalDate slotDate);

    // Find all slots for a specific date (for administrative purposes)
    List<SlotInstance> findBySlotDate(LocalDate date);

    // Find locked slots that have expired
    @Query("SELECT si FROM SlotInstance si JOIN si.bookings b " +
           "WHERE si.status = 'LOCKED' AND b.lockedUntil < CURRENT_TIMESTAMP")
    List<SlotInstance> findExpiredLockedSlots();

    // Find slots by status
    List<SlotInstance> findByStatus(SlotStatus status);

    // Delete slots by availability ID (used when tutor deletes availability)
    void deleteByTutorAvailabilityAvailabilityId(Long availabilityId);

    // Check if slot exists for a specific availability and date
    boolean existsByTutorAvailabilityAvailabilityIdAndSlotDate(Long availabilityId, LocalDate slotDate);

    // Find slots by tutor and date range for slot generation
    @Query("SELECT si FROM SlotInstance si JOIN si.tutorAvailability ta " +
           "WHERE ta.tutorProfile.tutorId = :tutorId " +
           "AND si.slotDate BETWEEN :startDate AND :endDate " +
           "ORDER BY si.slotDate")
    List<SlotInstance> findSlotsForGeneration(
        @Param("tutorId") Long tutorId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}