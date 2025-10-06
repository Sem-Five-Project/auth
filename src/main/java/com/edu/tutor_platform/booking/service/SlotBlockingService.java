package com.edu.tutor_platform.booking.service;

import com.edu.tutor_platform.booking.entity.SlotInstance;
import com.edu.tutor_platform.booking.entity.Booking;
import com.edu.tutor_platform.booking.enums.SlotStatus;
import com.edu.tutor_platform.booking.repository.SlotInstanceRepository;
import com.edu.tutor_platform.booking.repository.BookingRepository;
import com.edu.tutor_platform.studentprofile.entity.StudentProfile;
import com.edu.tutor_platform.studentprofile.repository.StudentProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SlotBlockingService {
    
    private final SlotInstanceRepository slotInstanceRepository;
    private final BookingRepository bookingRepository;
    private final StudentProfileRepository studentProfileRepository;
    
    // Block duration for payment processing (15 minutes)
    private static final int BLOCK_DURATION_MINUTES = 15;
    
    /**
     * Blocks a slot for a student to make payment within 15 minutes
     * 
     * @param slotId The slot to block
     * @param studentId The student who wants to book
     * @return The booking ID if successful, null if slot is not available
     */
    @Transactional
    public Long blockSlotForPayment(Long slotId, Long studentId, String orderId) {
        log.info("Attempting to block slot {} for student {} with order {}", slotId, studentId, orderId);
        
        // Find the slot
        Optional<SlotInstance> slotOpt = slotInstanceRepository.findById(slotId);
        if (slotOpt.isEmpty()) {
            log.error("Slot {} not found", slotId);
            throw new RuntimeException("Slot not found");
        }
        
        SlotInstance slot = slotOpt.get();
        
        // Check if slot is available
        if (slot.getStatus() != SlotStatus.AVAILABLE) {
            log.error("Slot {} is not available, current status: {}", slotId, slot.getStatus());
            throw new RuntimeException("Slot is not available for booking");
        }
        
        // Find student profile
        Optional<StudentProfile> studentOpt = studentProfileRepository.findById(studentId);
        if (studentOpt.isEmpty()) {
            log.error("Student {} not found", studentId);
            throw new RuntimeException("Student not found");
        }
        
        StudentProfile student = studentOpt.get();
        
        // Check if student already has a pending booking for this slot
        Optional<Booking> existingBooking = bookingRepository.findBySlotInstanceAndStudentProfileAndIsConfirmed(slot, student, false);
        if (existingBooking.isPresent() && existingBooking.get().getLockedUntil().isAfter(LocalDateTime.now())) {
            log.error("Student {} already has a pending booking for slot {}", studentId, slotId);
            throw new RuntimeException("You already have a pending payment for this slot");
        }
        
        // Block the slot
        slot.setStatus(SlotStatus.LOCKED);
        slotInstanceRepository.save(slot);
        
        // Create a pending booking with lock time
        LocalDateTime lockExpiration = LocalDateTime.now().plusMinutes(BLOCK_DURATION_MINUTES);
        
        Booking booking = Booking.builder()
                .studentProfile(student)
                .slotInstance(slot)
                .createdAt(LocalDateTime.now())
                .lockedUntil(lockExpiration)
                .isConfirmed(false)
                .build();
        
        Booking savedBooking = bookingRepository.save(booking);
        
        log.info("Successfully blocked slot {} for student {} until {}. Booking ID: {}", 
                slotId, studentId, lockExpiration, savedBooking.getBookingId());
        
        return savedBooking.getBookingId();
    }
    
    /**
     * Confirms a booking when payment is successful
     * 
     * @param bookingId The booking to confirm
     * @param orderId The payment order ID
     */
    @Transactional
    public void confirmBooking(Long bookingId, String orderId) {
        log.info("Confirming booking {} for order {}", bookingId, orderId);
        
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            log.error("Booking {} not found", bookingId);
            throw new RuntimeException("Booking not found");
        }
        
        Booking booking = bookingOpt.get();
        
        // Check if booking is still valid (not expired)
        if (booking.getLockedUntil().isBefore(LocalDateTime.now())) {
            log.error("Booking {} has expired at {}", bookingId, booking.getLockedUntil());
            // Release the slot and delete the expired booking
            releaseExpiredBooking(booking);
            throw new RuntimeException("Booking has expired. Please try booking again.");
        }
        
        // Confirm the booking
        booking.setIsConfirmed(true);
        booking.setLockedUntil(null); // Remove lock time since it's confirmed
        
        // Update slot status to BOOKED
        SlotInstance slot = booking.getSlotInstance();
        slot.setStatus(SlotStatus.BOOKED);
        
        bookingRepository.save(booking);
        slotInstanceRepository.save(slot);
        
        log.info("Successfully confirmed booking {} for order {}", bookingId, orderId);
    }
    
    /**
     * Cancels a booking and releases the slot
     * 
     * @param bookingId The booking to cancel
     * @param reason The cancellation reason
     */
    @Transactional
    public void cancelBooking(Long bookingId, String reason) {
        log.info("Cancelling booking {} - Reason: {}", bookingId, reason);
        
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            log.error("Booking {} not found", bookingId);
            throw new RuntimeException("Booking not found");
        }
        
        Booking booking = bookingOpt.get();
        releaseExpiredBooking(booking);
        
        log.info("Successfully cancelled booking {}", bookingId);
    }
    
    /**
     * Releases an expired booking and makes the slot available again
     */
    private void releaseExpiredBooking(Booking booking) {
        // Release the slot
        SlotInstance slot = booking.getSlotInstance();
        slot.setStatus(SlotStatus.AVAILABLE);
        slotInstanceRepository.save(slot);
        
        // Delete the expired booking
        bookingRepository.delete(booking);
        
        log.info("Released slot {} and deleted expired booking {}", 
                slot.getSlotId(), booking.getBookingId());
    }
    
    /**
     * Scheduled task to clean up expired slot blocks every 5 minutes
     */
    @Scheduled(fixedRate = 300000) // Run every 5 minutes
    @Transactional
    public void cleanupExpiredSlotBlocks() {
        log.debug("Running cleanup of expired slot blocks");
        
        LocalDateTime now = LocalDateTime.now();
        
        // Find all expired, unconfirmed bookings
        List<Booking> expiredBookings = bookingRepository.findExpiredUnconfirmedBookings(now);
        
        if (!expiredBookings.isEmpty()) {
            log.info("Found {} expired bookings to cleanup", expiredBookings.size());
            
            for (Booking booking : expiredBookings) {
                try {
                    releaseExpiredBooking(booking);
                    log.info("Cleaned up expired booking {} for slot {}",
                            booking.getBookingId(), booking.getSlotInstance().getSlotId());
                } catch (Exception e) {
                    log.error("Error cleaning up expired booking {}: {}",
                            booking.getBookingId(), e.getMessage());
                }
            }
        }
        
        // Also cleanup directly locked slots that have expired
        cleanupExpiredDirectSlotLocks();
    }
    
    /**
     * Clean up directly locked slots that have expired
     */
    @Transactional
    private void cleanupExpiredDirectSlotLocks() {
        LocalDateTime now = LocalDateTime.now();
        
        // Find all slots that are LOCKED but have expired lockedUntil time
        List<SlotInstance> expiredSlots = slotInstanceRepository.findExpiredLockedSlots(now);
        
        if (!expiredSlots.isEmpty()) {
            log.info("Found {} directly locked slots that have expired", expiredSlots.size());
            
            for (SlotInstance slot : expiredSlots) {
                try {
                    slot.setStatus(SlotStatus.AVAILABLE);
                    slot.setLockedUntil(null);
                    slotInstanceRepository.save(slot);
                    
                    log.info("Released expired directly locked slot {}", slot.getSlotId());
                } catch (Exception e) {
                    log.error("Error releasing expired directly locked slot {}: {}",
                            slot.getSlotId(), e.getMessage());
                }
            }
        }
    }
    
    /**
     * Gets the remaining lock time for a booking
     */
    public Optional<LocalDateTime> getLockExpirationTime(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .map(Booking::getLockedUntil)
                .filter(lockTime -> lockTime != null && lockTime.isAfter(LocalDateTime.now()));
    }
    
    /**
     * Checks if a slot is available for booking
     */
    public boolean isSlotAvailable(Long slotId) {
        return slotInstanceRepository.findById(slotId)
                .map(slot -> slot.getStatus() == SlotStatus.AVAILABLE)
                .orElse(false);
    }
    
    /**
     * Gets booking by order ID (for payment processing)
     */
    public Optional<Booking> findBookingByOrderId(String orderId) {
        // This would require adding orderId field to Booking entity
        // For now, we'll find it through payment relationship
        return Optional.empty(); // TODO: Implement when orderId is added to Booking
    }
    
    /**
     * Directly locks a slot without creating a booking record
     * Sets the slot status to LOCKED with 15-minute timeout
     *
     * @param slotId The slot to lock
     * @return true if successfully locked, false if slot is not available
     */
    @Transactional
    public boolean lockSlotDirectly(Long slotId) {
        log.info("Directly locking slot {}", slotId);
        
        Optional<SlotInstance> slotOpt = slotInstanceRepository.findById(slotId);
        if (slotOpt.isEmpty()) {
            log.error("Slot {} not found", slotId);
            throw new RuntimeException("Slot not found");
        }
        
        SlotInstance slot = slotOpt.get();
        
        // Check if slot is available
        if (slot.getStatus() != SlotStatus.AVAILABLE) {
            log.warn("Slot {} is not available for locking, current status: {}", slotId, slot.getStatus());
            return false;
        }
        
        // Lock the slot with 15-minute timeout
        slot.setStatus(SlotStatus.LOCKED);
        slot.setLockedUntil(LocalDateTime.now().plusMinutes(BLOCK_DURATION_MINUTES));
        slotInstanceRepository.save(slot);
        
        log.info("Successfully locked slot {} until {}", slotId, slot.getLockedUntil());
        return true;
    }
    
    /**
     * Directly releases a locked slot back to AVAILABLE status
     *
     * @param slotId The slot to release
     * @return true if successfully released, false if slot was not locked
     */
    @Transactional
    public boolean releaseSlotDirectly(Long slotId) {
        log.info("Directly releasing slot {}", slotId);
        
        Optional<SlotInstance> slotOpt = slotInstanceRepository.findById(slotId);
        if (slotOpt.isEmpty()) {
            log.error("Slot {} not found", slotId);
            throw new RuntimeException("Slot not found");
        }
        
        SlotInstance slot = slotOpt.get();
        
        // Check if slot is locked
        if (slot.getStatus() != SlotStatus.LOCKED) {
            log.warn("Slot {} is not locked, current status: {}", slotId, slot.getStatus());
            return false;
        }
        
        // Release the slot
        slot.setStatus(SlotStatus.AVAILABLE);
        slot.setLockedUntil(null);
        slotInstanceRepository.save(slot);
        
        log.info("Successfully released slot {}", slotId);
        return true;
    }
    @Transactional
