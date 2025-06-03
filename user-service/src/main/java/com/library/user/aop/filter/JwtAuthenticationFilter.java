package com.library.user.aop.filter;

import com.library.user.domain.constant.SecurityConstants;
import com.library.user.util.security.JwtTokenProvider;
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

    private final JwtTokenProvider jwtTokenProvider;
    private final List<String> excludedUrls = List.of(
            SecurityConstants.AUTH_LOGIN_URL,
            SecurityConstants.AUTH_LOGOUT_URL,
            SecurityConstants.AUTH_JWKS_URL,
            SecurityConstants.AUTH_REGISTER_URL,
            SecurityConstants.AUTH_REFRESH_URL
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
        if (Objects.isNull(authorizationHeader) || !authorizationHeader.startsWith(SecurityConstants.TOKEN_PREFIX)) {
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