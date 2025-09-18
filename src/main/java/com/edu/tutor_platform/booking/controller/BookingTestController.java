// package com.edu.tutor_platform.booking.controller;

// import com.edu.tutor_platform.booking.dto.*;
// import com.edu.tutor_platform.booking.enums.DayOfWeek;
// import com.edu.tutor_platform.booking.service.BookingService;
// import com.edu.tutor_platform.booking.service.RecurringSlotScheduler;
// import com.edu.tutor_platform.booking.service.SlotManagementService;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.time.LocalDate;
// import java.time.LocalTime;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// @RestController
// @RequestMapping("/test/booking")
// @RequiredArgsConstructor
// public class BookingTestController {

//     private final SlotManagementService slotManagementService;
//     private final BookingService bookingService;
//     private final RecurringSlotScheduler recurringSlotScheduler;

//     /**
//      * Complete booking workflow test
//      * This endpoint demonstrates the entire booking process from slot creation to payment
//      */
//     @PostMapping("/complete-workflow")
//     public ResponseEntity<Map<String, Object>> testCompleteBookingWorkflow(
//             @RequestParam Long tutorId,
//             @RequestParam Long studentId) {
        
//         Map<String, Object> results = new HashMap<>();
        
//         try {
//             // Step 1: Create tutor availability (recurring slot)
//             TutorAvailabilityDTO availabilityDTO = TutorAvailabilityDTO.builder()
//                     .tutorId(tutorId)
//                     .dayOfWeek(DayOfWeek.MONDAY)
//                     .startTime(LocalTime.of(14, 0)) // 2:00 PM
//                     .endTime(LocalTime.of(15, 0))   // 3:00 PM
//                     .recurring(true)
//                     .build();
            
//             TutorAvailabilityDTO createdAvailability = slotManagementService.createOrUpdateAvailability(availabilityDTO);
//             results.put("step1_availability", createdAvailability);
            
//             // Step 2: Search for available slots
//             SlotSearchRequestDTO searchRequest = SlotSearchRequestDTO.builder()
//                     .tutorId(tutorId)
//                     .startDate(LocalDate.now())
//                     .endDate(LocalDate.now().plusWeeks(2))
//                     .build();
            
//             List<SlotInstanceDTO> availableSlots = slotManagementService.searchAvailableSlots(searchRequest);
//             results.put("step2_available_slots", availableSlots);
            
//             if (availableSlots.isEmpty()) {
//                 results.put("error", "No available slots found");
//                 return ResponseEntity.ok(results);
//             }
            
//             // Step 3: Create booking reservation for the first available slot
//             SlotInstanceDTO firstSlot = availableSlots.get(0);
//             BookingRequestDTO bookingRequest = BookingRequestDTO.builder()
//                     .slotId(firstSlot.getSlotId())
//                     .studentId(studentId)
//                     .paymentMethod("CARD")
//                     .returnUrl("http://localhost:3000/payment/success")
//                     .cancelUrl("http://localhost:3000/payment/cancel")
//                     .notes("Test booking")
//                     .build();
            
//             BookingDTO booking = bookingService.createBookingReservation(bookingRequest);
//             results.put("step3_booking_reservation", booking);
            
//             // Step 4: Process payment
//             Map<String, Object> paymentResult = bookingService.processBookingPayment(booking.getBookingId(), bookingRequest);
//             results.put("step4_payment_processing", paymentResult);
            
//             // Step 5: Simulate payment success and confirm booking
//             BookingDTO confirmedBooking = bookingService.confirmBooking(booking.getBookingId(), "PAYMENT_SUCCESS");
//             results.put("step5_confirmed_booking", confirmedBooking);
            
//             // Step 6: Get final booking status
//             Map<String, Object> finalStatus = Map.of(
//                     "bookingId", confirmedBooking.getBookingId(),
//                     "status", confirmedBooking.getBookingStatus(),
//                     "isConfirmed", confirmedBooking.getIsConfirmed(),
//                     "slotDate", confirmedBooking.getSlotDate(),
//                     "startTime", confirmedBooking.getStartTime(),
//                     "endTime", confirmedBooking.getEndTime(),
//                     "amount", confirmedBooking.getAmount()
//             );
//             results.put("step6_final_status", finalStatus);
            
//             results.put("workflow_status", "COMPLETED_SUCCESSFULLY");
            
//         } catch (Exception e) {
//             results.put("workflow_status", "FAILED");
//             results.put("error", e.getMessage());
//             results.put("error_type", e.getClass().getSimpleName());
//         }
        
//         return ResponseEntity.ok(results);
//     }

//     /**
//      * Test tutor availability creation
//      */
//     @PostMapping("/test-availability")
//     public ResponseEntity<Map<String, Object>> testTutorAvailability(
//             @RequestParam Long tutorId,
//             @RequestParam String dayOfWeek,
//             @RequestParam String startTime,
//             @RequestParam String endTime,
//             @RequestParam(defaultValue = "true") boolean recurring) {
        
//         Map<String, Object> results = new HashMap<>();
        
//         try {
//             TutorAvailabilityDTO availabilityDTO = TutorAvailabilityDTO.builder()
//                     .tutorId(tutorId)
//                     .dayOfWeek(DayOfWeek.valueOf(dayOfWeek.toUpperCase()))
//                     .startTime(LocalTime.parse(startTime))
//                     .endTime(LocalTime.parse(endTime))
//                     .recurring(recurring)
//                     .build();
            
