//package com.edu.tutor_platform.booking.service;
//
//import com.edu.tutor_platform.booking.entity.SlotInstance;
//import com.edu.tutor_platform.booking.entity.Booking;
//import com.edu.tutor_platform.booking.enums.SlotStatus;
//import com.edu.tutor_platform.booking.repository.SlotInstanceRepository;
//import com.edu.tutor_platform.booking.repository.BookingRepository;
//import com.edu.tutor_platform.studentprofile.entity.StudentProfile;
//import com.edu.tutor_platform.studentprofile.repository.StudentProfileRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class SlotBlockingService {
//
//    private final SlotInstanceRepository slotInstanceRepository;
//    private final BookingRepository bookingRepository;
//    private final StudentProfileRepository studentProfileRepository;
//
//    // Block duration for payment processing (15 minutes)
//    private static final int BLOCK_DURATION_MINUTES = 15;
//
//    /**
//     * Blocks a slot for a student to make payment within 15 minutes
//     *
//     * @param slotId The slot to block
//     * @param studentId The student who wants to book
//     * @return The booking ID if successful, null if slot is not available
//     */
//    @Transactional
//    public Long blockSlotForPayment(Long slotId, Long studentId, String orderId) {
//        log.info("Attempting to block slot {} for student {} with order {}", slotId, studentId, orderId);
//
//        // Find the slot
//        Optional<SlotInstance> slotOpt = slotInstanceRepository.findById(slotId);
//        if (slotOpt.isEmpty()) {
//            log.error("Slot {} not found", slotId);
//            throw new RuntimeException("Slot not found");
//        }
//
//        SlotInstance slot = slotOpt.get();
//
//        // Check if slot is available
//        if (slot.getStatus() != SlotStatus.AVAILABLE) {
//            log.error("Slot {} is not available, current status: {}", slotId, slot.getStatus());
//            throw new RuntimeException("Slot is not available for booking");
//        }
//
//        // Find student profile
//        Optional<StudentProfile> studentOpt = studentProfileRepository.findById(studentId);
//        if (studentOpt.isEmpty()) {
//            log.error("Student {} not found", studentId);
//            throw new RuntimeException("Student not found");
//        }
//
//        StudentProfile student = studentOpt.get();
//
//        // Check if student already has a pending booking for this slot
//        Optional<Booking> existingBooking = bookingRepository.findBySlotInstanceAndStudentProfileAndIsConfirmed(slot, student, false);
//        if (existingBooking.isPresent() && existingBooking.get().getLockedUntil().isAfter(LocalDateTime.now())) {
//            log.error("Student {} already has a pending booking for slot {}", studentId, slotId);
//            throw new RuntimeException("You already have a pending payment for this slot");
//        }
//
//        // Block the slot
//        slot.setStatus(SlotStatus.LOCKED);
//        slotInstanceRepository.save(slot);
//
//        // Create a pending booking with lock time
//        LocalDateTime lockExpiration = LocalDateTime.now().plusMinutes(BLOCK_DURATION_MINUTES);
//
//        Booking booking = Booking.builder()
//                .studentProfile(student)
//                .slotInstance(slot)
//                .createdAt(LocalDateTime.now())
//                .lockedUntil(lockExpiration)
//                .isConfirmed(false)
//                .build();
//
//        Booking savedBooking = bookingRepository.save(booking);
//
//        log.info("Successfully blocked slot {} for student {} until {}. Booking ID: {}",
//                slotId, studentId, lockExpiration, savedBooking.getBookingId());
//
//        return savedBooking.getBookingId();
//    }
//
//    /**
//     * Confirms a booking when payment is successful
//     *
//     * @param bookingId The booking to confirm
//     * @param orderId The payment order ID
//     */
//    @Transactional
//    public void confirmBooking(Long bookingId, String orderId) {
//        log.info("Confirming booking {} for order {}", bookingId, orderId);
//
//        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
//        if (bookingOpt.isEmpty()) {
//            log.error("Booking {} not found", bookingId);
//            throw new RuntimeException("Booking not found");
//        }
//
//        Booking booking = bookingOpt.get();
//
//        // Check if booking is still valid (not expired)
//        if (booking.getLockedUntil().isBefore(LocalDateTime.now())) {
//            log.error("Booking {} has expired at {}", bookingId, booking.getLockedUntil());
//            // Release the slot and delete the expired booking
//            releaseExpiredBooking(booking);
//            throw new RuntimeException("Booking has expired. Please try booking again.");
//        }
//
//        // Confirm the booking
//        booking.setIsConfirmed(true);
//        booking.setLockedUntil(null); // Remove lock time since it's confirmed
//
//        // Update slot status to BOOKED
//        SlotInstance slot = booking.getSlotInstance();
//        slot.setStatus(SlotStatus.BOOKED);
//
//        bookingRepository.save(booking);
//        slotInstanceRepository.save(slot);
//
//        log.info("Successfully confirmed booking {} for order {}", bookingId, orderId);
//    }
//
//    /**
//     * Cancels a booking and releases the slot
//     *
//     * @param bookingId The booking to cancel
//     * @param reason The cancellation reason
//     */
//    @Transactional
//    public void cancelBooking(Long bookingId, String reason) {
//        log.info("Cancelling booking {} - Reason: {}", bookingId, reason);
//
//        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
//        if (bookingOpt.isEmpty()) {
//            log.error("Booking {} not found", bookingId);
//            throw new RuntimeException("Booking not found");
//        }
//
//        Booking booking = bookingOpt.get();
//        releaseExpiredBooking(booking);
//
//        log.info("Successfully cancelled booking {}", bookingId);
//    }
//
//    /**
//     * Releases an expired booking and makes the slot available again
//     */
//    private void releaseExpiredBooking(Booking booking) {
//        // Release the slot
//        SlotInstance slot = booking.getSlotInstance();
//        slot.setStatus(SlotStatus.AVAILABLE);
//        slotInstanceRepository.save(slot);
//
//        // Delete the expired booking
//        bookingRepository.delete(booking);
//
//        log.info("Released slot {} and deleted expired booking {}",
//                slot.getSlotId(), booking.getBookingId());
//    }
//
//    /**
//     * Scheduled task to clean up expired slot blocks every 5 minutes
//     */
//    @Scheduled(fixedRate = 300000) // Run every 5 minutes
//    @Transactional
//    public void cleanupExpiredSlotBlocks() {
//        log.debug("Running cleanup of expired slot blocks");
//
//        LocalDateTime now = LocalDateTime.now();
//
//        // Find all expired, unconfirmed bookings
//        List<Booking> expiredBookings = bookingRepository.findExpiredUnconfirmedBookings(now);
//
//        if (!expiredBookings.isEmpty()) {
//            log.info("Found {} expired bookings to cleanup", expiredBookings.size());
//
//            for (Booking booking : expiredBookings) {
//                try {
//                    releaseExpiredBooking(booking);
//                    log.info("Cleaned up expired booking {} for slot {}",
//                            booking.getBookingId(), booking.getSlotInstance().getSlotId());
//                } catch (Exception e) {
//                    log.error("Error cleaning up expired booking {}: {}",
//                            booking.getBookingId(), e.getMessage());
//                }
//            }
//        }
//    }
//
//    /**
//     * Gets the remaining lock time for a booking
//     */
//    public Optional<LocalDateTime> getLockExpirationTime(Long bookingId) {
//        return bookingRepository.findById(bookingId)
//                .map(Booking::getLockedUntil)
//                .filter(lockTime -> lockTime != null && lockTime.isAfter(LocalDateTime.now()));
//    }
//
//    /**
//     * Checks if a slot is available for booking
//     */
//    public boolean isSlotAvailable(Long slotId) {
//        return slotInstanceRepository.findById(slotId)
//                .map(slot -> slot.getStatus() == SlotStatus.AVAILABLE)
//                .orElse(false);
//    }
//
//    /**
//     * Gets booking by order ID (for payment processing)
//     */
//    public Optional<Booking> findBookingByOrderId(String orderId) {
//        // This would require adding orderId field to Booking entity
//        // For now, we'll find it through payment relationship
//        return Optional.empty(); // TODO: Implement when orderId is added to Booking
//    }
//}