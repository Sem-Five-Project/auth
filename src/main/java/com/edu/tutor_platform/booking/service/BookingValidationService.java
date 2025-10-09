// package com.edu.tutor_platform.booking.service;

// import com.edu.tutor_platform.booking.dto.BookingRequestDTO;
// import com.edu.tutor_platform.booking.dto.TutorAvailabilityDTO;
// import com.edu.tutor_platform.booking.entity.Booking;
// import com.edu.tutor_platform.booking.entity.SlotInstance;
// import com.edu.tutor_platform.booking.entity.TutorAvailability;
// import com.edu.tutor_platform.booking.enums.SlotStatus;
// import com.edu.tutor_platform.booking.repository.BookingRepository;
// import com.edu.tutor_platform.booking.repository.SlotInstanceRepository;
// import com.edu.tutor_platform.booking.repository.TutorAvailabilityRepository;
// import com.edu.tutor_platform.studentprofile.repository.StudentProfileRepository;
// import com.edu.tutor_platform.tutorprofile.repository.TutorProfileRepository;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.stereotype.Service;

// import java.time.LocalDateTime;
// import java.time.LocalTime;
// import java.util.List;
// import java.util.Optional;

// @Service
// @RequiredArgsConstructor
// @Slf4j
// public class BookingValidationService {

//     private final TutorAvailabilityRepository tutorAvailabilityRepository;
//     private final SlotInstanceRepository slotInstanceRepository;
//     private final BookingRepository bookingRepository;
//     private final StudentProfileRepository studentProfileRepository;
//     private final TutorProfileRepository tutorProfileRepository;

//     /**
//      * Validate tutor availability creation/update
//      */
//     public void validateTutorAvailability(TutorAvailabilityDTO dto) {
//         // Check if tutor exists
//         if (!tutorProfileRepository.existsById(dto.getTutorId())) {
//             throw new IllegalArgumentException("Tutor not found with ID: " + dto.getTutorId());
//         }

//         // Validate time slots
//         if (dto.getStartTime().isAfter(dto.getEndTime()) ||
//             dto.getStartTime().equals(dto.getEndTime())) {
//             throw new IllegalArgumentException("Start time must be before end time");
//         }

//         // Validate minimum slot duration (e.g., at least 30 minutes)
//         if (dto.getStartTime().plusMinutes(30).isAfter(dto.getEndTime())) {
//             throw new IllegalArgumentException("Minimum slot duration is 30 minutes");
//         }

//         // Validate business hours (e.g., between 6 AM and 10 PM)
//         LocalTime earliestStart = LocalTime.of(6, 0);
//         LocalTime latestEnd = LocalTime.of(22, 0);

//         if (dto.getStartTime().isBefore(earliestStart) ||
//             dto.getEndTime().isAfter(latestEnd)) {
//             throw new IllegalArgumentException("Availability must be between 6:00 AM and 10:00 PM");
//         }

//         // Check for overlapping availability
//         validateNoOverlappingAvailability(dto);

//         // Validate maximum daily availability (e.g., max 10 hours per day)
//         validateMaximumDailyAvailability(dto);
//     }

//     /**
//      * Validate booking request
//      */
//     public void validateBookingRequest(BookingRequestDTO request) {
//         // Check if student exists
//         if (!studentProfileRepository.existsById(request.getStudentId())) {
//             throw new IllegalArgumentException("Student not found with ID: " + request.getStudentId());
//         }

//         // Check if slot exists
//         Optional<SlotInstance> slotOpt = slotInstanceRepository.findById(request.getSlotId());
//         if (slotOpt.isEmpty()) {
//             throw new IllegalArgumentException("Slot not found with ID: " + request.getSlotId());
//         }

//         SlotInstance slot = slotOpt.get();

//         // Check if slot is available
//         if (slot.getStatus() != SlotStatus.AVAILABLE) {
//             throw new IllegalStateException("Slot is not available for booking");
//         }

//         // Check if slot is in the future
//         if (slot.getSlotDate().isBefore(java.time.LocalDate.now())) {
//             throw new IllegalArgumentException("Cannot book slots in the past");
//         }

//         // Check minimum advance booking time (e.g., at least 2 hours in advance)
//         LocalDateTime slotDateTime = LocalDateTime.of(
//             slot.getSlotDate(),
//             slot.getTutorAvailability().getStartTime()
//         );

