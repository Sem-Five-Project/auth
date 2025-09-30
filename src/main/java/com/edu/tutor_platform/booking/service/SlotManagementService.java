//package com.edu.tutor_platform.booking.service;
//
//import com.edu.tutor_platform.booking.dto.SlotInstanceDTO;
//import com.edu.tutor_platform.booking.dto.SlotSearchRequestDTO;
//import com.edu.tutor_platform.booking.dto.TutorAvailabilityDTO;
//import com.edu.tutor_platform.booking.entity.SlotInstance;
//import com.edu.tutor_platform.booking.entity.TutorAvailability;
//import com.edu.tutor_platform.booking.enums.DayOfWeek;
//import com.edu.tutor_platform.booking.enums.SlotStatus;
//import com.edu.tutor_platform.booking.repository.SlotInstanceRepository;
//import com.edu.tutor_platform.booking.repository.TutorAvailabilityRepository;
//import com.edu.tutor_platform.tutorprofile.entity.TutorProfile;
//import com.edu.tutor_platform.tutorprofile.repository.TutorProfileRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.time.temporal.ChronoUnit;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class SlotManagementService {
//
//    private final TutorAvailabilityRepository tutorAvailabilityRepository;
//    private final SlotInstanceRepository slotInstanceRepository;
//    private final TutorProfileRepository tutorProfileRepository;
//    private final BookingValidationService validationService;
//
//    /**
//     * Create or update tutor availability
//     */
//    @Transactional
//    public TutorAvailabilityDTO createOrUpdateAvailability(TutorAvailabilityDTO dto) {
//        log.info("Creating/updating availability for tutor: {}", dto.getTutorId());
//
//        // Validate the availability DTO
//        validationService.validateTutorAvailability(dto);
//
//        // Validate recurring settings if applicable
//        if (dto.getRecurring() != null && dto.getRecurring()) {
//            validationService.validateRecurringSettings(dto);
//        }
//
//        // Validate tutor exists
//        TutorProfile tutorProfile = tutorProfileRepository.findById(dto.getTutorId())
//                .orElseThrow(() -> new RuntimeException("Tutor not found"));
//
//        TutorAvailability availability;
//        if (dto.getAvailabilityId() != null) {
//            // Update existing
//            availability = tutorAvailabilityRepository.findById(dto.getAvailabilityId())
//                    .orElseThrow(() -> new RuntimeException("Availability not found"));
//            updateAvailabilityFromDTO(availability, dto);
//        } else {
//            // Create new
//            availability = TutorAvailability.builder()
//                    .tutorProfile(tutorProfile)
//                    .dayOfWeek(dto.getDayOfWeek())
//                    .startTime(dto.getStartTime())
//                    .endTime(dto.getEndTime())
//                    .recurring(dto.getRecurring())
//                    .build();
//        }
//
//        availability = tutorAvailabilityRepository.save(availability);
//
//        // Generate slot instances if this is recurring or for the next 2 weeks
//        if (availability.getRecurring()) {
//            generateRecurringSlots(availability);
//        } else {
//            generateSlotsForNextTwoWeeks(availability);
//        }
//
//        return convertToDTO(availability);
//    }
//
//    /**
//     * Generate recurring slots for the next 8 weeks
//     */
//    @Transactional
//    public void generateRecurringSlots(TutorAvailability availability) {
//        if (!availability.getRecurring()) {
//            log.warn("Attempting to generate recurring slots for non-recurring availability: {}",
//                    availability.getAvailabilityId());
//            return;
//        }
//
//        LocalDate today = LocalDate.now();
//        LocalDate endDate = today.plusWeeks(8); // Generate 8 weeks ahead
//
//        generateSlotsForDateRange(availability, today, endDate);
//    }
//
//    /**
//     * Generate slots for next 2 weeks for manual (non-recurring) availability
//     */
//    @Transactional
//    public void generateSlotsForNextTwoWeeks(TutorAvailability availability) {
//        LocalDate today = LocalDate.now();
//        LocalDate endDate = today.plusWeeks(2);
//
//        generateSlotsForDateRange(availability, today, endDate);
//    }
//
//    /**
//     * Generate slot instances for a specific date range and availability
//     */
//    private void generateSlotsForDateRange(TutorAvailability availability, LocalDate startDate, LocalDate endDate) {
//        DayOfWeek targetDayOfWeek = availability.getDayOfWeek();
//
//        // Find the first occurrence of the target day of week
//        LocalDate currentDate = startDate;
//        while (currentDate.getDayOfWeek() != java.time.DayOfWeek.valueOf(targetDayOfWeek.name())) {
//            currentDate = currentDate.plusDays(1);
//            if (currentDate.isAfter(endDate)) {
//                return; // No occurrences in the date range
//            }
//        }
//
//        // Generate slots for each occurrence of the day of week
//        while (!currentDate.isAfter(endDate)) {
//            // Check if slot already exists for this date
//            if (!slotInstanceRepository.existsByTutorAvailabilityAvailabilityIdAndSlotDate(
//                    availability.getAvailabilityId(), currentDate)) {
//
//                SlotInstance slotInstance = SlotInstance.builder()
//                        .tutorAvailability(availability)
//                        .slotDate(currentDate)
//                        .status(SlotStatus.AVAILABLE)
//                        .build();
//
//                slotInstanceRepository.save(slotInstance);
//                log.debug("Generated slot for tutor {} on {}",
//                         availability.getTutorProfile().getTutorId(), currentDate);
//            }
//
//            currentDate = currentDate.plusWeeks(1); // Move to next week
//        }
//    }
//
//    /**
//     * Get all availability for a tutor
//     */
//    public List<TutorAvailabilityDTO> getTutorAvailability(Long tutorId) {
//        List<TutorAvailability> availabilities = tutorAvailabilityRepository
//                .findByTutorProfileTutorId(tutorId);
//
//        return availabilities.stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * Search for available slots based on criteria
//     */
//    public List<SlotInstanceDTO> searchAvailableSlots(SlotSearchRequestDTO searchRequest) {
//        List<SlotInstance> slots = new ArrayList<>();
//
//        if (searchRequest.getTutorId() != null) {
//            // Search for specific tutor
//            if (searchRequest.getSpecificDate() != null) {
//                slots = slotInstanceRepository.findByTutorIdAndDateAndStatus(
//                        searchRequest.getTutorId(),
//                        searchRequest.getSpecificDate(),
//                        SlotStatus.AVAILABLE
//                );
//            } else if (searchRequest.getStartDate() != null && searchRequest.getEndDate() != null) {
//                slots = slotInstanceRepository.findAvailableSlotsByTutorAndDateRange(
//                        searchRequest.getTutorId(),
//                        searchRequest.getStartDate(),
//                        searchRequest.getEndDate()
//                );
//            }
//        } else {
//            // Search across all tutors (implement based on needs)
//            if (searchRequest.getSpecificDate() != null) {
//                slots = slotInstanceRepository.findBySlotDate(searchRequest.getSpecificDate())
//                        .stream()
//                        .filter(slot -> slot.getStatus() == SlotStatus.AVAILABLE)
//                        .collect(Collectors.toList());
//            }
//        }
//
//        return slots.stream()
//                .map(this::convertSlotToDTO)
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * Delete tutor availability and all associated slot instances
//     */
//    @Transactional
//    public void deleteAvailability(Long availabilityId) {
//        TutorAvailability availability = tutorAvailabilityRepository.findById(availabilityId)
//                .orElseThrow(() -> new RuntimeException("Availability not found"));
//
//        // Delete all slot instances first
//        slotInstanceRepository.deleteByTutorAvailabilityAvailabilityId(availabilityId);
//
//        // Delete availability
//        tutorAvailabilityRepository.delete(availability);
//
//        log.info("Deleted availability {} and all associated slot instances", availabilityId);
//    }
//
//    /**
//     * Weekly recurring slot generation (called by scheduled task)
//     */
//    @Transactional
//    public void generateWeeklyRecurringSlots() {
//        log.info("Starting weekly recurring slot generation");
//
//        List<TutorAvailability> recurringAvailabilities = tutorAvailabilityRepository.findByRecurringTrue();
//
//        LocalDate nextWeekStart = LocalDate.now().plusWeeks(8); // Generate 8 weeks ahead
//        LocalDate nextWeekEnd = nextWeekStart.plusDays(6);
//
//        for (TutorAvailability availability : recurringAvailabilities) {
//            generateSlotsForDateRange(availability, nextWeekStart, nextWeekEnd);
//        }
//
//        log.info("Completed weekly recurring slot generation for {} availabilities",
//                recurringAvailabilities.size());
//    }
//
//
//    /**
//     * Update availability entity from DTO
//     */
//    private void updateAvailabilityFromDTO(TutorAvailability availability, TutorAvailabilityDTO dto) {
//        availability.setDayOfWeek(dto.getDayOfWeek());
//        availability.setStartTime(dto.getStartTime());
//        availability.setEndTime(dto.getEndTime());
//        availability.setRecurring(dto.getRecurring());
//    }
//
//    /**
//     * Convert entity to DTO
//     */
//    private TutorAvailabilityDTO convertToDTO(TutorAvailability availability) {
//        return TutorAvailabilityDTO.builder()
//                .availabilityId(availability.getAvailabilityId())
//                .tutorId(availability.getTutorProfile().getTutorId())
//                .tutorName(availability.getTutorProfile().getUser().getFirstName() + " " +
//                          availability.getTutorProfile().getUser().getLastName())
//                .dayOfWeek(availability.getDayOfWeek())
//                .startTime(availability.getStartTime())
//                .endTime(availability.getEndTime())
//                .recurring(availability.getRecurring())
//                .generatedSlots(availability.getSlotInstances().size())
//                .build();
//    }
//
//    /**
//     * Convert slot instance to DTO
//     */
//    private SlotInstanceDTO convertSlotToDTO(SlotInstance slot) {
//        TutorProfile tutorProfile = slot.getTutorAvailability().getTutorProfile();
//
//        return SlotInstanceDTO.builder()
//                .slotId(slot.getSlotId())
//                .availabilityId(slot.getTutorAvailability().getAvailabilityId())
//                .tutorId(tutorProfile.getTutorId())
//                .tutorName(tutorProfile.getUser().getFirstName() + " " +
//                          tutorProfile.getUser().getLastName())
//                .slotDate(slot.getSlotDate())
//                .dayOfWeek(DayOfWeek.valueOf(slot.getSlotDate().getDayOfWeek().name()))
//                .startTime(slot.getTutorAvailability().getStartTime())
//                .endTime(slot.getTutorAvailability().getEndTime())
//                .status(slot.getStatus())
//                .hourlyRate(tutorProfile.getHourlyRate() != null ? tutorProfile.getHourlyRate().doubleValue() : null)
//                .tutorBio(tutorProfile.getBio())
//                .tutorExperience(tutorProfile.getExperienceInMonths())
//                .isRecurring(slot.getTutorAvailability().getRecurring())
//                .rating(tutorProfile.getRating() != null ? tutorProfile.getRating().doubleValue() : null)
//                .build();
//    }
//}