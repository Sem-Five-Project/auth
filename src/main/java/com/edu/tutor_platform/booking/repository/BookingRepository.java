package com.edu.tutor_platform.booking.repository;

import com.edu.tutor_platform.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Find bookings by student ID
    List<Booking> findByStudentProfileStudentId(Long studentId);

    // Find bookings by slot ID
    List<Booking> findBySlotInstanceSlotId(Long slotId);

    // Find confirmed bookings by student
    List<Booking> findByStudentProfileStudentIdAndIsConfirmedTrue(Long studentId);

    // Find unconfirmed bookings by student
    List<Booking> findByStudentProfileStudentIdAndIsConfirmedFalse(Long studentId);

    // Find bookings by tutor (through slot instance)
    @Query("SELECT b FROM Booking b JOIN b.slotInstance si JOIN si.tutorAvailability ta " +
           "WHERE ta.tutorProfile.tutorId = :tutorId")
    List<Booking> findByTutorId(@Param("tutorId") Long tutorId);

    // Find confirmed bookings by tutor
    @Query("SELECT b FROM Booking b JOIN b.slotInstance si JOIN si.tutorAvailability ta " +
           "WHERE ta.tutorProfile.tutorId = :tutorId AND b.isConfirmed = true")
    List<Booking> findConfirmedBookingsByTutorId(@Param("tutorId") Long tutorId);

    // Find expired locked bookings (for cleanup)
    @Query("SELECT b FROM Booking b WHERE b.lockedUntil < :currentTime AND b.isConfirmed = false")
    List<Booking> findExpiredLockedBookings(@Param("currentTime") LocalDateTime currentTime);

    // Find active reservations (locked but not expired)
    @Query("SELECT b FROM Booking b WHERE b.lockedUntil > :currentTime AND b.isConfirmed = false")
    List<Booking> findActiveReservations(@Param("currentTime") LocalDateTime currentTime);

    // Find booking by slot and student
    Optional<Booking> findBySlotInstanceSlotIdAndStudentProfileStudentId(Long slotId, Long studentId);

    // Find bookings within a date range for a student
    @Query("SELECT b FROM Booking b JOIN b.slotInstance si " +
           "WHERE b.studentProfile.studentId = :studentId " +
           "AND si.slotDate BETWEEN :startDate AND :endDate")
    List<Booking> findByStudentAndDateRange(
        @Param("studentId") Long studentId,
        @Param("startDate") java.time.LocalDate startDate,
        @Param("endDate") java.time.LocalDate endDate
    );

    // Find bookings within a date range for a tutor
    @Query("SELECT b FROM Booking b JOIN b.slotInstance si JOIN si.tutorAvailability ta " +
           "WHERE ta.tutorProfile.tutorId = :tutorId " +
           "AND si.slotDate BETWEEN :startDate AND :endDate")
    List<Booking> findByTutorAndDateRange(
        @Param("tutorId") Long tutorId,
        @Param("startDate") java.time.LocalDate startDate,
        @Param("endDate") java.time.LocalDate endDate
    );

    // Find bookings that need payment processing (confirmed but no payment record)
    @Query("SELECT b FROM Booking b WHERE b.isConfirmed = true AND b.payment IS NULL")
    List<Booking> findBookingsNeedingPayment();

    // Find bookings by payment status
    @Query("SELECT b FROM Booking b JOIN b.payment p WHERE p.status = :paymentStatus")
    List<Booking> findByPaymentStatus(@Param("paymentStatus") String paymentStatus);

    // Delete expired unconfirmed bookings
    void deleteByLockedUntilBeforeAndIsConfirmedFalse(LocalDateTime expirationTime);
    
    // Find booking by slot, student and confirmation status (for slot blocking)
    Optional<Booking> findBySlotInstanceAndStudentProfileAndIsConfirmed(
            com.edu.tutor_platform.booking.entity.SlotInstance slotInstance,
            com.edu.tutor_platform.studentprofile.entity.StudentProfile studentProfile,
            Boolean isConfirmed);
    
    // Find expired unconfirmed bookings (alias for cleanup)
    @Query("SELECT b FROM Booking b WHERE b.isConfirmed = false AND b.lockedUntil < :now")
    List<Booking> findExpiredUnconfirmedBookings(@Param("now") LocalDateTime now);
    
    // Find booking by order ID (for payment processing)
    @Query("SELECT b FROM Booking b JOIN b.payment p WHERE p.orderId = :orderId")
    Optional<Booking> findByOrderId(@Param("orderId") String orderId);
    
    // Check if student has any pending payments
    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.studentProfile.studentId = :studentId AND b.isConfirmed = false AND b.lockedUntil > :now")
    Boolean hasActivePendingBookings(@Param("studentId") Long studentId, @Param("now") LocalDateTime now);

       // Call DB function get_student_bookings returning a single JSON array string
       @Query(value = "SELECT COALESCE(jsonb_agg(row_to_json(t)), '[]'::jsonb)::text FROM public.get_student_bookings(:studentId) t", nativeQuery = true)
       String findStudentBookingsJson(@Param("studentId") Long studentId);
}