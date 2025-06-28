package com.library.user.service.impl;

import com.library.common.dto.UserCreatedEvent;
import com.library.common.constants.EventType;
import com.library.common.aop.exception.ResourceExistedException;
import com.library.common.aop.exception.ResourceNotFoundException;
import com.library.common.model.KafkaEvent;
import com.library.user.service.KafkaProducerService;
import com.library.user.model.RefreshToken;
import com.library.user.model.Role;
import com.library.user.model.User;
import com.library.user.service.AuthService;
import com.library.user.utils.enums.UserRole;
import com.library.user.service.RefreshTokenService;
import com.library.user.repository.RoleRepository;
import com.library.user.repository.UserRepository;
import com.library.user.dto.request.LoginRequestDTO;
import com.library.user.dto.request.RegisterRequestDTO;
import com.library.user.dto.response.LoginResponseDTO;
import com.library.user.dto.response.RegisterResponseDTO;
import com.library.user.utils.mapper.UserMapper;
import com.library.user.utils.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Value("${spring.application.name}")
    private String source;

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final KafkaProducerService kafkaProducerService;

    @Override
    @Transactional
    public RegisterResponseDTO registerUser(RegisterRequestDTO registerRequestDTO) {
        if (userRepository.existsByUsername(registerRequestDTO.getUsername())) {
            throw new ResourceExistedException("User", "username", registerRequestDTO.getUsername());
        }
        if (userRepository.existsByEmail(registerRequestDTO.getEmail())) {
            throw new ResourceExistedException("User", "email", registerRequestDTO.getEmail());
        }
        User user = userMapper.toEntity(registerRequestDTO);
        user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        Role readerRole = roleRepository.findByName(UserRole.READER.name())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", UserRole.READER.name()));
        user.setRoles(List.of(readerRole));
        userRepository.save(user);
        // Trigger user-created event
        UserCreatedEvent userCreatedEvent = UserCreatedEvent.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
        KafkaEvent<UserCreatedEvent> event = KafkaEvent.create(
                EventType.USER_CREATED, source, userCreatedEvent);
        kafkaProducerService.sendEvent(
                EventType.USER_CREATED, String.valueOf(user.getId()), event);

        return RegisterResponseDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .roles(user.getRoles().stream().map(Role::getName).toList())
                .build();
    }

    @Override
    @Transactional
    public LoginResponseDTO loginUser(LoginRequestDTO loginRequestDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDTO.getUsername(),
                            loginRequestDTO.getPassword()
                    )
            );

            // Generate JWT access token
            Objects.requireNonNull(authentication);
            String accessToken = jwtTokenProvider.generateAccessToken(authentication);

            // Create refresh token and save to database
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(
                    loginRequestDTO.getUsername());

            List<String> roles = authentication.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
            return LoginResponseDTO.builder()
                    .username(authentication.getName())
                    .roles(roles)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getToken())
                    .build();
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }
}
