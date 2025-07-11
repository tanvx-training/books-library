package com.library.user.application.filter;

import com.library.user.infrastructure.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // JWT related constants
    public static final String TOKEN_PREFIX = "Bearer ";

    // API endpoints
    public static final String AUTH_LOGIN_URL = "/api/auth/login";
    public static final String AUTH_REGISTER_URL = "/api/auth/register";
    public static final String AUTH_REFRESH_URL = "/api/auth/refresh";
    public static final String AUTH_LOGOUT_URL = "/api/auth/logout";
    public static final String AUTH_JWKS_URL = "/api/auth/.well-known/jwks.json";

    private final JwtTokenProvider jwtTokenProvider;
    private final List<String> excludedUrls = List.of(
            AUTH_LOGIN_URL,
            AUTH_LOGOUT_URL,
            AUTH_JWKS_URL,
            AUTH_REGISTER_URL,
            AUTH_REFRESH_URL
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        if (isUrlExcluded(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (Objects.isNull(authorizationHeader) || !authorizationHeader.startsWith(TOKEN_PREFIX)) {
            throw new BadCredentialsException("Invalid Authorization header format");
        }
        // Lấy token từ header
        String token = authorizationHeader.substring(7);

        // Xác thực token
        if (!jwtTokenProvider.validateToken(token)) {
            throw new BadCredentialsException("Invalid JWT token");
        }

        String username = jwtTokenProvider.getUsernameFromToken(token);
        Claims claims = jwtTokenProvider.getClaimsFromToken(token);
        List<String> roles = (List<String>) claims.get("roles");
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.trim()));
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(username, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    // Kiểm tra URL có nằm trong danh sách loại trừ không
    private boolean isUrlExcluded(String url) {
        return excludedUrls.stream().anyMatch(url::startsWith);
    }
}