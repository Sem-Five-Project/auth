// package com.edu.tutor_platform.booking.service;

// import com.edu.tutor_platform.booking.entity.TutorAvailability;
// import com.edu.tutor_platform.booking.repository.TutorAvailabilityRepository;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.boot.context.event.ApplicationReadyEvent;
// import org.springframework.context.event.EventListener;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.util.List;

// @Service
// @RequiredArgsConstructor
// @Slf4j
// public class RecurringSlotScheduler {

//     private final SlotManagementService slotManagementService;
//     private final TutorAvailabilityRepository tutorAvailabilityRepository;

//     /**
//      * Generate recurring slots on application startup
//      * This ensures that if the application is restarted, we have slots for the next few weeks
//      */
//     @EventListener(ApplicationReadyEvent.class)
//     @Transactional
//     public void generateSlotsOnStartup() {
//         log.info("Application started - generating recurring slots for the next 8 weeks");
//         generateWeeklyRecurringSlots();
//     }

//     /**
//      * Scheduled task to generate recurring slots every week
//      * Runs every Sunday at 2:00 AM
//      */
//     @Scheduled(cron = "0 0 2 * * SUN", zone = "Asia/Colombo")
//     @Transactional
//     public void generateWeeklyRecurringSlotsScheduled() {
//         log.info("Weekly scheduled task: generating recurring slots");
//         generateWeeklyRecurringSlots();
//     }

//     /**
//      * Generate recurring slots for all tutors
//      * This method is called both on startup and weekly
//      */
//     public void generateWeeklyRecurringSlots() {
//         try {
//             List<TutorAvailability> recurringAvailabilities = tutorAvailabilityRepository.findByRecurringTrue();

//             log.info("Found {} recurring availabilities to process", recurringAvailabilities.size());

//             int totalGenerated = 0;
//             for (TutorAvailability availability : recurringAvailabilities) {
//                 try {
//                     slotManagementService.generateRecurringSlots(availability);
//                     totalGenerated++;
//                     log.debug("Generated slots for availability {} of tutor {}", 
//                              availability.getAvailabilityId(), 
//                              availability.getTutorProfile().getTutorId());
//                 } catch (Exception e) {
//                     log.error("Failed to generate slots for availability {}: {}", 
//                              availability.getAvailabilityId(), e.getMessage());
//                 }
//             }

//             log.info("Successfully generated recurring slots for {}/{} availabilities", 
//                     totalGenerated, recurringAvailabilities.size());

//         } catch (Exception e) {
//             log.error("Error during weekly recurring slot generation: {}", e.getMessage(), e);
//         }
//     }

//     /**
//      * Manual trigger for generating recurring slots (for testing or admin purposes)
//      * Can be called via REST endpoint
//      */
//     @Transactional
//     public void manuallyGenerateRecurringSlots() {
//         log.info("Manual trigger: generating recurring slots");
//         generateWeeklyRecurringSlots();
//     }

//     /**
//      * Generate slots for a specific tutor (useful when tutor updates recurring settings)
//      */
//     @Transactional
//     public void generateRecurringSlotsForTutor(Long tutorId) {
//         log.info("Generating recurring slots for tutor {}", tutorId);

//         List<TutorAvailability> recurringAvailabilities = tutorAvailabilityRepository
//                 .findByTutorProfileTutorIdAndRecurringTrue(tutorId);

//         int generated = 0;
//         for (TutorAvailability availability : recurringAvailabilities) {
//             try {
//                 slotManagementService.generateRecurringSlots(availability);
//                 generated++;
//             } catch (Exception e) {
//                 log.error("Failed to generate slots for availability {}: {}", 
//                          availability.getAvailabilityId(), e.getMessage());
//             }
//         }

//         log.info("Generated recurring slots for {} availabilities of tutor {}", generated, tutorId);
//     }

//     /**
//      * Cleanup old slot instances (older than 1 month and not booked)
//      * Runs every day at 3:00 AM
//      */
//     @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Colombo")
//     @Transactional
//     public void cleanupOldSlots() {
//         log.info("Starting cleanup of old slot instances");

//         try {
//             // This would be implemented to clean up old, unbooked slots
//             // For now, we'll just log the intention
//             log.info("Cleanup of old slots completed (placeholder implementation)");
//         } catch (Exception e) {
//             log.error("Error during slot cleanup: {}", e.getMessage(), e);
//         }
//     }

//     /**
//      * Health check for slot generation
//      * Runs every hour to ensure slots are being generated properly
//      */
//     @Scheduled(fixedRate = 3600000) // Every hour
//     public void slotGenerationHealthCheck() {
//         try {
//             List<TutorAvailability> recurringAvailabilities = tutorAvailabilityRepository.findByRecurringTrue();

//             if (recurringAvailabilities.isEmpty()) {
//                 log.debug("No recurring availabilities found - health check passed");
//                 return;
//             }

//             // Basic health check - ensure we have some slots generated
//             long totalSlots = recurringAvailabilities.stream()
//                     .mapToLong(av -> av.getSlotInstances().size())
//                     .sum();

//             if (totalSlots == 0) {
//                 log.warn("Health check: No slot instances found for {} recurring availabilities", 
//                         recurringAvailabilities.size());
//                 // Could trigger a manual generation here if needed
//                 // generateWeeklyRecurringSlots();
//             } else {
//                 log.debug("Health check passed: {} slot instances for {} recurring availabilities", 
//                          totalSlots, recurringAvailabilities.size());
//             }

//         } catch (Exception e) {
//             log.error("Error during slot generation health check: {}", e.getMessage());
//         }
//     }
// }

