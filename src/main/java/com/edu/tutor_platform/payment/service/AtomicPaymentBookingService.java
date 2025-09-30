//package com.edu.tutor_platform.payment.service;
//
//import com.edu.tutor_platform.booking.entity.Booking;
//import com.edu.tutor_platform.booking.entity.SlotInstance;
//import com.edu.tutor_platform.booking.enums.SlotStatus;
//import com.edu.tutor_platform.booking.repository.BookingRepository;
//import com.edu.tutor_platform.booking.repository.SlotInstanceRepository;
//import com.edu.tutor_platform.booking.service.SlotBlockingService;
//import com.edu.tutor_platform.payment.dto.HashResponse;
//import com.edu.tutor_platform.payment.dto.PaymentRequestDTO;
//import com.edu.tutor_platform.payment.entity.Payment;
//import com.edu.tutor_platform.payment.repository.PaymentRepository;
//import com.edu.tutor_platform.payment.util.HashUtil;
//import com.edu.tutor_platform.studentprofile.repository.StudentProfileRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Optional;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class AtomicPaymentBookingService {
//
//    private final PaymentRepository paymentRepository;
//    private final BookingRepository bookingRepository;
//    private final SlotInstanceRepository slotInstanceRepository;
//    private final StudentProfileRepository studentProfileRepository;
//    private final SlotBlockingService slotBlockingService;
//    private final HashUtil hashUtil;
//
//    @Value("${payhere.merchant.id}")
//    private String merchantId;
//
//    private static final int PAYMENT_TIMEOUT_MINUTES = 15;
//
//    /**
//     * Initiates atomic payment-booking process
//     * 1. Blocks the slot
//     * 2. Creates pending payment record
//     * 3. Generates PayHere hash
//     *
//     * @param request Payment request with slot details
//     * @return Hash response for PayHere integration
//     */
//    @Transactional
//    public HashResponse initiateAtomicPaymentBooking(PaymentRequestDTO request) {
//        log.info("Initiating atomic payment-booking for slot {} by student {}",
//                request.getSlotId(), request.getStudentId());
//
//        // Validate required fields
//        validatePaymentRequest(request);
//
//        // Generate unique order ID if not provided
//        String orderId = request.getOrderId() != null ? request.getOrderId() :
//                        "ORDER_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
//
//        // Check if slot is available
//        if (!slotBlockingService.isSlotAvailable(request.getSlotId())) {
//            throw new RuntimeException("Slot is no longer available");
//        }
//
//        // Check if student already has pending payments (prevent duplicate bookings)
//        if (bookingRepository.hasActivePendingBookings(request.getStudentId(), LocalDateTime.now())) {
//            throw new RuntimeException("You already have a pending payment. Please complete or cancel it first.");
//        }
//
//        try {
//            // Step 1: Block the slot (creates pending booking)
//            Long bookingId = slotBlockingService.blockSlotForPayment(
//                    request.getSlotId(),
//                    request.getStudentId(),
//                    orderId
//            );
//
//            // Step 2: Create payment record with expiration
//            LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(PAYMENT_TIMEOUT_MINUTES);
//
//            Payment payment = Payment.builder()
//                    .orderId(orderId)
//                    .amount(request.getAmount())
//                    .currency(request.getCurrency())
//                    .status("PENDING")
//                    .classId(request.getClassId())
//                    .studentId(request.getStudentId())
//                    .tutorId(request.getTutorId())
//                    .availabilityId(request.getAvailabilityId())
//                    .slotId(request.getSlotId())
//                    .expiresAt(expirationTime)
//                    .build();
//
//            Payment savedPayment = paymentRepository.save(payment);
//
//            // Step 3: Link payment to booking
//            Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
//            if (bookingOpt.isPresent()) {
//                Booking booking = bookingOpt.get();
//                booking.setOrderId(orderId);
//                booking.setPayment(savedPayment);
//                bookingRepository.save(booking);
//            }
//
//            // Step 4: Generate PayHere hash
//            String hash = hashUtil.generateHash(merchantId, orderId, request.getAmount(), request.getCurrency());
//
//            log.info("Successfully initiated atomic payment-booking. Order: {}, Booking: {}, Payment expires at: {}",
//                    orderId, bookingId, expirationTime);
//
//            return new HashResponse(merchantId, hash);
//
//        } catch (Exception e) {
//            log.error("Failed to initiate atomic payment-booking: {}", e.getMessage());
//            // If anything fails, ensure slot is released
//            try {
//                releaseSlotIfBlocked(request.getSlotId());
//            } catch (Exception releaseError) {
//                log.error("Failed to release slot after payment initiation error: {}", releaseError.getMessage());
//            }
//            throw new RuntimeException("Failed to initiate payment: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Completes the atomic transaction when payment is successful
//     */
//    @Transactional
//    public void completeAtomicPaymentBooking(String orderId) {
//        log.info("Completing atomic payment-booking for order: {}", orderId);
//
//        // Find payment record
//        Optional<Payment> paymentOpt = paymentRepository.findByOrderId(orderId);
//        if (paymentOpt.isEmpty()) {
//            log.error("Payment not found for order: {}", orderId);
//            throw new RuntimeException("Payment record not found");
//        }
//
//        Payment payment = paymentOpt.get();
//
//        // Check if payment is expired
//        if (payment.getExpiresAt().isBefore(LocalDateTime.now())) {
//            log.error("Payment {} has expired at {}", orderId, payment.getExpiresAt());
//            // Mark payment as expired and release slot
//            payment.setStatus("EXPIRED");
//            paymentRepository.save(payment);
//
//            // Find and cancel the booking
//            Optional<Booking> bookingOpt = bookingRepository.findByOrderId(orderId);
//            if (bookingOpt.isPresent()) {
//                slotBlockingService.cancelBooking(bookingOpt.get().getBookingId(), "Payment expired");
//            }
//
//            throw new RuntimeException("Payment window has expired. Please try booking again.");
//        }
//
//        try {
//            // Find the associated booking
//            Optional<Booking> bookingOpt = bookingRepository.findByOrderId(orderId);
//            if (bookingOpt.isEmpty()) {
//                log.error("Booking not found for order: {}", orderId);
//                throw new RuntimeException("Booking not found");
//            }
//
//            Booking booking = bookingOpt.get();
//
//            // Confirm the booking (this updates slot status to BOOKED)
//            slotBlockingService.confirmBooking(booking.getBookingId(), orderId);
//
//            // Update payment status
//            payment.setStatus("SUCCESS");
//            payment.setCompletedAt(LocalDateTime.now());
//            paymentRepository.save(payment);
//
//            log.info("Successfully completed atomic payment-booking for order: {}", orderId);
//
//        } catch (Exception e) {
//            log.error("Failed to complete atomic payment-booking for order {}: {}", orderId, e.getMessage());
//
//            // Mark payment as failed
//            payment.setStatus("FAILED");
//            paymentRepository.save(payment);
//
//            throw new RuntimeException("Failed to complete booking: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Cancels the atomic transaction (on payment failure or timeout)
//     * 1. Releases the blocked slot
//     * 2. Marks payment as failed
//     * 3. Cancels the booking
//     */
//    @Transactional
//    public void cancelAtomicPaymentBooking(String orderId, String reason) {
//        log.info("Cancelling atomic payment-booking for order: {} - Reason: {}", orderId, reason);
//
//        try {
//            // Find and update payment
//            Optional<Payment> paymentOpt = paymentRepository.findByOrderId(orderId);
//            if (paymentOpt.isPresent()) {
//                Payment payment = paymentOpt.get();
//                payment.setStatus("FAILED");
//                payment.setCompletedAt(LocalDateTime.now());
//                paymentRepository.save(payment);
//            }
//
//            // Find and cancel booking
//            Optional<Booking> bookingOpt = bookingRepository.findByOrderId(orderId);
//            if (bookingOpt.isPresent()) {
//                slotBlockingService.cancelBooking(bookingOpt.get().getBookingId(), reason);
//            }
//
//            log.info("Successfully cancelled atomic payment-booking for order: {}", orderId);
//
//        } catch (Exception e) {
//            log.error("Error cancelling atomic payment-booking for order {}: {}", orderId, e.getMessage());
//        }
//    }
//
//    /**
//     * Gets the status of a payment-booking transaction
//     */
//    public Map<String, Object> getTransactionStatus(String orderId) {
//        Map<String, Object> status = new HashMap<>();
//
//        Optional<Payment> paymentOpt = paymentRepository.findByOrderId(orderId);
//        if (paymentOpt.isPresent()) {
//            Payment payment = paymentOpt.get();
//            status.put("paymentStatus", payment.getStatus());
//            status.put("expiresAt", payment.getExpiresAt());
//            status.put("isExpired", payment.getExpiresAt().isBefore(LocalDateTime.now()));
//
//            // Get booking info
//            Optional<Booking> bookingOpt = bookingRepository.findByOrderId(orderId);
//            if (bookingOpt.isPresent()) {
//                Booking booking = bookingOpt.get();
//                status.put("bookingId", booking.getBookingId());
//                status.put("isConfirmed", booking.getIsConfirmed());
//                status.put("slotId", booking.getSlotInstance().getSlotId());
//                status.put("lockedUntil", booking.getLockedUntil());
//            }
//        } else {
//            status.put("paymentStatus", "NOT_FOUND");
//        }
//
//        return status;
//    }
//
//    private void validatePaymentRequest(PaymentRequestDTO request) {
//        if (request.getSlotId() == null) {
//            throw new RuntimeException("Slot ID is required");
//        }
//        if (request.getStudentId() == null) {
//            throw new RuntimeException("Student ID is required");
//        }
//        if (request.getAmount() <= 0) {
//            throw new RuntimeException("Amount must be greater than 0");
//        }
//    }
//
//    private void releaseSlotIfBlocked(Long slotId) {
//        Optional<SlotInstance> slotOpt = slotInstanceRepository.findById(slotId);
//        if (slotOpt.isPresent()) {
//            SlotInstance slot = slotOpt.get();
//            if (slot.getStatus() == SlotStatus.LOCKED) {
//                slot.setStatus(SlotStatus.AVAILABLE);
//                slotInstanceRepository.save(slot);
//                log.info("Released blocked slot: {}", slotId);
//            }
//        }
//    }
//}