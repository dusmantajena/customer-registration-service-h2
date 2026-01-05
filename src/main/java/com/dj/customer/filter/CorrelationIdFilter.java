package com.dj.customer.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    private static final String MDC_CORRELATION_ID_KEY = "correlationId";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Check if request already has correlation ID
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);

        // 2. Generate if missing
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
            log.debug("Generated new Correlation ID: {}", correlationId);
        }

        // 3. Put into MDC so logback can use it
        MDC.put(MDC_CORRELATION_ID_KEY, correlationId);

        // 4. Add to response header
        response.addHeader(CORRELATION_ID_HEADER, correlationId);

        try {
            log.info("[{}] Request Started: {} {}", correlationId, request.getMethod(), request.getRequestURI());
            filterChain.doFilter(request, response);
        } finally {
            log.info("[{}] Request Completed: {} {}", correlationId, request.getMethod(), request.getRequestURI());
            MDC.remove(MDC_CORRELATION_ID_KEY);
        }
    }
}
