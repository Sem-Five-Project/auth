package com.edu.tutor_platform.booking.service;

import com.edu.tutor_platform.booking.dto.BookingDTO;
import com.edu.tutor_platform.booking.dto.BookingRequestDTO;
import com.edu.tutor_platform.booking.entity.Booking;
import com.edu.tutor_platform.booking.entity.SlotInstance;
import com.edu.tutor_platform.booking.enums.SlotStatus;
import com.edu.tutor_platform.booking.repository.BookingRepository;
import com.edu.tutor_platform.booking.repository.SlotInstanceRepository;
import com.edu.tutor_platform.payment.entity.Payment;
import com.edu.tutor_platform.payment.service.PaymentService;
import com.edu.tutor_platform.studentprofile.entity.StudentProfile;
import com.edu.tutor_platform.studentprofile.repository.StudentProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final SlotInstanceRepository slotInstanceRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final PaymentService paymentService;
    //private final BookingValidationService validationService;

    private static final int BOOKING_LOCK_MINUTES = 15; // 15 minutes to complete payment

    /**
     * Create a booking reservation with time-limited lock
     */
    // @Transactional
    // public BookingDTO createBookingReservation(BookingRequestDTO request) {
    //     log.info("Creating booking reservation for slot {} by student {}",
    //             request.getSlotId(), request.getStudentId());

    //     // Validate booking request
    //     validationService.validateBookingRequest(request);

    //     // Get validated entities
    //     StudentProfile student = studentProfileRepository.findById(request.getStudentId())
    //             .orElseThrow(() -> new RuntimeException("Student not found"));

    //     SlotInstance slot = slotInstanceRepository.findById(request.getSlotId())
    //             .orElseThrow(() -> new RuntimeException("Slot not found"));

    //     // Create booking with lock
    //     LocalDateTime lockUntil = LocalDateTime.now().plusMinutes(BOOKING_LOCK_MINUTES);
        
    //     Booking booking = Booking.builder()
    //             .studentProfile(student)
    //             .slotInstance(slot)
    //             .lockedUntil(lockUntil)
    //             .isConfirmed(false)
    //             .build();

    //     booking = bookingRepository.save(booking);

    //     // Update slot status to LOCKED
    //     slot.setStatus(SlotStatus.LOCKED);
    //     slotInstanceRepository.save(slot);

    //     log.info("Created booking reservation {} with lock until {}", 
    //             booking.getBookingId(), lockUntil);

    //     return convertToDTO(booking);
    // }

    /**
     * Confirm booking after successful payment
     */
    // @Transactional
    // public BookingDTO confirmBooking(Long bookingId, String paymentId) {
    //     log.info("Confirming booking {} with payment {}", bookingId, paymentId);

    //     // Validate booking confirmation
    //     validationService.validateBookingConfirmation(bookingId);

    //     Booking booking = bookingRepository.findById(bookingId)
    //             .orElseThrow(() -> new RuntimeException("Booking not found"));

    //     // Update booking status
    //     booking.setIsConfirmed(true);
    //     booking = bookingRepository.save(booking);

    //     // Update slot status to BOOKED
    //     SlotInstance slot = booking.getSlotInstance();
    //     slot.setStatus(SlotStatus.BOOKED);
    //     slotInstanceRepository.save(slot);

    //     log.info("Confirmed booking {} successfully", bookingId);

    //     return convertToDTO(booking);
    // }

    /**
     * Cancel a booking reservation
     */
    @Transactional
    public void cancelBookingReservation(Long bookingId) {
        log.info("Cancelling booking reservation {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getIsConfirmed()) {
            throw new RuntimeException("Cannot cancel confirmed booking");
        }

        // Release the slot
        SlotInstance slot = booking.getSlotInstance();
        slot.setStatus(SlotStatus.AVAILABLE);
        slotInstanceRepository.save(slot);

        // Delete the booking
        bookingRepository.delete(booking);

        log.info("Cancelled booking reservation {} and released slot", bookingId);
    }

    /**
     * Process payment for a booking
     */
    // @Transactional
    // public Map<String, Object> processBookingPayment(Long bookingId, BookingRequestDTO request) {
    //     log.info("Processing payment for booking {}", bookingId);

    //     // Validate payment processing
    //     validationService.validatePaymentProcessing(bookingId);

    //     Booking booking = bookingRepository.findById(bookingId)
    //             .orElseThrow(() -> new RuntimeException("Booking not found"));

    //     SlotInstance slot = booking.getSlotInstance();
    //     Double hourlyRate = slot.getTutorAvailability().getTutorProfile().getHourlyRate().doubleValue();

    //     // Calculate amount (assuming 1-hour slots for now)
    //     double amount = hourlyRate;

    //     // Create payment request
    //     try {
    //         // Create PaymentRequestDTO for the existing payment service
    //         com.edu.tutor_platform.payment.dto.PaymentRequestDTO paymentRequest =
    //                 new com.edu.tutor_platform.payment.dto.PaymentRequestDTO();
    //         paymentRequest.setOrderId(String.valueOf(bookingId));
    //         paymentRequest.setAmount(amount);
    //         paymentRequest.setCurrency("LKR");
    //         paymentRequest.setStudentId(booking.getStudentProfile().getStudentId());
    //         paymentRequest.setTutorId(slot.getTutorAvailability().getTutorProfile().getTutorId());
    //         paymentRequest.setClassId(bookingId); // Use booking ID as class ID

    //         // Generate payment hash using existing service
    //         String paymentHash = paymentService.generatePaymentHash(paymentRequest);

    //         // Prepare response with payment details
    //         Map<String, Object> paymentResult = new java.util.HashMap<>();
    //         paymentResult.put("hash", paymentHash);
    //         paymentResult.put("orderId", paymentRequest.getOrderId());
    //         paymentResult.put("amount", paymentRequest.getAmount());
    //         paymentResult.put("currency", paymentRequest.getCurrency());
    //         paymentResult.put("studentId", paymentRequest.getStudentId());
    //         paymentResult.put("tutorId", paymentRequest.getTutorId());
    //         paymentResult.put("bookingId", bookingId);
    //         paymentResult.put("slotDate", slot.getSlotDate().toString());
    //         paymentResult.put("startTime", slot.getTutorAvailability().getStartTime().toString());
    //         paymentResult.put("endTime", slot.getTutorAvailability().getEndTime().toString());

    //         log.info("Payment initiated for booking {} with amount {}", bookingId, amount);
    //         return paymentResult;

    //     } catch (Exception e) {
    //         log.error("Payment processing failed for booking {}: {}", bookingId, e.getMessage());
    //         throw new RuntimeException("Payment processing failed: " + e.getMessage());
    //     }
    // }

    /**
     * Handle payment notification (webhook)
     */
    // @Transactional
    // public void handlePaymentNotification(String orderId, String status) {
    //     log.info("Handling payment notification for order {} with status {}", orderId, status);

    //     try {
    //         Long bookingId = Long.parseLong(orderId);
            
    //         if ("SUCCESS".equals(status)) {
    //             confirmBooking(bookingId, orderId);
    //         } else {
    //             // Payment failed, cancel the reservation
    //             cancelBookingReservation(bookingId);
    //         }
    //     } catch (NumberFormatException e) {
    //         log.error("Invalid booking ID in payment notification: {}", orderId);
    //     } catch (Exception e) {
    //         log.error("Error handling payment notification: {}", e.getMessage());
    //     }
    // }

    /**
     * Get student's bookings
     */
    public List<BookingDTO> getStudentBookings(Long studentId) {
        List<Booking> bookings = bookingRepository.findByStudentProfileStudentId(studentId);
        return bookings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get tutor's bookings
     */
    public List<BookingDTO> getTutorBookings(Long tutorId) {
        List<Booking> bookings = bookingRepository.findByTutorId(tutorId);
        return bookings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get confirmed bookings for a student
     */
    public List<BookingDTO> getConfirmedStudentBookings(Long studentId) {
        List<Booking> bookings = bookingRepository.findByStudentProfileStudentIdAndIsConfirmedTrue(studentId);
        return bookings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get confirmed bookings for a tutor
     */
    public List<BookingDTO> getConfirmedTutorBookings(Long tutorId) {
        List<Booking> bookings = bookingRepository.findConfirmedBookingsByTutorId(tutorId);
        return bookings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Scheduled task to clean up expired bookings
     * Runs every 5 minutes
     */
    @Scheduled(fixedRate = 300000) // 5 minutes = 300,000 milliseconds
    @Transactional
    public void cleanupExpiredBookings() {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> expiredBookings = bookingRepository.findExpiredLockedBookings(now);
        
        log.info("Found {} expired bookings to clean up", expiredBookings.size());
        
        for (Booking booking : expiredBookings) {
            try {
                releaseExpiredBooking(booking);
                log.debug("Released expired booking {}", booking.getBookingId());
            } catch (Exception e) {
                log.error("Error releasing expired booking {}: {}", booking.getBookingId(), e.getMessage());
            }
        }
        
        if (!expiredBookings.isEmpty()) {
            log.info("Cleaned up {} expired bookings", expiredBookings.size());
        }
    }

    /**
     * Release an expired booking and make the slot available
     */
    private void releaseExpiredBooking(Booking booking) {
        // Release the slot
        SlotInstance slot = booking.getSlotInstance();
        slot.setStatus(SlotStatus.AVAILABLE);
        slotInstanceRepository.save(slot);

        // Delete the expired booking
        bookingRepository.delete(booking);
    }

    /**
     * Get booking by ID
     */
    public BookingDTO getBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        return convertToDTO(booking);
    }

    /**
     * Check if a booking is still valid (not expired)
     */
    public boolean isBookingValid(Long bookingId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            return false;
        }
        
        Booking booking = bookingOpt.get();
        return booking.getIsConfirmed() || LocalDateTime.now().isBefore(booking.getLockedUntil());
    }

    /**
     * Convert booking entity to DTO
     */
    private BookingDTO convertToDTO(Booking booking) {
        SlotInstance slot = booking.getSlotInstance();
        String bookingStatus = getBookingStatus(booking);
        
        return BookingDTO.builder()
                .bookingId(booking.getBookingId())
                .studentId(booking.getStudentProfile().getStudentId())
                .studentName(booking.getStudentProfile().getUser().getFirstName() + " " + 
                           booking.getStudentProfile().getUser().getLastName())
                .slotId(slot.getSlotId())
                .tutorId(slot.getTutorAvailability().getTutorProfile().getTutorId())
                .tutorName(slot.getTutorAvailability().getTutorProfile().getUser().getFirstName() + " " +
                          slot.getTutorAvailability().getTutorProfile().getUser().getLastName())
                .slotDate(slot.getSlotDate())
                .startTime(slot.getTutorAvailability().getStartTime())
                .endTime(slot.getTutorAvailability().getEndTime())
                .createdAt(booking.getCreatedAt())
                .lockedUntil(booking.getLockedUntil())
                .isConfirmed(booking.getIsConfirmed())
                .amount(slot.getTutorAvailability().getTutorProfile().getHourlyRate() != null ? 
                       slot.getTutorAvailability().getTutorProfile().getHourlyRate().doubleValue() : null)
                .paymentStatus(booking.getPayment() != null ? booking.getPayment().getStatus() : null)
                .paymentId(booking.getPayment() != null ? booking.getPayment().getOrderId() : null)
                .hourlyRate(slot.getTutorAvailability().getTutorProfile().getHourlyRate() != null ? 
                           slot.getTutorAvailability().getTutorProfile().getHourlyRate().doubleValue() : null)
                .bookingStatus(bookingStatus)
                .build();
    }

    /**
     * Determine booking status
     */
    private String getBookingStatus(Booking booking) {
        if (booking.getIsConfirmed()) {
            return "CONFIRMED";
        } else if (LocalDateTime.now().isAfter(booking.getLockedUntil())) {
            return "EXPIRED";
        } else {
            return "RESERVED";
        }
    }
}