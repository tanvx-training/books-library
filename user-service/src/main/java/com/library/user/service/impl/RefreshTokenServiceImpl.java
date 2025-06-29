package com.library.user.service.impl;

import com.library.common.aop.annotation.Loggable;
import com.library.common.aop.exception.BadRequestException;
import com.library.common.aop.exception.ResourceNotFoundException;
import com.library.common.enums.LogLevel;
import com.library.common.enums.OperationType;
import com.library.user.model.RefreshToken;
import com.library.user.model.User;
import com.library.user.service.RefreshTokenService;
import com.library.user.repository.RefreshTokenRepository;
import com.library.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Value("${app.jwt.expiration.refresh}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;

    private final UserRepository userRepository;

        @Override
    @Transactional
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.CREATE,
        resourceType = "RefreshToken",
        logArguments = true,
        logReturnValue = false, // Don't log tokens for security
        logExecutionTime = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 2000L,
        sanitizeSensitiveData = true,
        messagePrefix = "REFRESH_TOKEN_SERVICE_CREATE",
        customTags = {
            "layer=service", 
            "transaction=write", 
            "security_operation=true",
            "token_management=true",
            "cleanup_existing=true",
            "auth_flow=true"
        }
    )
    public RefreshToken createRefreshToken(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username: " + username));

        // Remove existing refresh token if exists
        List<RefreshToken> existingTokens = refreshTokenRepository.findByUser(user);
        if (!existingTokens.isEmpty()) {
            refreshTokenRepository.deleteAll(existingTokens);
            refreshTokenRepository.flush();
        }

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));

        return refreshTokenRepository.save(refreshToken);
    }
    
    @Override
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.AUTHENTICATION,
        resourceType = "RefreshToken",
        logArguments = false, // Don't log token for security
        logReturnValue = false, // Don't log tokens for security
        logExecutionTime = true,
        performanceThresholdMs = 500L,
        messagePrefix = "REFRESH_TOKEN_SERVICE_VERIFY",
        customTags = {
            "layer=service", 
            "security_operation=true",
            "token_validation=true",
            "expiry_check=true",
            "auth_flow=true"
        }
    )
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new BadRequestException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }
    
    @Override
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.READ,
        resourceType = "RefreshToken",
        logArguments = false, // Don't log token for security
        logReturnValue = false, // Don't log tokens for security
        logExecutionTime = true,
        performanceThresholdMs = 300L,
        messagePrefix = "REFRESH_TOKEN_SERVICE_FIND",
        customTags = {
            "layer=service", 
            "security_operation=true",
            "token_lookup=true",
            "auth_flow=true"
        }
    )
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByTokenAndDeleteFlg(token, false);
    }
    
    @Override
    @Transactional
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.DELETE,
        resourceType = "RefreshToken",
        logArguments = true,
        logReturnValue = false,
        logExecutionTime = true,
        performanceThresholdMs = 1000L,
        messagePrefix = "REFRESH_TOKEN_SERVICE_DELETE",
        customTags = {
            "layer=service", 
            "transaction=write", 
            "security_operation=true",
            "token_cleanup=true",
            "logout_operation=true",
            "user_validation=true"
        }
    )
    public void deleteByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(User.class.getName(), "id", userId));
        refreshTokenRepository.deleteByUser(user);
    }
} 