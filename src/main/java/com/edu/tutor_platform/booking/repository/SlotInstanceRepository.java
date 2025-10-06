package com.edu.tutor_platform.booking.repository;

import com.edu.tutor_platform.booking.entity.SlotInstance;
import com.edu.tutor_platform.booking.enums.SlotStatus;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    // Find locked slots that have expired (for direct slot locking)
    @Query("SELECT si FROM SlotInstance si " +
           "WHERE si.status = 'LOCKED' AND si.lockedUntil IS NOT NULL AND si.lockedUntil < :currentTime")
    List<SlotInstance> findExpiredLockedSlots(@Param("currentTime") LocalDateTime currentTime);

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

       @Modifying
       @Query("UPDATE SlotInstance si SET si.status = 'LOCKED', si.lockedUntil = :lockedUntil, si.lastReservedStudentId = :studentId WHERE si.slotId IN :slotIds AND si.status = 'AVAILABLE'")
       int lockSlots(@Param("slotIds") List<Long> slotIds, @Param("lockedUntil") LocalDateTime lockedUntil, @Param("studentId") Long studentId);

       
    @Query("select si from SlotInstance si where si.status = :status and si.lockedUntil is not null and si.lockedUntil < :cutoff")
    List<SlotInstance> findExpiredLockedSlots(@Param("status") SlotStatus status, @Param("cutoff") LocalDateTime cutoff);

              @Query("""
                                    select si from SlotInstance si
                                    join si.tutorAvailability ta
                                    where ta.recurring = true
                                           and ta.tutorProfile.tutorId = :tutorId
                                           and ta.dayOfWeek = :weekday
                                           and si.slotDate between :start and :end
                                    """)
    List<SlotInstance> findRecurringSlotsInMonth(@Param("tutorId") Long tutorId,
                                                 @Param("weekday") com.edu.tutor_platform.booking.enums.DayOfWeek weekday,
                                                 @Param("start") LocalDate start,
                                                 @Param("end") LocalDate end);


       @Lock(LockModeType.PESSIMISTIC_WRITE)
       @Query("select si from SlotInstance si where si.slotId in :ids")
       List<SlotInstance> findAllForUpdateByIds(@Param("ids") List<Long> ids);
@Query(value = """
        SELECT get_tutor_slots(:tutorId, :weekday, :month, :year)::text AS slots
        """, nativeQuery = true)
    String findTutorWeeklySlotsJson(@Param("tutorId") Long tutorId,
                                    @Param("weekday") String weekday,
                                    @Param("month") Integer month,
                                    @Param("year") Integer year);

    // Fetch next month slots for multiple availability ids.
    // Note: Passing a collection parameter to a PostgreSQL function expecting BIGINT[].
    // Spring will expand the collection; we explicitly cast to BIGINT[] to be safe.
@Query(value = """
        SELECT get_next_month_slots(CAST(:availabilityIdsArray AS BIGINT[]), :year, :month)::text AS slots
        """, nativeQuery = true)
    String findNextMonthSlotsJson(
            @Param("availabilityIdsArray") String availabilityIdsArray,
            @Param("year") Integer year,
            @Param("month") Integer month);

    // Call Supabase/PostgreSQL function check_class_exist returning JSONB
    @Query(value = "SELECT check_class_exist(:tutorId, :languageId, :subjectId, :studentId, :classType)::text", nativeQuery = true)
    String checkClassExist(
           @Param("tutorId") Long tutorId,
           @Param("languageId") Long languageId,
           @Param("subjectId") Long subjectId,
           @Param("studentId") Long studentId,
           @Param("classType") String classType);
}