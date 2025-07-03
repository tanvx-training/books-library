package com.library.apigateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String requestId = UUID.randomUUID().toString();

        // Thêm requestId vào MDC
        MDC.put("requestId", requestId);

        // Lấy IP của client
        String clientIp = request.getRemoteAddress() != null
                ? request.getRemoteAddress().getHostString()
                : "unknown";
        MDC.put("clientIp", clientIp);

        // Log thông tin request
        log.info("Incoming request: method={}, uri={}, headers={}",
                request.getMethod(), request.getURI(), request.getHeaders());

        // Thêm requestId vào header
        ServerHttpRequest modifiedRequest = request.mutate()
                .header("X-Request-ID", requestId)
                .build();

        ServerWebExchange modifiedExchange = exchange.mutate()
                .request(modifiedRequest)
                .build();

        return chain.filter(modifiedExchange)
                .doFinally(signalType -> {
                    log.info("Request completed: method={}, uri={}, status={}",
                            request.getMethod(), request.getURI(),
                            exchange.getResponse().getStatusCode());
                    MDC.clear();
                });
    }

    @Override
    public int getOrder() {
        return -2;
    }
}