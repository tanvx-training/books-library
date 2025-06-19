package com.library.notification.util;

import com.library.common.dto.UserCreatedEvent;
import com.library.notification.domain.model.NotificationTemplate;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailUtils {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sendUserCreatedMail(UserCreatedEvent userCreatedEvent, NotificationTemplate template) {
        try {
            Context context = new Context();
            
            // Set all variables that are used in the template
            context.setVariable("firstName", userCreatedEvent.getFirstName());
            context.setVariable("lastName", userCreatedEvent.getLastName());
            context.setVariable("userName", userCreatedEvent.getUsername());
            context.setVariable("email", userCreatedEvent.getEmail());
            context.setVariable("registrationDate", java.time.LocalDate.now().toString());
            context.setVariable("subject", template.getSubject());
            
            // Process the template
            String html = templateEngine.process("user-created", context);

            // Create and send the email
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(userCreatedEvent.getEmail());
            helper.setSubject(template.getSubject());
            helper.setText(html, true);
            
            // Add a from address
            helper.setFrom("library-system@example.com", "Library System");
            
            mailSender.send(mimeMessage);
            log.info("Welcome email sent successfully to user {}", userCreatedEvent.getEmail());
        } catch (Exception e) {
            log.error("Failed to send welcome email: {}", e.getMessage(), e);
        }
    }
}
