package com.edu.tutor_platform.notification.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class EmailService {

    private final String defaultFromEmail;
    private final String sendGridApiKey;

//    public EmailService() {
//        Dotenv dotenv = Dotenv.load();
//        this.defaultFromEmail = dotenv.get("DEFAULT_FROM_EMAIL");
//    }

    public EmailService(
            @Value("${default.from.email}") String defaultFromEmail,
            @Value("${sendgrid.api.key}") String sendGridApiKey
    ) {
        this.defaultFromEmail = defaultFromEmail;
        this.sendGridApiKey = sendGridApiKey;
    }

    public void sendEmail(String toEmail, String subject, String body) throws IOException {
        sendEmail(defaultFromEmail, toEmail, subject, body);
    }

    public void sendEmail(String fromEmail, String toEmail, String subject, String body) throws IOException {
//        Dotenv dotenv = Dotenv.load();
//        String apiKey = dotenv.get("SENDGRID_API_KEY");
//        if (apiKey == null || apiKey.isEmpty()) {
//            throw new IllegalStateException("SENDGRID_API_KEY environment variable is not set.");
//        }
        if (sendGridApiKey == null || sendGridApiKey.isEmpty()) {
            throw new IllegalStateException("sendgrid.api.key property is not set.");
        }



        Email from = new Email(fromEmail);
        Email to = new Email(toEmail);
        Content content = new Content("text/plain", body);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println("Status Code: " + response.getStatusCode());
            System.out.println("Body: " + response.getBody());
            System.out.println("Headers: " + response.getHeaders());

        } catch (IOException ex) {
            log.error("Failed to send email: {}", ex.getMessage(), ex);
            throw ex;
        }
    }
}

