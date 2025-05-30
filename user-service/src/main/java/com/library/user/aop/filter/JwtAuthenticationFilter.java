//package com.library.user.aop.filter;
//
//import com.library.user.domain.constant.SecurityConstants;
//import com.library.user.util.security.JwtTokenProvider;
//import io.jsonwebtoken.Claims;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletRequestWrapper;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.core.Ordered;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Objects;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class JwtAuthenticationFilter extends OncePerRequestFilter implements Ordered {
//
//    private final JwtTokenProvider jwtTokenProvider;
//    private final List<String> excludedUrls = List.of(
//            SecurityConstants.AUTH_LOGIN_URL,
//            SecurityConstants.AUTH_LOGOUT_URL,
//            SecurityConstants.AUTH_JWKS_URL,
//            SecurityConstants.AUTH_REGISTER_URL,
//            SecurityConstants.AUTH_REFRESH_URL
//    );
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//        String path = request.getRequestURI();
//        if (isUrlExcluded(path)) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
//        if (Objects.isNull(authorizationHeader) || !authorizationHeader.startsWith(SecurityConstants.TOKEN_PREFIX)) {
//            onError(response, "Invalid Authorization header format", HttpStatus.UNAUTHORIZED);
//            return;
//        }
//        // Lấy token từ header
//        String token = authorizationHeader.substring(7);
//
//        // Xác thực token
//        if (!jwtTokenProvider.validateToken(token)) {
//            onError(response, "Invalid JWT token", HttpStatus.UNAUTHORIZED);
//            return;
//        }
//
//        String username = jwtTokenProvider.getUsernameFromToken(token);
//        Claims claims = jwtTokenProvider.getClaimsFromToken(token);
//        List<String> roles = (List<String>) claims.get("roles");
//
//        // Thêm thông tin người dùng vào headers của request
//        HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(request) {
//            @Override
//            public String getHeader(String name) {
//                if (SecurityConstants.HEADER_USER_ID.equals(name)) {
//                    return username;
//                } else if (SecurityConstants.HEADER_USER_ROLES.equals(name)) {
//                    return String.join(",", roles);
//                }
//                return super.getHeader(name);
//            }
//        };
//
//        log.info("Request headers: {}", requestWrapper.getHeaderNames());
//        filterChain.doFilter(requestWrapper, response);
//    }
//
//    // Xử lý lỗi và gửi phản hồi
//    private void onError(HttpServletResponse response, String error, HttpStatus httpStatus) throws IOException {
//        log.error("Authentication error: {}", error);
//        response.setStatus(httpStatus.value());
//        response.getWriter().write(error);
//    }
//
//    // Kiểm tra URL có nằm trong danh sách loại trừ không
//    private boolean isUrlExcluded(String url) {
//        return excludedUrls.stream().anyMatch(url::startsWith);
//    }
//
//    // Xác định thứ tự ưu tiên của filter
//    @Override
//    public int getOrder() {
//        return -100; // Ưu tiên cao
//    }
//}