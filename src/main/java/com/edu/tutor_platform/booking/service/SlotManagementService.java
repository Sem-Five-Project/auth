package com.edu.tutor_platform.booking.service;

import com.edu.tutor_platform.booking.dto.SlotInstanceDTO;
import com.edu.tutor_platform.booking.dto.SlotLockResponseDTO;

import com.edu.tutor_platform.booking.dto.SlotInstanceSummaryDTO;
import com.edu.tutor_platform.booking.dto.SlotSearchRequestDTO;
import com.edu.tutor_platform.booking.dto.TutorAvailabilityDTO;
import com.edu.tutor_platform.booking.entity.SlotInstance;
import com.edu.tutor_platform.booking.entity.TutorAvailability;
import com.edu.tutor_platform.booking.enums.DayOfWeek;
import com.edu.tutor_platform.booking.enums.SlotStatus;
import com.edu.tutor_platform.booking.repository.SlotInstanceRepository;
import com.edu.tutor_platform.clazz.repository.TutorAvailabilityRepository;
import com.edu.tutor_platform.tutorprofile.entity.TutorProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.edu.tutor_platform.tutorprofile.repository.TutorProfileRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SlotManagementService {

    //private final TutorAvailabilityRepository tutorAvailabilityRepository;
    private final SlotInstanceRepository slotInstanceRepository;
    private final TutorProfileRepository tutorProfileRepository;
    //private final BookingValidationService validationService;

    /**
     * Create or update tutor availability
     */
    //@Transactional
    // public TutorAvailabilityDTO createOrUpdateAvailability(TutorAvailabilityDTO dto) {
    //     log.info("Creating/updating availability for tutor: {}", dto.getTutorId());
        
    //     // Validate the availability DTO
    //     validationService.validateTutorAvailability(dto);
        
    //     // Validate recurring settings if applicable
    //     if (dto.getRecurring() != null && dto.getRecurring()) {
    //         validationService.validateRecurringSettings(dto);
    //     }
        
    //     // Validate tutor exists
    //     TutorProfile tutorProfile = tutorProfileRepository.findById(dto.getTutorId())
    //             .orElseThrow(() -> new RuntimeException("Tutor not found"));

    //     TutorAvailability availability;
    //     if (dto.getAvailabilityId() != null) {
    //         // Update existing
    //         availability = tutorAvailabilityRepository.findById(dto.getAvailabilityId())
    //                 .orElseThrow(() -> new RuntimeException("Availability not found"));
    //         updateAvailabilityFromDTO(availability, dto);
    //     } else {
    //         // Create new
    //         availability = TutorAvailability.builder()
    //                 .tutorProfile(tutorProfile)
    //                 .dayOfWeek(dto.getDayOfWeek())
    //                 .startTime(dto.getStartTime())
    //                 .endTime(dto.getEndTime())
    //                 .recurring(dto.getRecurring())
    //                 .build();
    //     }

    //     availability = tutorAvailabilityRepository.save(availability);

    //     // Generate slot instances if this is recurring or for the next 2 weeks
    //     if (availability.getRecurring()) {
    //         generateRecurringSlots(availability);
    //     } else {
    //         generateSlotsForNextTwoWeeks(availability);
    //     }

    //     return convertToDTO(availability);
    // }
 public List<SlotInstanceDTO> findMonthlyRecurringSlots(Long tutorId, DayOfWeek weekday, int month, int year) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        List<SlotInstance> slots = slotInstanceRepository.findRecurringSlotsInMonth(tutorId, weekday, start, end);
        return slots.stream().map(this::toDto).toList();
    }

    private SlotInstanceDTO toDto(SlotInstance si) {
        // Map the fields you need (adjust to your DTO)
        return SlotInstanceDTO.builder()
                .slotId(si.getSlotId())
                .slotDate(si.getSlotDate())
                .status(si.getStatus())
                .availabilityId(si.getTutorAvailability() != null ? si.getTutorAvailability().getAvailabilityId() : null)
                .build();
    }
    /**
     * Generate recurring slots for the next 8 weeks
     */
    @Transactional
    public void generateRecurringSlots(TutorAvailability availability) {
        if (!availability.getRecurring()) {
            log.warn("Attempting to generate recurring slots for non-recurring availability: {}", 
                    availability.getAvailabilityId());
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusWeeks(8); // Generate 8 weeks ahead
        
        generateSlotsForDateRange(availability, today, endDate);
    }
    @Transactional
    public SlotLockResponseDTO lockSlotsForCheckout(List<Long> slotIds) {
        System.out.println("Locking slots: " + slotIds);
        if (slotIds == null || slotIds.isEmpty()) {
            return SlotLockResponseDTO.builder().success(false).failedSlots(List.of()).build();
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lockUntil = now.plusMinutes(15);

        List<SlotInstance> slots = slotInstanceRepository.findAllForUpdateByIds(slotIds);
        Set<Long> found = slots.stream().map(SlotInstance::getSlotId).collect(Collectors.toSet());

        List<Long> failed = new ArrayList<>();

        // Mark failures: already LOCKED (not expired) or BOOKED
        for (SlotInstance si : slots) {
            boolean lockedActive = si.getStatus() == SlotStatus.LOCKED
                    && si.getLockedUntil() != null
                    && si.getLockedUntil().isAfter(now);
            boolean booked = si.getStatus() == SlotStatus.BOOKED;

            if (lockedActive || booked) {
                failed.add(si.getSlotId());
            }
        }

        // Any ids not found -> fail them too
        for (Long id : slotIds) {
            if (!found.contains(id)) {
                failed.add(id);
            }
        }

        if (!failed.isEmpty()) {
            return SlotLockResponseDTO.builder().success(false).failedSlots(failed).build();
        }

        // Lock all
        for (SlotInstance si : slots) {
            si.setStatus(SlotStatus.LOCKED);
            si.setLockedUntil(lockUntil);
        }
        // Rely on JPA dirty checking or call saveAll(slots)
        // slotInstanceRepository.saveAll(slots);

        return SlotLockResponseDTO.builder().success(true).failedSlots(List.of()).build();
    }
    
    @Transactional
    public SlotLockResponseDTO releaseSlotsForCheckout(List<Long> slotIds) {
        if (slotIds == null || slotIds.isEmpty()) {
            return SlotLockResponseDTO.builder().success(false).failedSlots(List.of()).build();
        }

        var now = java.time.LocalDateTime.now();
        var failed = new java.util.ArrayList<Long>();

        var slots = slotInstanceRepository.findAllForUpdateByIds(slotIds);
        var found = slots.stream().map(SlotInstance::getSlotId).collect(java.util.stream.Collectors.toSet());

        for (var si : slots) {
            boolean lockedActive = si.getStatus() == SlotStatus.LOCKED
                    && si.getLockedUntil() != null
                    && si.getLockedUntil().isAfter(now);

            if (!lockedActive) {
                // Not currently locked → fail (mirrors single release behavior)
                failed.add(si.getSlotId());
                continue;
            }

            // Release
            si.setStatus(SlotStatus.AVAILABLE);
            si.setLockedUntil(null);
        }

        // Any ids not found → fail
        for (Long id : slotIds) {
            if (!found.contains(id)) failed.add(id);
        }

        if (!failed.isEmpty()) {
            return SlotLockResponseDTO.builder().success(false).failedSlots(failed).build();
        }
        return SlotLockResponseDTO.builder().success(true).failedSlots(java.util.List.of()).build();
    }
    /**
     * Generate slots for next 2 weeks for manual (non-recurring) availability
     */
    @Transactional
    public void generateSlotsForNextTwoWeeks(TutorAvailability availability) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusWeeks(2);
        
        generateSlotsForDateRange(availability, today, endDate);
    }

    /**
     * Generate slot instances for a specific date range and availability
     */
    private void generateSlotsForDateRange(TutorAvailability availability, LocalDate startDate, LocalDate endDate) {
        DayOfWeek targetDayOfWeek = availability.getDayOfWeek();
        
        // Find the first occurrence of the target day of week
        LocalDate currentDate = startDate;
        while (currentDate.getDayOfWeek() != java.time.DayOfWeek.valueOf(targetDayOfWeek.name())) {
            currentDate = currentDate.plusDays(1);
            if (currentDate.isAfter(endDate)) {
                return; // No occurrences in the date range
            }
        }

        // Generate slots for each occurrence of the day of week
        while (!currentDate.isAfter(endDate)) {
            // Check if slot already exists for this date
            if (!slotInstanceRepository.existsByTutorAvailabilityAvailabilityIdAndSlotDate(
                    availability.getAvailabilityId(), currentDate)) {
                
                SlotInstance slotInstance = SlotInstance.builder()
                        .tutorAvailability(availability)
                        .slotDate(currentDate)
                        .status(SlotStatus.AVAILABLE)
                        .build();
                
                slotInstanceRepository.save(slotInstance);
                log.debug("Generated slot for tutor {} on {}", 
                         availability.getTutorProfile().getTutorId(), currentDate);
            }
            
            currentDate = currentDate.plusWeeks(1); // Move to next week
        }
    }

    /**
     * Get all availability for a tutor
     */
    // public List<TutorAvailabilityDTO> getTutorAvailability(Long tutorId) {
    //     List<TutorAvailability> availabilities = tutorAvailabilityRepository
    //             .findByTutorProfileTutorId(tutorId);
        
    //     return availabilities.stream()
    //             .map(this::convertToDTO)
    //             .collect(Collectors.toList());
    // }

   //ok
    public List<SlotInstanceDTO>searchAvailableSlots(SlotSearchRequestDTO searchRequest) {
        List<SlotInstance> slots = new ArrayList<>();

        if (searchRequest.getTutorId() != null) {
            // Search for specific tutor
            if (searchRequest.getSpecificDate() != null) {
                slots = slotInstanceRepository.findByTutorIdAndDateAndStatus(
                        searchRequest.getTutorId(), 
                        searchRequest.getSpecificDate(), 
                        SlotStatus.AVAILABLE
                );
                log.debug("Found {} available slots for tutor {} on {}", slots.size(), searchRequest.getTutorId(), searchRequest.getSpecificDate());
            } else if (searchRequest.getStartDate() != null && searchRequest.getEndDate() != null) {
                slots = slotInstanceRepository.findAvailableSlotsByTutorAndDateRange(
                        searchRequest.getTutorId(),
                        searchRequest.getStartDate(),
                        searchRequest.getEndDate()
                );
            }
        } else {
            // Search across all tutors (implement based on needs)
            if (searchRequest.getSpecificDate() != null) {
                slots = slotInstanceRepository.findBySlotDate(searchRequest.getSpecificDate())
                        .stream()
                        .filter(slot -> slot.getStatus() == SlotStatus.AVAILABLE)
                        .collect(Collectors.toList());
            }
        }

        return slots.stream()
                .map(this::convertSlotToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Delete tutor availability and all associated slot instances
     */
    // @Transactional
    // public void deleteAvailability(Long availabilityId) {
    //     TutorAvailability availability = tutorAvailabilityRepository.findById(availabilityId)
    //             .orElseThrow(() -> new RuntimeException("Availability not found"));

    //     // Delete all slot instances first
    //     slotInstanceRepository.deleteByTutorAvailabilityAvailabilityId(availabilityId);
        
    //     // Delete availability
    //     tutorAvailabilityRepository.delete(availability);
        
    //     log.info("Deleted availability {} and all associated slot instances", availabilityId);
    // }

    /**
     * Weekly recurring slot generation (called by scheduled task)
     */
    @Transactional
    // public void generateWeeklyRecurringSlots() {
    //     log.info("Starting weekly recurring slot generation");
        
    //     List<TutorAvailability> recurringAvailabilities = tutorAvailabilityRepository.findByRecurringTrue();
        
    //     LocalDate nextWeekStart = LocalDate.now().plusWeeks(8); // Generate 8 weeks ahead
    //     LocalDate nextWeekEnd = nextWeekStart.plusDays(6);
        
    //     for (TutorAvailability availability : recurringAvailabilities) {
    //         generateSlotsForDateRange(availability, nextWeekStart, nextWeekEnd);
    //     }
        
    //     log.info("Completed weekly recurring slot generation for {} availabilities", 
    //             recurringAvailabilities.size());
    // }


    /**
     * Update availability entity from DTO
     */
    private void updateAvailabilityFromDTO(TutorAvailability availability, TutorAvailabilityDTO dto) {
        availability.setDayOfWeek(dto.getDayOfWeek());
        availability.setStartTime(dto.getStartTime());
        availability.setEndTime(dto.getEndTime());
        availability.setRecurring(dto.getRecurring());
    }

    /**
     * Convert entity to DTO
     */
    private TutorAvailabilityDTO convertToDTO(TutorAvailability availability) {
        return TutorAvailabilityDTO.builder()
                .availabilityId(availability.getAvailabilityId())
                .tutorId(availability.getTutorProfile().getTutorId())
                .tutorName(availability.getTutorProfile().getUser().getFirstName() + " " + 
                          availability.getTutorProfile().getUser().getLastName())
                .dayOfWeek(availability.getDayOfWeek())
                .startTime(availability.getStartTime())
                .endTime(availability.getEndTime())
                .recurring(availability.getRecurring())
                .generatedSlots(availability.getSlotInstances().size())
                .build();
    }

    /**
     * Convert slot instance to DTO
     */
    private SlotInstanceDTO convertSlotToDTO(SlotInstance slot) {
        TutorProfile tutorProfile = slot.getTutorAvailability().getTutorProfile();
        
        return SlotInstanceDTO.builder()
                .slotId(slot.getSlotId())
                .availabilityId(slot.getTutorAvailability().getAvailabilityId())
                .tutorId(tutorProfile.getTutorId())
                .tutorName(tutorProfile.getUser().getFirstName() + " " + 
                          tutorProfile.getUser().getLastName())
                .slotDate(slot.getSlotDate())
                .dayOfWeek(DayOfWeek.valueOf(slot.getSlotDate().getDayOfWeek().name()))
                .startTime(slot.getTutorAvailability().getStartTime())
                .endTime(slot.getTutorAvailability().getEndTime())
                .status(slot.getStatus())
                .hourlyRate(tutorProfile.getHourlyRate() != null ? tutorProfile.getHourlyRate().doubleValue() : null)
                .tutorBio(tutorProfile.getBio())
                .tutorExperience(tutorProfile.getExperienceInMonths())
                .isRecurring(slot.getTutorAvailability().getRecurring())
                .rating(tutorProfile.getRating() != null ? tutorProfile.getRating().doubleValue() : null)
                .build();
    }
}







// package com.edu.tutor_platform.booking.service;

// import com.edu.tutor_platform.booking.dto.*;
// import com.edu.tutor_platform.booking.entity.SlotInstance;
// import com.edu.tutor_platform.booking.entity.TutorAvailability;
// import com.edu.tutor_platform.booking.enums.DayOfWeek;
// import com.edu.tutor_platform.booking.enums.SlotStatus;
// import com.edu.tutor_platform.booking.repository.SlotInstanceRepository;
// import com.edu.tutor_platform.clazz.repository.TutorAvailabilityRepository;
// import com.edu.tutor_platform.tutorprofile.entity.TutorProfile;
// import com.edu.tutor_platform.tutorprofile.repository.TutorProfileRepository;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.util.*;
// import java.util.stream.Collectors;

// @Service
// @RequiredArgsConstructor
// @Slf4j
// public class SlotManagementService {

//     private final TutorAvailabilityRepository tutorAvailabilityRepository;
//     private final SlotInstanceRepository slotInstanceRepository;
//     private final TutorProfileRepository tutorProfileRepository;
//     // private final BookingValidationService validationService; // add if exists

//     @Transactional
//     public TutorAvailabilityDTO createOrUpdateAvailability(TutorAvailabilityDTO dto) {
//         log.info("Create/Update availability tutorId {}", dto.getTutorId());

//         TutorProfile tutorProfile = tutorProfileRepository.findById(dto.getTutorId())
//                 .orElseThrow(() -> new RuntimeException("Tutor not found"));

//         TutorAvailability availability;
//         if (dto.getAvailabilityId() != null) {
//             availability = tutorAvailabilityRepository.findById(dto.getAvailabilityId())
//                     .orElseThrow(() -> new RuntimeException("Availability not found"));
//             updateAvailabilityFromDTO(availability, dto);
//         } else {
//             availability = TutorAvailability.builder()
//                     .tutorProfile(tutorProfile)
//                     .dayOfWeek(dto.getDayOfWeek())
//                     .startTime(dto.getStartTime())
//                     .endTime(dto.getEndTime())
//                     .recurring(Boolean.TRUE.equals(dto.getRecurring()))
//                     .build();
//         }

//         availability = tutorAvailabilityRepository.save(availability);

//         if (availability.getRecurring()) {
//             generateRecurringSlots(availability);
//         } else {
//             generateSlotsForNextTwoWeeks(availability);
//         }

//         return convertToDTO(availability);
//     }

//     public List<TutorAvailabilityDTO> getTutorAvailability(Long tutorId) {
//         return tutorAvailabilityRepository.findByTutorProfileTutorId(tutorId)
//                 .stream()
//                 .map(this::convertToDTO)
//                 .toList();
//     }

//     @Transactional
//     public void deleteAvailability(Long availabilityId) {
//         TutorAvailability availability = tutorAvailabilityRepository.findById(availabilityId)
//                 .orElseThrow(() -> new RuntimeException("Availability not found"));

//         slotInstanceRepository.deleteByTutorAvailabilityAvailabilityId(availabilityId);
//         tutorAvailabilityRepository.delete(availability);
//         log.info("Deleted availability {} and its slots", availabilityId);
//     }

//     public List<SlotInstanceDTO> findMonthlyRecurringSlots(Long tutorId, DayOfWeek weekday, int month, int year) {
//         LocalDate start = LocalDate.of(year, month, 1);
//         LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
//         List<SlotInstance> slots = slotInstanceRepository.findRecurringSlotsInMonth(tutorId, weekday, start, end);
//         return slots.stream().map(this::toDto).toList();
//     }

//     private SlotInstanceDTO toDto(SlotInstance si) {
//         return SlotInstanceDTO.builder()
//                 .slotId(si.getSlotId())
//                 .slotDate(si.getSlotDate())
//                 .status(si.getStatus())
//                 .availabilityId(
//                         si.getTutorAvailability() != null
//                                 ? si.getTutorAvailability().getAvailabilityId()
//                                 : null)
//                 .build();
//     }

//     @Transactional
//     public void generateRecurringSlots(TutorAvailability availability) {
//         if (!availability.getRecurring()) return;
//         LocalDate today = LocalDate.now();
//         LocalDate endDate = today.plusWeeks(8);
//         generateSlotsForDateRange(availability, today, endDate);
//     }

//     @Transactional
//     public void generateSlotsForNextTwoWeeks(TutorAvailability availability) {
//         LocalDate today = LocalDate.now();
//         LocalDate endDate = today.plusWeeks(2);
//         generateSlotsForDateRange(availability, today, endDate);
//     }

//     private void generateSlotsForDateRange(TutorAvailability availability, LocalDate startDate, LocalDate endDate) {
//         DayOfWeek target = availability.getDayOfWeek();
//         LocalDate current = startDate;
//         while (current.getDayOfWeek() != java.time.DayOfWeek.valueOf(target.name())) {
//             current = current.plusDays(1);
//             if (current.isAfter(endDate)) return;
//         }
//         while (!current.isAfter(endDate)) {
//             if (!slotInstanceRepository.existsByTutorAvailabilityAvailabilityIdAndSlotDate(
//                     availability.getAvailabilityId(), current)) {
//                 SlotInstance slotInstance = SlotInstance.builder()
//                         .tutorAvailability(availability)
//                         .slotDate(current)
//                         .status(SlotStatus.AVAILABLE)
//                         .build();
//                 slotInstanceRepository.save(slotInstance);
//             }
//             current = current.plusWeeks(1);
//         }
//     }

//     public List<SlotInstanceDTO> searchAvailableSlots(SlotSearchRequestDTO searchRequest) {
//         List<SlotInstance> slots = new ArrayList<>();
//         if (searchRequest.getTutorId() != null) {
//             if (searchRequest.getSpecificDate() != null) {
//                 slots = slotInstanceRepository.findByTutorIdAndDateAndStatus(
//                         searchRequest.getTutorId(),
//                         searchRequest.getSpecificDate(),
//                         SlotStatus.AVAILABLE
//                 );
//             } else if (searchRequest.getStartDate() != null && searchRequest.getEndDate() != null) {
//                 slots = slotInstanceRepository.findAvailableSlotsByTutorAndDateRange(
//                         searchRequest.getTutorId(),
//                         searchRequest.getStartDate(),
//                         searchRequest.getEndDate()
//                 );
//             }
//         } else if (searchRequest.getSpecificDate() != null) {
//             slots = slotInstanceRepository.findBySlotDate(searchRequest.getSpecificDate())
//                     .stream()
//                     .filter(s -> s.getStatus() == SlotStatus.AVAILABLE)
//                     .toList();
//         }

//         return slots.stream().map(this::convertSlotToDTO).toList();
//     }

//     @Transactional
//     public SlotLockResponseDTO lockSlotsForCheckout(List<Long> slotIds) {
//         if (slotIds == null || slotIds.isEmpty()) {
//             return SlotLockResponseDTO.builder().success(false).failedSlots(List.of()).build();
//         }

//         LocalDateTime now = LocalDateTime.now();
//         LocalDateTime lockUntil = now.plusMinutes(15);

//         List<SlotInstance> slots = slotInstanceRepository.findAllForUpdateByIds(slotIds);
//         Set<Long> found = slots.stream().map(SlotInstance::getSlotId).collect(Collectors.toSet());
//         List<Long> failed = new ArrayList<>();

//         for (SlotInstance si : slots) {
//             boolean lockedActive = si.getStatus() == SlotStatus.LOCKED
//                     && si.getLockedUntil() != null
//                     && si.getLockedUntil().isAfter(now);
//             boolean booked = si.getStatus() == SlotStatus.BOOKED;
//             if (lockedActive || booked) failed.add(si.getSlotId());
//         }

//         for (Long id : slotIds) if (!found.contains(id)) failed.add(id);

//         if (!failed.isEmpty()) {
//             return SlotLockResponseDTO.builder().success(false).failedSlots(failed).build();
//         }

//         for (SlotInstance si : slots) {
//             si.setStatus(SlotStatus.LOCKED);
//             si.setLockedUntil(lockUntil);
//         }
//         return SlotLockResponseDTO.builder().success(true).failedSlots(List.of()).build();
//     }

//     @Transactional
//     public SlotLockResponseDTO releaseSlotsForCheckout(List<Long> slotIds) {
//         if (slotIds == null || slotIds.isEmpty()) {
//             return SlotLockResponseDTO.builder().success(false).failedSlots(List.of()).build();
//         }

//         LocalDateTime now = LocalDateTime.now();
//         List<SlotInstance> slots = slotInstanceRepository.findAllForUpdateByIds(slotIds);
//         Set<Long> found = slots.stream().map(SlotInstance::getSlotId).collect(Collectors.toSet());
//         List<Long> failed = new ArrayList<>();

//         for (SlotInstance si : slots) {
//             boolean lockedActive = si.getStatus() == SlotStatus.LOCKED
//                     && si.getLockedUntil() != null
//                     && si.getLockedUntil().isAfter(now);
//             if (!lockedActive) {
//                 failed.add(si.getSlotId());
//             } else {
//                 si.setStatus(SlotStatus.AVAILABLE);
//                 si.setLockedUntil(null);
//             }
//         }
//         for (Long id : slotIds) if (!found.contains(id)) failed.add(id);

//         if (!failed.isEmpty()) {
//             return SlotLockResponseDTO.builder().success(false).failedSlots(failed).build();
//         }
//         return SlotLockResponseDTO.builder().success(true).failedSlots(List.of()).build();
//     }

//     private void updateAvailabilityFromDTO(TutorAvailability availability, TutorAvailabilityDTO dto) {
//         availability.setDayOfWeek(dto.getDayOfWeek());
//         availability.setStartTime(dto.getStartTime());
//         availability.setEndTime(dto.getEndTime());
//         availability.setRecurring(Boolean.TRUE.equals(dto.getRecurring()));
//     }

//     private TutorAvailabilityDTO convertToDTO(TutorAvailability availability) {
//         return TutorAvailabilityDTO.builder()
//                 .availabilityId(availability.getAvailabilityId())
//                 .tutorId(availability.getTutorProfile().getTutorId())
//                 .tutorName(availability.getTutorProfile().getUser().getFirstName() + " " +
//                         availability.getTutorProfile().getUser().getLastName())
//                 .dayOfWeek(availability.getDayOfWeek())
//                 .startTime(availability.getStartTime())
//                 .endTime(availability.getEndTime())
//                 .recurring(availability.getRecurring())
//                 .generatedSlots(
//                         availability.getSlotInstances() != null
//                                 ? availability.getSlotInstances().size()
//                                 : 0)
//                 .build();
//     }

//     private SlotInstanceDTO convertSlotToDTO(SlotInstance slot) {
//         TutorProfile tp = slot.getTutorAvailability().getTutorProfile();
//         return SlotInstanceDTO.builder()
//                 .slotId(slot.getSlotId())
//                 .availabilityId(slot.getTutorAvailability().getAvailabilityId())
//                 .tutorId(tp.getTutorId())
//                 .tutorName(tp.getUser().getFirstName() + " " + tp.getUser().getLastName())
//                 .slotDate(slot.getSlotDate())
//                 .dayOfWeek(DayOfWeek.valueOf(slot.getSlotDate().getDayOfWeek().name()))
//                 .startTime(slot.getTutorAvailability().getStartTime())
//                 .endTime(slot.getTutorAvailability().getEndTime())
//                 .status(slot.getStatus())
//                 .hourlyRate(tp.getHourlyRate() != null ? tp.getHourlyRate().doubleValue() : null)
//                 .tutorBio(tp.getBio())
//                 .tutorExperience(tp.getExperienceInMonths())
//                 .isRecurring(slot.getTutorAvailability().getRecurring())
//                 .rating(tp.getRating() != null ? tp.getRating().doubleValue() : null)
//                 .build();
//     }
// }