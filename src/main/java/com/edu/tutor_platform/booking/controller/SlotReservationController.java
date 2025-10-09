package com.edu.tutor_platform.booking.controller;

import com.edu.tutor_platform.booking.dto.SlotReservationRequestDTO;
import com.edu.tutor_platform.booking.dto.BulkReservationRequestDTO;
import com.edu.tutor_platform.booking.dto.SlotLockRequestDTO;
import com.edu.tutor_platform.booking.dto.SlotLockResponseDTO;
import com.edu.tutor_platform.booking.service.SlotBlockingService;
import com.edu.tutor_platform.booking.service.SlotManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
public class SlotReservationController {

    private final SlotBlockingService slotBlockingService;
    private final SlotManagementService slotManagementService;

    /**
     * Lock a slot directly for 15 minutes
     * Sets slot status to LOCKED with locked_until timestamp
     */
    // @PostMapping("/reserve")
    // public ResponseEntity<Map<String, Object>> reserveSlot(
    //         @Valid @RequestBody SlotReservationRequestDTO request) {
        
    //     log.info("Received slot reservation request for slot: {}", request.getSlotId());
        
    //     try {
    //         boolean success = slotBlockingService.lockSlotDirectly(request.getSlotId());
            
    //         Map<String, Object> response = new HashMap<>();
    //         response.put("success", success);
    //         response.put("slotId", request.getSlotId());
            
    //         if (success) {
    //             response.put("message", "Slot successfully locked for 15 minutes");
    //             response.put("status", "LOCKED");
    //             log.info("Successfully locked slot: {}", request.getSlotId());
    //             return ResponseEntity.ok(response);
    //         } else {
    //             response.put("message", "Slot is not available for reservation");
    //             response.put("status", "UNAVAILABLE");
    //             log.warn("Failed to lock slot: {} - slot not available", request.getSlotId());
    //             return ResponseEntity.badRequest().body(response);
    //         }
            
    //     } catch (Exception e) {
    //         log.error("Error reserving slot: {}", e.getMessage(), e);
            
    //         Map<String, Object> errorResponse = new HashMap<>();
    //         errorResponse.put("success", false);
    //         errorResponse.put("slotId", request.getSlotId());
    //         errorResponse.put("message", "Error reserving slot: " + e.getMessage());
    //         errorResponse.put("status", "ERROR");
            
