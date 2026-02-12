package com.example.logging;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.DispatcherType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;


public class RequestLoggingFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger("REQUEST");

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        HttpServletRequest http = (HttpServletRequest) request;
        HttpServletResponse httpRes = (HttpServletResponse) response;

        if (http.getDispatcherType() != DispatcherType.REQUEST) {
            chain.doFilter(request, response);
            return;
        }

        long start = System.currentTimeMillis();
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = null;

        if (auth != null && auth.isAuthenticated()) {
            Object principal = auth.getPrincipal();
            if (principal instanceof String email && !"anonymousUser".equals(email)) {
                userEmail = email;
            }
        }

        MDC.put("user", userEmail);

        try {
            chain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - start;

            log.info(
                    "method={} path={} status={} durationMs={}",
                    http.getMethod(),
                    http.getRequestURI(),
                    httpRes.getStatus(),
                    duration
            );

            MDC.clear();
        }
    }
}
