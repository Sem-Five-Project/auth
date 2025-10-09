// package com.edu.tutor_platform.booking.controller;

// import com.edu.tutor_platform.booking.dto.TutorAvailabilityDTO;
// import com.edu.tutor_platform.booking.dto.SlotInstanceDTO;
// import com.edu.tutor_platform.booking.dto.SlotSearchRequestDTO;
// import com.edu.tutor_platform.booking.service.SlotManagementService;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import jakarta.validation.Valid;
// import java.time.LocalDate;
// import java.util.List;

// @RestController
// @RequestMapping("/tutor/slots")
// @RequiredArgsConstructor
// public class TutorSlotController {

//     private final SlotManagementService slotManagementService;

//     /**
//      * Create or update tutor availability
//      */
//     @PostMapping("/availability")
//     public ResponseEntity<TutorAvailabilityDTO> createOrUpdateAvailability(
//             @Valid @RequestBody TutorAvailabilityDTO availabilityDTO) {

//         TutorAvailabilityDTO result = slotManagementService.createOrUpdateAvailability(availabilityDTO);
//         return ResponseEntity.ok(result);
//     }

//     /**
//      * Get all availability for a tutor
//      */
//     @GetMapping("/availability/{tutorId}")
//     public ResponseEntity<List<TutorAvailabilityDTO>> getTutorAvailability(
//             @PathVariable Long tutorId) {

//         List<TutorAvailabilityDTO> availability = slotManagementService.getTutorAvailability(tutorId);
//         return ResponseEntity.ok(availability);
//     }

//     /**
//      * Delete tutor availability
//      */
//     @DeleteMapping("/availability/{availabilityId}")
//     public ResponseEntity<Void> deleteAvailability(@PathVariable Long availabilityId) {
//         slotManagementService.deleteAvailability(availabilityId);
//         return ResponseEntity.ok().build();
//     }

//     /**
//      * Get tutor's slot instances for a specific date range
//      */
//     @GetMapping("/{tutorId}")
//     public ResponseEntity<List<SlotInstanceDTO>> getTutorSlots(
//             @PathVariable Long tutorId,
//             @RequestParam(required = false) LocalDate startDate,
//             @RequestParam(required = false) LocalDate endDate,
//             @RequestParam(required = false) LocalDate specificDate) {

//         SlotSearchRequestDTO searchRequest = SlotSearchRequestDTO.builder()
//                 .tutorId(tutorId)
//                 .startDate(startDate != null ? startDate : LocalDate.now())
//                 .endDate(endDate != null ? endDate : LocalDate.now().plusWeeks(2))
//                 .specificDate(specificDate)
//                 .build();

//         List<SlotInstanceDTO> slots = slotManagementService.searchAvailableSlots(searchRequest);
//         return ResponseEntity.ok(slots);
//     }

//     /**
//      * Generate recurring slots manually (usually done automatically)
//      */
//     @PostMapping("/generate-recurring/{tutorId}")
//     public ResponseEntity<String> generateRecurringSlots(@PathVariable Long tutorId) {
//         List<TutorAvailabilityDTO> recurringAvailabilities = slotManagementService
//                 .getTutorAvailability(tutorId)
//                 .stream()
//                 .filter(TutorAvailabilityDTO::getRecurring)
//                 .toList();

//         int generatedCount = 0;
//         for (TutorAvailabilityDTO availability : recurringAvailabilities) {
//             // This would trigger slot generation for this availability
//             generatedCount++;
//         }

//         return ResponseEntity.ok("Generated slots for " + generatedCount + " recurring availabilities");
//     }

//     /**
//      * Get availability statistics for a tutor
//      */
//     @GetMapping("/stats/{tutorId}")
//     public ResponseEntity<Object> getAvailabilityStats(@PathVariable Long tutorId) {
//         List<TutorAvailabilityDTO> availabilities = slotManagementService.getTutorAvailability(tutorId);

//         long totalAvailabilities = availabilities.size();
//         long recurringCount = availabilities.stream()
//                 .filter(TutorAvailabilityDTO::getRecurring)
//                 .count();
//         long totalGeneratedSlots = availabilities.stream()
//                 .mapToLong(a -> a.getGeneratedSlots() != null ? a.getGeneratedSlots() : 0)
//                 .sum();

//         java.util.Map<String, Object> stats = new java.util.HashMap<>();
//         stats.put("totalAvailabilities", totalAvailabilities);
//         stats.put("recurringAvailabilities", recurringCount);
//         stats.put("oneTimeAvailabilities", totalAvailabilities - recurringCount);
//         stats.put("totalGeneratedSlots", totalGeneratedSlots);

//         return ResponseEntity.ok(stats);
//     }

//     /**
//      * Bulk create availability (for initial tutor setup)
//      */
//     @PostMapping("/availability/bulk")
//     public ResponseEntity<List<TutorAvailabilityDTO>> bulkCreateAvailability(
//             @Valid @RequestBody List<TutorAvailabilityDTO> availabilities) {

//         List<TutorAvailabilityDTO> results = availabilities.stream()
//                 .map(slotManagementService::createOrUpdateAvailability)
//                 .toList();

//         return ResponseEntity.ok(results);
//     }

//     /**
//      * Update availability status (enable/disable)
//      */
//     @PatchMapping("/availability/{availabilityId}/status")
//     public ResponseEntity<TutorAvailabilityDTO> updateAvailabilityStatus(
//             @PathVariable Long availabilityId,
//             @RequestParam Boolean enabled) {

//         // This would be implemented to enable/disable availability without deleting
//         // For now, we'll return a simple response
//         return ResponseEntity.ok().build();
//     }
// }