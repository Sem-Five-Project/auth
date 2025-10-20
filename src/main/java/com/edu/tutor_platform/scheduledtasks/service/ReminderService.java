// package com.edu.tutor_platform.scheduledtasks.service;


// import com.edu.tutor_platform.clazz.entity.Participants;
// import com.edu.tutor_platform.clazz.service.ParticipantsService;
// import com.edu.tutor_platform.notification.service.EmailService;
// import com.edu.tutor_platform.notification.service.FcmService;
// import com.edu.tutor_platform.session.entity.Session;
// import com.edu.tutor_platform.session.service.SessionService;
// import com.edu.tutor_platform.tutorprofile.service.TutorProfileService;
// import com.edu.tutor_platform.user.entity.User;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Service;

// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.stream.Stream;

// @Service
// @Slf4j
// public class ReminderService {

//     @Autowired
//     private FcmService fcmService;
//     private final SessionService sessionService;
//     private final EmailService emailService;
//     private final ParticipantsService participantsService;
//     private final TutorProfileService tutorProfileService;


//     public ReminderService( EmailService emailService, SessionService sessionService , ParticipantsService participantsService, TutorProfileService tutorProfileService) {
//         this.tutorProfileService = tutorProfileService;
//         this.emailService = emailService;
//         this.sessionService = sessionService;
//         this.participantsService = participantsService;
//     }

//     @Scheduled(fixedRate = 6000)
//     public void remind() {
//         LocalDateTime now = LocalDateTime.now();
//         LocalDateTime remindTime = now.plusMinutes(15);
//         List<Session>  sessions = sessionService.getSessionsStartingBetween(now, remindTime);
//         for (Session session : sessions) {
//             if (!session.isNotificationSent()) {
//                 List<Participants> participants = participantsService.findByClassEntity(session.getClassEntity());
//                 List<User> students = Stream.concat(
//                         participants.stream().map(p -> p.getStudent().getUser()),
//                         Stream.of(tutorProfileService.getTutorById(session.getClassEntity().getTutorId()).getUser())
//                 ).toList();
//                 for (User user : students) {
//                     String toEmail = user.getEmail();
//                     String token = user.getFirebaseToken();
//                     if (token == null || token.isEmpty()) {
//                         log.warn("User {} has no Firebase token, skipping notification.", user.getId());
//                         continue;
//                     }
//                     String meetingLink = user.getRole().equals("TUTOR") ? session.getLinkForHost() : session.getLinkForMeeting();

//                     String subject = "Session Reminder";
//                     String body = "Dear " + user.getFirstName() +" "+ user.getLastName() + ",\n\n" +
//                             "This is a reminder for your upcoming session with " + session.getClassEntity().getTutorId() + ".\n" +
//                             "Subject: " + session.getClassEntity().getSubjectId() + "\n" +
//                             "Start Time: " + session.getStartTime() + "\n\n" +
//                             "You can join the session using the following link:\n" +
//                             meetingLink + "\n\n" +
//                             "If you have any questions or need to reschedule, please contact your tutor.\n\n" +
//                             "Best regards,\nTutor Connect";
//                     try {
//                         emailService.sendEmail(toEmail, subject, body);
//                     } catch (Exception e) {
//                         log.error("Failed to send email to {}: {}", toEmail, e.getMessage(), e);
//                     }
//                     try {
//                         fcmService.sendNotification(token, "Session Reminder", "Your session " + session.getSessionName() + " starts soon.");
//                     } catch (Exception e) {
//                         log.error("Failed to send FCM notification to user {}: {}", user.getId(), e.getMessage(), e);
//                     }
//                 }

//                 sessionService.setNotificationSent(session);
//             }
//         }
//     }
// }