//         if (slotDateTime.isBefore(LocalDateTime.now().plusHours(2))) {
//             throw new IllegalArgumentException("Bookings must be made at least 2 hours in advance");
//         }

//         // Check if student already has a booking for this slot
//         Optional<Booking> existingBooking = bookingRepository
//             .findBySlotInstanceSlotIdAndStudentProfileStudentId(request.getSlotId(), request.getStudentId());

//         if (existingBooking.isPresent()) {
//             throw new IllegalStateException("Student already has a booking for this slot");
//         }

//         // Check if student has conflicting bookings (same time slot on same day)
//         validateNoConflictingBookings(request, slot);

//         // Check student's booking limits (e.g., max 5 pending reservations)
//         validateStudentBookingLimits(request.getStudentId());
//     }

//     /**
//      * Validate no overlapping availability for the same tutor
//      */
//     private void validateNoOverlappingAvailability(TutorAvailabilityDTO dto) {
//         List<TutorAvailability> overlapping = tutorAvailabilityRepository
//             .findOverlappingAvailability(
//                 dto.getTutorId(),
//                 dto.getDayOfWeek(),
//                 dto.getStartTime(),
//                 dto.getEndTime()
//             );

//         // Filter out current availability if updating
//         if (dto.getAvailabilityId() != null) {
//             overlapping = overlapping.stream()
//                 .filter(av -> !av.getAvailabilityId().equals(dto.getAvailabilityId()))
//                 .toList();
//         }

//         if (!overlapping.isEmpty()) {
//             throw new IllegalStateException("Overlapping availability found for the same day and time");
//         }
//     }

//     /**
//      * Validate maximum daily availability hours
//      */
//     private void validateMaximumDailyAvailability(TutorAvailabilityDTO dto) {
//         List<TutorAvailability> existingAvailability = tutorAvailabilityRepository
//             .findByTutorProfileTutorIdAndDayOfWeek(dto.getTutorId(), dto.getDayOfWeek());

//         // Filter out current availability if updating
//         if (dto.getAvailabilityId() != null) {
//             existingAvailability = existingAvailability.stream()
//                 .filter(av -> !av.getAvailabilityId().equals(dto.getAvailabilityId()))
//                 .toList();
//         }

//         // Calculate total hours including new availability
//         int totalMinutes = existingAvailability.stream()
//             .mapToInt(av -> (av.getEndTime().getHour() * 60 + av.getEndTime().getMinute()) -
//                            (av.getStartTime().getHour() * 60 + av.getStartTime().getMinute()))
//             .sum();

//         int newMinutes = (dto.getEndTime().getHour() * 60 + dto.getEndTime().getMinute()) -
//                         (dto.getStartTime().getHour() * 60 + dto.getStartTime().getMinute());

//         totalMinutes += newMinutes;

//         if (totalMinutes > 600) { // 10 hours = 600 minutes
//             throw new IllegalArgumentException("Maximum 10 hours of availability per day allowed");
//         }
//     }

//     /**
//      * Validate no conflicting bookings for the same student
//      */
//     private void validateNoConflictingBookings(BookingRequestDTO request, SlotInstance slot) {
//         List<Booking> studentBookings = bookingRepository
//             .findByStudentAndDateRange(
//                 request.getStudentId(),
//                 slot.getSlotDate(),
//                 slot.getSlotDate()
//             );

//         for (Booking booking : studentBookings) {
//             SlotInstance existingSlot = booking.getSlotInstance();
//             TutorAvailability existingAvailability = existingSlot.getTutorAvailability();
//             TutorAvailability newAvailability = slot.getTutorAvailability();

//             // Check for time overlap
//             boolean hasOverlap = !(newAvailability.getEndTime().isBefore(existingAvailability.getStartTime()) ||
//                                   newAvailability.getStartTime().isAfter(existingAvailability.getEndTime()));

//             if (hasOverlap) {
//                 throw new IllegalStateException("Student has a conflicting booking at the same time");
//             }
//         }
//     }

//     /**
//      * Validate student booking limits
//      */
//     private void validateStudentBookingLimits(Long studentId) {
//         // Check maximum pending reservations
//         List<Booking> pendingBookings = bookingRepository
//             .findByStudentProfileStudentIdAndIsConfirmedFalse(studentId);

//         if (pendingBookings.size() >= 5) {
//             throw new IllegalStateException("Maximum 5 pending reservations allowed per student");
//         }

