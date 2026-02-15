package com.adg.shared.adapter.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 요청 완료 시 traceId, requestId, clientId, endpoint, latencyMs 등을 로그하는 필터.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final String MDC_CLIENT_ID = "clientId";
    private final AtomicLong counter = new AtomicLong(0);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        long start = System.currentTimeMillis();
        String clientId = (String) request.getAttribute(AuthFilter.ATTR_CLIENT_ID);
        if (clientId != null) {
            MDC.put(MDC_CLIENT_ID, clientId);
        }
        try {
            filterChain.doFilter(request, response);
        } finally {
            long latencyMs = System.currentTimeMillis() - start;
            Object decisionId = request.getAttribute("decisionId");
            Object fallbackUsed = request.getAttribute("fallbackUsed");
            if (decisionId != null) {
                MDC.put("decisionId", decisionId.toString());
            }
            if (fallbackUsed != null) {
                MDC.put("fallbackUsed", String.valueOf(fallbackUsed));
            }
            if (clientId != null) {
                MDC.remove(MDC_CLIENT_ID);
            }
            int status = response.getStatus();
            boolean isError = status >= 400;
            boolean shouldLog = isError || (counter.incrementAndGet() % 100 == 0);
            if (shouldLog) {
                logger().info("request completed: endpoint={}, status={}, latencyMs={}, decisionId={}, fallbackUsed={}",
                        request.getRequestURI(), status, latencyMs, decisionId, fallbackUsed);
            }
            MDC.remove("decisionId");
            MDC.remove("fallbackUsed");
        }
    }

    private org.slf4j.Logger logger() {
        return org.slf4j.LoggerFactory.getLogger(RequestLoggingFilter.class);
    }
}
