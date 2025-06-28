package com.library.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Payload for PASSWORD_RESET events
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetEvent {
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String resetToken;
    private LocalDateTime tokenExpiryTime;
    private String resetUrl;
} 