//         // Check maximum bookings per day
//         LocalDateTime today = LocalDateTime.now();
//         List<Booking> todayBookings = bookingRepository
//             .findByStudentAndDateRange(
//                 studentId,
//                 today.toLocalDate(),
//                 today.toLocalDate()
//             );

//         if (todayBookings.size() >= 3) {
//             throw new IllegalStateException("Maximum 3 bookings per day allowed");
//         }
//     }

//     /**
//      * Validate booking confirmation
//      */
//     public void validateBookingConfirmation(Long bookingId) {
//         Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
//         if (bookingOpt.isEmpty()) {
//             throw new IllegalArgumentException("Booking not found with ID: " + bookingId);
//         }

//         Booking booking = bookingOpt.get();

//         // Check if booking is already confirmed
//         if (booking.getIsConfirmed()) {
//             throw new IllegalStateException("Booking is already confirmed");
//         }

//         // Check if booking reservation has expired
//         if (LocalDateTime.now().isAfter(booking.getLockedUntil())) {
//             throw new IllegalStateException("Booking reservation has expired");
//         }

//         // Check if slot is still locked
//         SlotInstance slot = booking.getSlotInstance();
//         if (slot.getStatus() != SlotStatus.LOCKED) {
//             throw new IllegalStateException("Slot is no longer locked for this booking");
//         }
//     }

//     /**
//      * Validate slot cancellation
//      */
//     public void validateSlotCancellation(Long slotId, Long tutorId) {
//         Optional<SlotInstance> slotOpt = slotInstanceRepository.findById(slotId);
//         if (slotOpt.isEmpty()) {
//             throw new IllegalArgumentException("Slot not found with ID: " + slotId);
//         }

//         SlotInstance slot = slotOpt.get();

//         // Check if tutor owns this slot
//         if (!slot.getTutorAvailability().getTutorProfile().getTutorId().equals(tutorId)) {
//             throw new IllegalArgumentException("Tutor does not own this slot");
//         }

//         // Check if slot can be cancelled (minimum 24 hours notice)
//         LocalDateTime slotDateTime = LocalDateTime.of(
//             slot.getSlotDate(),
//             slot.getTutorAvailability().getStartTime()
//         );

//         if (slotDateTime.isBefore(LocalDateTime.now().plusHours(24))) {
//             throw new IllegalStateException("Slots cannot be cancelled within 24 hours of the scheduled time");
//         }

//         // Check if slot has confirmed bookings
//         List<Booking> confirmedBookings = slot.getBookings().stream()
//             .filter(Booking::getIsConfirmed)
//             .toList();

//         if (!confirmedBookings.isEmpty()) {
//             throw new IllegalStateException("Cannot cancel slot with confirmed bookings");
//         }
//     }

//     /**
//      * Validate recurring availability settings
//      */
//     public void validateRecurringSettings(TutorAvailabilityDTO dto) {
//         if (dto.getRecurring()) {
//             // For recurring availability, ensure tutor has set up their profile completely
//             if (!tutorProfileRepository.existsById(dto.getTutorId())) {
//                 throw new IllegalArgumentException("Tutor profile must be complete before setting recurring availability");
//             }

//             // Validate that tutor doesn't have too many recurring slots
//             List<TutorAvailability> existingRecurring = tutorAvailabilityRepository
//                 .findByTutorProfileTutorIdAndRecurringTrue(dto.getTutorId());

//             if (existingRecurring.size() >= 14) { // Max 2 slots per day of the week
//                 throw new IllegalStateException("Maximum 14 recurring availability slots allowed per tutor");
//             }
//         }
//     }

//     /**
//      * Validate payment processing
//      */
//     public void validatePaymentProcessing(Long bookingId) {
//         validateBookingConfirmation(bookingId);

//         Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
//         Booking booking = bookingOpt.get(); // Already validated above

//         // Check if payment already exists
//         if (booking.getPayment() != null) {
//             throw new IllegalStateException("Payment already exists for this booking");
//         }

//         // Validate tutor has set hourly rate
//         Double hourlyRate = booking.getSlotInstance()
//             .getTutorAvailability()
//             .getTutorProfile()
//             .getHourlyRate().doubleValue();

//         if (hourlyRate == null || hourlyRate <= 0) {
//             throw new IllegalStateException("Tutor must set a valid hourly rate before accepting bookings");
//         }
//     }
// }