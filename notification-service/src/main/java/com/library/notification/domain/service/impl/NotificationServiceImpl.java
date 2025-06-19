package com.library.notification.domain.service.impl;

import com.library.common.constants.EventType;
import com.library.notification.domain.model.Notification;
import com.library.notification.domain.model.NotificationTemplate;
import com.library.notification.domain.service.NotificationService;
import com.library.notification.infrastructure.repository.NotificationRepository;
import com.library.notification.infrastructure.repository.NotificationTemplateRepository;
import com.library.notification.presentation.dto.UserCreatedMessage;
import com.library.notification.util.EmailUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationTemplateRepository templateRepository;
    private final EmailUtils emailUtils;

    @Override
    @Transactional
    public void handleUserCreated(UserCreatedMessage message) {
        log.info("Processing user_created event for user: {}", message.getPayload().getEmail());
        
        try {
            // Find the user_created template
            List<NotificationTemplate> templates = templateRepository.findByName(EventType.USER_CREATED);
            
            if (templates.isEmpty()) {
                log.error("No template found for user_created notification");
                return;
            }
            
            NotificationTemplate template = templates.get(0);
            
            // Send email
            emailUtils.sendUserCreatedMail(message.getPayload(), template);
            
            // Save notification record
            Notification notification = new Notification();
            notification.setUserId(message.getPayload().getUserId());
            notification.setType("EMAIL");
            notification.setStatus("SENT");
            notification.setTitle(template.getSubject());
            notification.setContent("Welcome email sent to " + message.getPayload().getEmail());
            notification.setSentAt(LocalDateTime.now());
            notification.setTemplate(template);
            
            notificationRepository.save(notification);
            
            log.info("User creation notification processed successfully for user: {}", message.getPayload().getEmail());
        } catch (Exception e) {
            log.error("Error processing user_created notification: {}", e.getMessage(), e);
        }
    }
}