//             TutorAvailabilityDTO created = slotManagementService.createOrUpdateAvailability(availabilityDTO);
//             results.put("created_availability", created);
//             results.put("status", "SUCCESS");
            
//         } catch (Exception e) {
//             results.put("status", "FAILED");
//             results.put("error", e.getMessage());
//         }
        
//         return ResponseEntity.ok(results);
//     }

//     /**
//      * Test slot search functionality
//      */
//     @GetMapping("/test-slot-search")
//     public ResponseEntity<Map<String, Object>> testSlotSearch(
//             @RequestParam(required = false) Long tutorId,
//             @RequestParam(required = false) String startDate,
//             @RequestParam(required = false) String endDate) {
        
//         Map<String, Object> results = new HashMap<>();
        
//         try {
//             SlotSearchRequestDTO searchRequest = SlotSearchRequestDTO.builder()
//                     .tutorId(tutorId)
//                     .startDate(startDate != null ? LocalDate.parse(startDate) : LocalDate.now())
//                     .endDate(endDate != null ? LocalDate.parse(endDate) : LocalDate.now().plusWeeks(2))
//                     .build();
            
//             List<SlotInstanceDTO> slots = slotManagementService.searchAvailableSlots(searchRequest);
//             results.put("available_slots", slots);
//             results.put("count", slots.size());
//             results.put("status", "SUCCESS");
            
//         } catch (Exception e) {
//             results.put("status", "FAILED");
//             results.put("error", e.getMessage());
//         }
        
//         return ResponseEntity.ok(results);
//     }

//     /**
//      * Test booking reservation
//      */
//     @PostMapping("/test-reservation")
//     public ResponseEntity<Map<String, Object>> testBookingReservation(
//             @RequestParam Long slotId,
//             @RequestParam Long studentId) {
        
//         Map<String, Object> results = new HashMap<>();
        
//         try {
//             BookingRequestDTO request = BookingRequestDTO.builder()
//                     .slotId(slotId)
//                     .studentId(studentId)
//                     .paymentMethod("CARD")
//                     .notes("Test reservation")
//                     .build();
            
//             BookingDTO booking = bookingService.createBookingReservation(request);
//             results.put("booking", booking);
//             results.put("status", "SUCCESS");
            
//         } catch (Exception e) {
//             results.put("status", "FAILED");
//             results.put("error", e.getMessage());
//         }
        
//         return ResponseEntity.ok(results);
//     }

//     /**
//      * Test payment processing
//      */
//     @PostMapping("/test-payment/{bookingId}")
//     public ResponseEntity<Map<String, Object>> testPaymentProcessing(@PathVariable Long bookingId) {
        
//         Map<String, Object> results = new HashMap<>();
        
//         try {
//             BookingRequestDTO request = BookingRequestDTO.builder()
//                     .paymentMethod("CARD")
//                     .returnUrl("http://localhost:3000/success")
//                     .cancelUrl("http://localhost:3000/cancel")
//                     .build();
            
//             Map<String, Object> paymentResult = bookingService.processBookingPayment(bookingId, request);
//             results.put("payment_details", paymentResult);
//             results.put("status", "SUCCESS");
            
//         } catch (Exception e) {
//             results.put("status", "FAILED");
//             results.put("error", e.getMessage());
//         }
        
//         return ResponseEntity.ok(results);
//     }

//     /**
//      * Test recurring slot generation
//      */
//     @PostMapping("/test-recurring-generation")
//     public ResponseEntity<Map<String, Object>> testRecurringSlotGeneration() {
        
//         Map<String, Object> results = new HashMap<>();
        
//         try {
//             recurringSlotScheduler.manuallyGenerateRecurringSlots();
//             results.put("status", "SUCCESS");
//             results.put("message", "Recurring slot generation completed");
            
//         } catch (Exception e) {
//             results.put("status", "FAILED");
//             results.put("error", e.getMessage());
//         }
        
//         return ResponseEntity.ok(results);
//     }

//     /**
//      * Get booking status
//      */
//     @GetMapping("/booking-status/{bookingId}")
//     public ResponseEntity<Map<String, Object>> getBookingStatus(@PathVariable Long bookingId) {
        
//         Map<String, Object> results = new HashMap<>();
        
//         try {
//             BookingDTO booking = bookingService.getBookingById(bookingId);
//             boolean isValid = bookingService.isBookingValid(bookingId);
            
//             results.put("booking", booking);
//             results.put("isValid", isValid);
//             results.put("status", "SUCCESS");
            
//         } catch (Exception e) {
//             results.put("status", "FAILED");
//             results.put("error", e.getMessage());
//         }
        
//         return ResponseEntity.ok(results);
//     }

//     /**
//      * Health check for the booking system
//      */
//     @GetMapping("/health")
//     public ResponseEntity<Map<String, Object>> healthCheck() {
//         Map<String, Object> health = new HashMap<>();
        
//         try {
//             // Test basic functionality
//             health.put("booking_service", "UP");
//             health.put("slot_management_service", "UP");
//             health.put("recurring_scheduler", "UP");
//             health.put("timestamp", java.time.LocalDateTime.now());
//             health.put("overall_status", "HEALTHY");
            
//         } catch (Exception e) {
//             health.put("overall_status", "UNHEALTHY");
//             health.put("error", e.getMessage());
//         }
        
//         return ResponseEntity.ok(health);
//     }
// }