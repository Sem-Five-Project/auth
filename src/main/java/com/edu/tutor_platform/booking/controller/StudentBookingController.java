//package com.edu.tutor_platform.booking.controller;
//
//import com.edu.tutor_platform.booking.dto.BookingDTO;
//import com.edu.tutor_platform.booking.dto.BookingRequestDTO;
//import com.edu.tutor_platform.booking.dto.SlotInstanceDTO;
//import com.edu.tutor_platform.booking.dto.SlotSearchRequestDTO;
//import com.edu.tutor_platform.booking.service.BookingService;
//import com.edu.tutor_platform.booking.service.SlotManagementService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import jakarta.validation.Valid;
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/student/bookings")
//@RequiredArgsConstructor
//public class StudentBookingController {
//
//    private final BookingService bookingService;
//    private final SlotManagementService slotManagementService;
//
//    /**
//     * Search for available slots
//     */
//    @GetMapping("/slots/search")
//    public ResponseEntity<List<SlotInstanceDTO>> searchAvailableSlots(
//            @RequestParam(required = false) Long tutorId,
//            @RequestParam(required = false) LocalDate startDate,
//            @RequestParam(required = false) LocalDate endDate,
//            @RequestParam(required = false) LocalDate specificDate,
//            @RequestParam(required = false) Double minHourlyRate,
//            @RequestParam(required = false) Double maxHourlyRate,
//            @RequestParam(required = false) Double minRating,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size,
//            @RequestParam(defaultValue = "slotDate") String sortBy,
//            @RequestParam(defaultValue = "ASC") String sortDirection) {
//
//        SlotSearchRequestDTO searchRequest = SlotSearchRequestDTO.builder()
//                .tutorId(tutorId)
//                .startDate(startDate != null ? startDate : LocalDate.now())
//                .endDate(endDate != null ? endDate : LocalDate.now().plusWeeks(2))
//                .specificDate(specificDate)
//                .minHourlyRate(minHourlyRate)
//                .maxHourlyRate(maxHourlyRate)
//                .minRating(minRating)
//                .page(page)
//                .size(size)
//                .sortBy(sortBy)
//                .sortDirection(sortDirection)
//                .build();
//
//        List<SlotInstanceDTO> slots = slotManagementService.searchAvailableSlots(searchRequest);
//        return ResponseEntity.ok(slots);
//    }
//
//    /**
//     * Get available slots for a specific tutor
//     */
//    @GetMapping("/slots/tutor/{tutorId}")
//    public ResponseEntity<List<SlotInstanceDTO>> getTutorSlots(
//            @PathVariable Long tutorId,
//            @RequestParam(required = false) LocalDate startDate,
//            @RequestParam(required = false) LocalDate endDate) {
//
//        SlotSearchRequestDTO searchRequest = SlotSearchRequestDTO.builder()
//                .tutorId(tutorId)
//                .startDate(startDate != null ? startDate : LocalDate.now())
//                .endDate(endDate != null ? endDate : LocalDate.now().plusWeeks(2))
//                .build();
//
//        List<SlotInstanceDTO> slots = slotManagementService.searchAvailableSlots(searchRequest);
//        return ResponseEntity.ok(slots);
//    }
//
//    /**
//     * Get available slots for a specific tutor on a specific date
//     */
//    @GetMapping("/slots")
//    public ResponseEntity<List<SlotInstanceDTO>> getTutorSlotsForDate(
//            @RequestParam Long tutorId,
//            @RequestParam String date) {
//
//        try {
//            LocalDate specificDate = LocalDate.parse(date);
//
//            SlotSearchRequestDTO searchRequest = SlotSearchRequestDTO.builder()
//                    .tutorId(tutorId)
//                    .specificDate(specificDate)
//                    .build();
//
//            List<SlotInstanceDTO> slots = slotManagementService.searchAvailableSlots(searchRequest);
//            return ResponseEntity.ok(slots);
//
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
//
//    /**
//     * Create a booking reservation (locks slot for limited time)
//     */
//    @PostMapping("/reserve")
//    public ResponseEntity<BookingDTO> createBookingReservation(
//            @Valid @RequestBody BookingRequestDTO request) {
//
//        BookingDTO booking = bookingService.createBookingReservation(request);
//        return ResponseEntity.ok(booking);
//    }
//
//    /**
//     * Process payment for a reserved booking
//     */
//    @PostMapping("/{bookingId}/payment")
//    public ResponseEntity<Map<String, Object>> processPayment(
//            @PathVariable Long bookingId,
//            @RequestBody BookingRequestDTO request) {
//
//        Map<String, Object> paymentResult = bookingService.processBookingPayment(bookingId, request);
//        return ResponseEntity.ok(paymentResult);
//    }
//
//    /**
//     * Cancel a booking reservation before payment
//     */
//    @DeleteMapping("/{bookingId}/cancel")
//    public ResponseEntity<Void> cancelBookingReservation(@PathVariable Long bookingId) {
//        bookingService.cancelBookingReservation(bookingId);
//        return ResponseEntity.ok().build();
//    }
//
//    /**
//     * Get student's bookings
//     */
//    @GetMapping("/student/{studentId}")
//    public ResponseEntity<List<BookingDTO>> getStudentBookings(
//            @PathVariable Long studentId,
//            @RequestParam(defaultValue = "false") boolean confirmedOnly) {
//
//        List<BookingDTO> bookings;
//        if (confirmedOnly) {
//            bookings = bookingService.getConfirmedStudentBookings(studentId);
//        } else {
//            bookings = bookingService.getStudentBookings(studentId);
//        }
//
//        return ResponseEntity.ok(bookings);
//    }
//
//    /**
//     * Get specific booking details
//     */
//    @GetMapping("/{bookingId}")
//    public ResponseEntity<BookingDTO> getBookingDetails(@PathVariable Long bookingId) {
//        BookingDTO booking = bookingService.getBookingById(bookingId);
//        return ResponseEntity.ok(booking);
//    }
//
//    /**
//     * Check if booking is still valid (not expired)
//     */
//    @GetMapping("/{bookingId}/status")
//    public ResponseEntity<Map<String, Object>> getBookingStatus(@PathVariable Long bookingId) {
//        boolean isValid = bookingService.isBookingValid(bookingId);
//        BookingDTO booking = bookingService.getBookingById(bookingId);
//
//        Map<String, Object> status = new java.util.HashMap<>();
//        status.put("isValid", isValid);
//        status.put("isConfirmed", booking.getIsConfirmed());
//        status.put("lockedUntil", booking.getLockedUntil());
//        status.put("bookingStatus", booking.getBookingStatus());
//
//        return ResponseEntity.ok(status);
//    }
//
//    /**
//     * Get student's booking history with pagination
//     */
//    @GetMapping("/student/{studentId}/history")
//    public ResponseEntity<List<BookingDTO>> getStudentBookingHistory(
//            @PathVariable Long studentId,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(defaultValue = "createdAt") String sortBy,
//            @RequestParam(defaultValue = "DESC") String sortDirection) {
//
//        // For now, return all bookings - in a real implementation, you'd implement pagination
//        List<BookingDTO> bookings = bookingService.getStudentBookings(studentId);
//        return ResponseEntity.ok(bookings);
//    }
//
//    /**
//     * Get upcoming confirmed bookings for a student
//     */
//    @GetMapping("/student/{studentId}/upcoming")
//    public ResponseEntity<List<BookingDTO>> getUpcomingBookings(@PathVariable Long studentId) {
//        List<BookingDTO> confirmedBookings = bookingService.getConfirmedStudentBookings(studentId);
//
//        // Filter for upcoming bookings (slot date >= today)
//        LocalDate today = LocalDate.now();
//        List<BookingDTO> upcomingBookings = confirmedBookings.stream()
//                .filter(booking -> !booking.getSlotDate().isBefore(today))
//                .toList();
//
//        return ResponseEntity.ok(upcomingBookings);
//    }
//
//    /**
//     * Get booking statistics for a student
//     */
//    @GetMapping("/student/{studentId}/stats")
//    public ResponseEntity<Map<String, Object>> getBookingStats(@PathVariable Long studentId) {
//        List<BookingDTO> allBookings = bookingService.getStudentBookings(studentId);
//        List<BookingDTO> confirmedBookings = bookingService.getConfirmedStudentBookings(studentId);
//
//        LocalDate today = LocalDate.now();
//        long upcomingCount = confirmedBookings.stream()
//                .filter(booking -> !booking.getSlotDate().isBefore(today))
//                .count();
//
//        long completedCount = confirmedBookings.stream()
//                .filter(booking -> booking.getSlotDate().isBefore(today))
//                .count();
//
//        double totalSpent = confirmedBookings.stream()
//                .filter(booking -> booking.getAmount() != null)
//                .mapToDouble(BookingDTO::getAmount)
//                .sum();
//
//        Map<String, Object> stats = new java.util.HashMap<>();
//        stats.put("totalBookings", allBookings.size());
//        stats.put("confirmedBookings", confirmedBookings.size());
//        stats.put("upcomingBookings", upcomingCount);
//        stats.put("completedBookings", completedCount);
//        stats.put("totalAmountSpent", totalSpent);
//
//        return ResponseEntity.ok(stats);
//    }
//
//    /**
//     * Quick book a slot (reserve and return payment details in one call)
//     */
//    @PostMapping("/quick-book")
//    public ResponseEntity<Map<String, Object>> quickBook(
//            @Valid @RequestBody BookingRequestDTO request) {
//
//        // Create reservation first
//        BookingDTO booking = bookingService.createBookingReservation(request);
//
//        // Get payment details
//        Map<String, Object> paymentResult = bookingService.processBookingPayment(booking.getBookingId(), request);
//
//        // Add booking details to response
//        paymentResult.put("booking", booking);
//
//        return ResponseEntity.ok(paymentResult);
//    }
//}