public boolean bookSlotDirectly(Long slotId) {
    log.info("Directly booking slot {}", slotId);

    Optional<SlotInstance> slotOpt = slotInstanceRepository.findById(slotId);
    if (slotOpt.isEmpty()) {
        log.error("Slot {} not found", slotId);
        throw new RuntimeException("Slot not found");
    }

    SlotInstance slot = slotOpt.get();

    // Only book if the slot is currently LOCKED
    if (slot.getStatus() != SlotStatus.LOCKED) {
        log.warn("Slot {} cannot be booked, current status: {}", slotId, slot.getStatus());
        return false;
    }

    // Book the slot
    slot.setStatus(SlotStatus.BOOKED);
    slot.setLockedUntil(null); // optional: clear the lock timestamp
    slotInstanceRepository.save(slot);

    log.info("Successfully booked slot {}", slotId);
    return true;
}

    /**
     * Bulk-lock a set of slots for 15 minutes. All-or-nothing: if any requested slot
     * is not AVAILABLE, none are locked and the unavailable IDs are returned.
     */
    @Transactional
    public List<Long> reserveSlotsBulk(List<Long> slotIds, Long studentId) {
        log.info("Bulk reserve requested for {} slots by student {}", slotIds.size(), studentId);

        // Load all requested slots
        List<SlotInstance> slots = slotInstanceRepository.findAllById(slotIds);

        // Determine unavailable ones (not AVAILABLE or already locked in future)
        LocalDateTime now = LocalDateTime.now();
        List<Long> unavailable = slots.stream()
                .filter(si -> si.getStatus() != SlotStatus.AVAILABLE)
                .map(SlotInstance::getSlotId)
                .collect(Collectors.toList());

        if (!unavailable.isEmpty()) {
            log.warn("Some slots unavailable: {}. Aborting bulk lock.", unavailable);
            return unavailable;
        }

        // Lock all for 15 minutes
    LocalDateTime lockedUntil = now.plusMinutes(BLOCK_DURATION_MINUTES);
    int updated = slotInstanceRepository.lockSlots(slotIds, lockedUntil, studentId);

        if (updated != slotIds.size()) {
            log.warn("Concurrent update detected. Expected {}, updated {}.", slotIds.size(), updated);
            // Re-check and return those that failed (now unavailable);
            // also rollback any locks we just applied to keep all-or-nothing
            List<SlotInstance> refreshed = slotInstanceRepository.findAllById(slotIds);
            List<Long> failed = refreshed.stream()
                    .filter(si -> si.getStatus() != SlotStatus.LOCKED)
                    .map(SlotInstance::getSlotId)
                    .collect(Collectors.toList());

            // Rollback: release any slots that were locked in this attempt
            for (SlotInstance si : refreshed) {
                if (si.getStatus() == SlotStatus.LOCKED && slotIds.contains(si.getSlotId())) {
                    si.setStatus(SlotStatus.AVAILABLE);
                    si.setLockedUntil(null);
                    slotInstanceRepository.save(si);
                }
            }

            return failed;
        }

        log.info("Successfully locked {} slots until {}", updated, lockedUntil);
        return List.of();
    }

}