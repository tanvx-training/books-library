package com.library.notification.utils;

import com.library.common.event.UserCreatedEvent;
import com.library.notification.model.NotificationTemplate;
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
            context.setVariable("fullname",
                    String.format("%s %s", userCreatedEvent.getFirstName(), userCreatedEvent.getLastName()));
            context.setVariable("username", userCreatedEvent.getUsername());
            context.setVariable("email", userCreatedEvent.getEmail());
            String html = templateEngine.process("user-created", context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(userCreatedEvent.getEmail());
            helper.setSubject(template.getSubject());
            helper.setText(html, true);
            mailSender.send(mimeMessage);
            log.info("Email sent to user {}", userCreatedEvent.getEmail());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