    //         return ResponseEntity.internalServerError().body(errorResponse);
    //     }
    // }
    
    
    @PostMapping("/slots/reserve")
    public ResponseEntity<SlotLockResponseDTO> lockSlots(@RequestBody SlotLockRequestDTO request) {
        System.out.println("Locking slots1: " + request.getSlotIds());
        if (request == null || request.getSlotIds() == null || request.getSlotIds().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        SlotLockResponseDTO resp = slotManagementService.lockSlotsForCheckout(request.getSlotIds());
        return ResponseEntity.ok(resp);
    }
    @PostMapping("/slots/release")
    public ResponseEntity<SlotLockResponseDTO> releaseSlots(@RequestBody SlotLockRequestDTO request) {
        System.out.println("Releasing slots: " + (request != null ? request.getSlotIds() : null));
        if (request == null || request.getSlotIds() == null || request.getSlotIds().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        SlotLockResponseDTO resp = slotManagementService.releaseSlotsForCheckout(request.getSlotIds());
        return ResponseEntity.ok(resp);
    }
    // @PostMapping("/reserve-bulk")
    // public ResponseEntity<Map<String, Object>> reserveSlotsBulk(
    //         @Valid @RequestBody BulkReservationRequestDTO request) {

    //     log.info("Received bulk slot reservation request for slots: {} by student {}", request.getSlotIds(), request.getStudentId());

    //     try {
    //         var unavailable = slotBlockingService.reserveSlotsBulk(request.getSlotIds(), request.getStudentId());

    //         Map<String, Object> response = new HashMap<>();
    //         response.put("requestedSlotIds", request.getSlotIds());

    //         if (unavailable.isEmpty()) {
    //             response.put("success", true);
    //             response.put("message", "All slots successfully locked for 15 minutes");
    //             response.put("status", "LOCKED");
    //             return ResponseEntity.ok(response);
    //         } else {
    //             response.put("success", false);
    //             response.put("message", "Some slots are not available for reservation");
    //             response.put("unavailableSlots", unavailable);
    //             response.put("status", "PARTIAL_UNAVAILABLE");
    //             return ResponseEntity.badRequest().body(response);
    //         }

    //     } catch (Exception e) {
    //         log.error("Error reserving slots: {}", e.getMessage(), e);

    //         Map<String, Object> errorResponse = new HashMap<>();
    //         errorResponse.put("requestedSlotIds", request.getSlotIds());
    //         errorResponse.put("success", false);
    //         errorResponse.put("message", "Error reserving slots: " + e.getMessage());
    //         errorResponse.put("status", "ERROR");

    //         return ResponseEntity.internalServerError().body(errorResponse);
    //     }
    // }

    /**
     * Release a locked slot back to AVAILABLE status
     * Changes LOCKED status back to AVAILABLE
     */
    @PostMapping("/release")
    public ResponseEntity<Map<String, Object>> releaseSlot(
            @Valid @RequestBody SlotReservationRequestDTO request) {
        
        log.info("Received slot release request for slot: {}", request.getSlotId());
        
        try {
            boolean success = slotBlockingService.releaseSlotDirectly(request.getSlotId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("slotId", request.getSlotId());
            
            if (success) {
                response.put("message", "Slot successfully released and made available");
                response.put("status", "AVAILABLE");
                log.info("Successfully released slot: {}", request.getSlotId());
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Slot was not locked or could not be released");
                response.put("status", "NOT_LOCKED");
                log.warn("Failed to release slot: {} - slot was not locked", request.getSlotId());
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            log.error("Error releasing slot: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("slotId", request.getSlotId());
            errorResponse.put("message", "Error releasing slot: " + e.getMessage());
            errorResponse.put("status", "ERROR");
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    @PostMapping("/book-slot")
    public ResponseEntity<Map<String, Object>> bookSlot(
            @Valid @RequestBody SlotReservationRequestDTO request) {

        log.info("Received slot booking request for slot: {}", request.getSlotId());

        try {
            boolean success = slotBlockingService.bookSlotDirectly(request.getSlotId());

            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("slotId", request.getSlotId());

            if (success) {
                response.put("message", "Slot successfully booked");
                response.put("status", "BOOKED");
                log.info("Slot {} marked as BOOKED", request.getSlotId());
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Slot could not be booked");
                response.put("status", "FAILED");
                log.warn("Failed to book slot: {}", request.getSlotId());
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            log.error("Error booking slot: {}", e.getMessage(), e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("slotId", request.getSlotId());
            errorResponse.put("message", "Error booking slot: " + e.getMessage());
            errorResponse.put("status", "ERROR");

            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Check the status of a slot
     */
    // @GetMapping("/slot/{slotId}/status")
    // public ResponseEntity<Map<String, Object>> getSlotStatus(@PathVariable Long slotId) {
        
    //     log.info("Checking status for slot: {}", slotId);
        
    //     try {
    //         boolean isAvailable = slotBlockingService.isSlotAvailable(slotId);
            
    //         Map<String, Object> response = new HashMap<>();
    //         response.put("slotId", slotId);
    //         response.put("isAvailable", isAvailable);
    //         response.put("status", isAvailable ? "AVAILABLE" : "NOT_AVAILABLE");
            
    //         log.info("Slot {} status: {}", slotId, isAvailable ? "AVAILABLE" : "NOT_AVAILABLE");
    //         return ResponseEntity.ok(response);
            
    //     } catch (Exception e) {
    //         log.error("Error checking slot status: {}", e.getMessage(), e);
            
    //         Map<String, Object> errorResponse = new HashMap<>();
    //         errorResponse.put("slotId", slotId);
    //         errorResponse.put("isAvailable", false);
    //         errorResponse.put("status", "ERROR");
    //         errorResponse.put("message", "Error checking slot status: " + e.getMessage());
            
    //         return ResponseEntity.internalServerError().body(errorResponse);
    //     }
    // }
}