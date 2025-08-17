package com.daedan.festabook.global.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter {

    private static final List<String> LOGGING_SKIP_PATH_PREFIX = List.of(
            "/api/swagger-ui",
            "/api/v3/api-docs",
            "/api/api-docs",
            "/api/actuator"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        if (isSkipLoggingForPath(uri)) {
            filterChain.doFilter(request, response);
            return;
        }

        long startTime = System.currentTimeMillis();

        MDC.put("traceId", UUID.randomUUID().toString());
        try {
            String httpMethod = request.getMethod();
            String queryString = request.getQueryString();

            log.info("[API CALL] method={}, uri={}, query={}",
                    httpMethod,
                    uri,
                    queryString
            );

            filterChain.doFilter(request, response);
        } finally {
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            int statusCode = response.getStatus();
            String requestBody = extractBodyFromCache(request);

            log.info("[API END] status={}, duration={}ms, requestBody={}",
                    statusCode,
                    executionTime,
                    requestBody
            );

            MDC.clear();
        }
    }

    private boolean isSkipLoggingForPath(String uri) {
        return LOGGING_SKIP_PATH_PREFIX.stream()
                .anyMatch(skipPath -> uri.startsWith(skipPath));
    }

    private String extractBodyFromCache(HttpServletRequest request) {
        String requestBody = "";
        if (request instanceof ContentCachingRequestWrapper) {
            ContentCachingRequestWrapper requestWrapper = (ContentCachingRequestWrapper) request;
            byte[] content = requestWrapper.getContentAsByteArray();
            if (content.length > 0) {
                requestBody = new String(content, StandardCharsets.UTF_8);
            }
        }
        return requestBody;
    }
}
