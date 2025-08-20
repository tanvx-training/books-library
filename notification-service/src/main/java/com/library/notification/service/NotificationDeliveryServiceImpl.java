package com.library.notification.service;

import com.library.notification.service.NotificationDeliveryService;
import com.library.notification.aop.NotificationDeliveryException;
import com.library.notification.aop.NotificationNotFoundException;
import com.library.notification.service.NotificationMapper;
import com.library.notification.repository.NotificationRepository;
import com.library.notification.repository.Notification;
import com.library.notification.repository.NotificationStatus;
import com.library.notification.repository.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationDeliveryServiceImpl implements NotificationDeliveryService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final JavaMailSender mailSender;

    @Value("${notification.email.from:noreply@library.com}")
    private String fromEmail;

    @Value("${notification.email.enabled:true}")
    private boolean emailEnabled;

    @Value("${notification.sms.enabled:false}")
    private boolean smsEnabled;

    @Value("${notification.push.enabled:false}")
    private boolean pushEnabled;

    @Override
    @Retryable(retryFor = {NotificationDeliveryException.class}, backoff = @Backoff(delay = 1000))
    public boolean deliverNotification(Notification notification) {
        log.info("Delivering notification: {} of type: {}", notification.getPublicId(), notification.getType());
        
        try {
            boolean delivered = switch (notification.getType()) {
                case EMAIL -> sendEmailNotification(notification);
                case SMS -> sendSmsNotification(notification);
                case PUSH -> sendPushNotification(notification);
            };
            
            updateDeliveryStatus(notification.getPublicId(), delivered, null);
            return delivered;
            
        } catch (Exception e) {
            log.error("Failed to deliver notification: {}", notification.getPublicId(), e);
            updateDeliveryStatus(notification.getPublicId(), false, e.getMessage());
            throw new NotificationDeliveryException("Failed to deliver notification", e);
        }
    }

    @Override
    public void updateDeliveryStatus(UUID notificationId, boolean delivered, String errorMessage) {
        log.debug("Updating delivery status for notification: {} - delivered: {}", notificationId, delivered);
        
        Notification notification = notificationRepository.findByPublicId(notificationId)
            .orElseThrow(() -> new NotificationNotFoundException("Notification not found: " + notificationId));
        
        if (delivered) {
            notificationMapper.markAsDelivered(notification);
            log.info("Notification delivered successfully: {}", notificationId);
        } else {
            notificationMapper.markAsFailed(notification);
            log.error("Notification delivery failed: {} - Error: {}", notificationId, errorMessage);
        }
        
        notificationRepository.save(notification);
    }

    @Override
    public boolean retryDelivery(UUID notificationId) {
        log.info("Retrying delivery for notification: {}", notificationId);
        
        Notification notification = notificationRepository.findByPublicId(notificationId)
            .orElseThrow(() -> new NotificationNotFoundException("Notification not found: " + notificationId));
        
        if (notification.getStatus() != NotificationStatus.FAILED) {
            log.warn("Cannot retry delivery for notification with status: {}", notification.getStatus());
            return false;
        }
        
        return deliverNotification(notification);
    }

    @Override
    public boolean sendEmailNotification(Notification notification) {
        if (!emailEnabled) {
            log.warn("Email notifications are disabled");
            return false;
        }
        
        log.info("Sending email notification: {}", notification.getPublicId());
        
        try {
            // In a real implementation, you would fetch user email from user service
            String userEmail = getUserEmail(notification.getUserPublicId());
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(userEmail);
            message.setSubject(notification.getTitle());
            message.setText(notification.getContent());
            
            mailSender.send(message);
            
            log.info("Email sent successfully to: {} for notification: {}", userEmail, notification.getPublicId());
            return true;
            
        } catch (Exception e) {
            log.error("Failed to send email for notification: {}", notification.getPublicId(), e);
            return false;
        }
    }

    @Override
    public boolean sendSmsNotification(Notification notification) {
        if (!smsEnabled) {
            log.warn("SMS notifications are disabled");
            return false;
        }
        
        log.info("Sending SMS notification: {}", notification.getPublicId());
        
        try {
            // In a real implementation, you would integrate with SMS service (Twilio, AWS SNS, etc.)
            String userPhone = getUserPhone(notification.getUserPublicId());
            
            // Mock SMS sending
            log.info("SMS would be sent to: {} with content: {}", userPhone, notification.getContent());
            
            // Simulate SMS sending delay
            Thread.sleep(100);
            
            log.info("SMS sent successfully to: {} for notification: {}", userPhone, notification.getPublicId());
            return true;
            
        } catch (Exception e) {
            log.error("Failed to send SMS for notification: {}", notification.getPublicId(), e);
            return false;
        }
    }

    @Override
    public boolean sendPushNotification(Notification notification) {
        if (!pushEnabled) {
            log.warn("Push notifications are disabled");
            return false;
        }
        
        log.info("Sending push notification: {}", notification.getPublicId());
        
        try {
            // In a real implementation, you would integrate with push service (Firebase, AWS SNS, etc.)
            String deviceToken = getUserDeviceToken(notification.getUserPublicId());
            
            // Mock push notification sending
            log.info("Push notification would be sent to device: {} with title: {} and content: {}", 
                deviceToken, notification.getTitle(), notification.getContent());
            
            // Simulate push sending delay
            Thread.sleep(50);
            
            log.info("Push notification sent successfully to device: {} for notification: {}", 
                deviceToken, notification.getPublicId());
            return true;
            
        } catch (Exception e) {
            log.error("Failed to send push notification for notification: {}", notification.getPublicId(), e);
            return false;
        }
    }

    @Override
    public boolean isTypeSupported(NotificationType type) {
        return switch (type) {
            case EMAIL -> emailEnabled;
            case SMS -> smsEnabled;
            case PUSH -> pushEnabled;
        };
    }

    private String getUserEmail(UUID userPublicId) {
        // Mock implementation - in reality, this would call member-service
        return "user-" + userPublicId.toString().substring(0, 8) + "@library.com";
    }

    private String getUserPhone(UUID userPublicId) {
        // Mock implementation - in reality, this would call member-service
        return "+084" + userPublicId.toString().substring(0, 7).replaceAll("[^0-9]", "");
    }

    private String getUserDeviceToken(UUID userPublicId) {
        // Mock implementation - in reality, this would call member-service
        return "device-token-" + userPublicId.toString().substring(0, 12);
    }
}