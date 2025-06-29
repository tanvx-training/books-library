package com.library.notification.utils;

import com.library.common.aop.annotation.Loggable;
import com.library.common.enums.LogLevel;
import com.library.common.enums.OperationType;
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

    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.CREATE,
        resourceType = "EmailNotification",
        logArguments = true,
        logReturnValue = false, // Void method
        logExecutionTime = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 5000L, // Email processing can take time
        sanitizeSensitiveData = true, // Email addresses should be sanitized
        messagePrefix = "EMAIL_UTILS_SEND_USER_CREATED",
        customTags = {
            "layer=utils", 
            "email_service=true", 
            "template_processing=true",
            "external_service=true",
            "user_onboarding=true",
            "smtp_operation=true",
            "thymeleaf_template=true"
        }
    )
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
            log.error("Failed to send email to user {}: {}", userCreatedEvent.getEmail(), e.getMessage());
            throw new RuntimeException("Email sending failed", e);
        }
    }
}
