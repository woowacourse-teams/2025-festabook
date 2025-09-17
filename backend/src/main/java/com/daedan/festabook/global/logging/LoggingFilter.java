package com.daedan.festabook.global.logging;

import static net.logstash.logback.argument.StructuredArguments.kv;

import com.daedan.festabook.global.logging.dto.ApiEventLog;
import com.daedan.festabook.global.logging.dto.ApiLog;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

@Slf4j
@Component
@Profile("prod | dev")
@RequiredArgsConstructor
public class LoggingFilter extends OncePerRequestFilter {

    private static final List<String> LOGGING_SKIP_PATH_PREFIX = List.of(
            "/api/swagger-ui",
            "/api/v3/api-docs",
            "/api/api-docs",
            "/api/actuator"
    );

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();
        String uri = request.getRequestURI();
        String httpMethod = request.getMethod();
        String queryString = request.getQueryString();

        if (isSkipLoggingForPath(uri)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            MDC.put("traceId", UUID.randomUUID().toString());

            ApiEventLog apiEvent = ApiEventLog.from(httpMethod, uri);
            log.info("", kv("event", apiEvent));

            filterChain.doFilter(request, response);
        } finally {
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            int statusCode = response.getStatus();
            Object requestBody = extractBodyFromCache(request);

            ApiLog apiLog = ApiLog.from(
                    httpMethod,
                    queryString,
                    uri,
                    statusCode,
                    requestBody,
                    executionTime);
            log.info("", kv("event", apiLog));

            MDC.clear();
        }
    }

    private boolean isSkipLoggingForPath(String uri) {
        return LOGGING_SKIP_PATH_PREFIX.stream().anyMatch(uri::startsWith);
    }

    private Object extractBodyFromCache(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper requestWrapper) {
            byte[] content = requestWrapper.getContentAsByteArray();

            if (content.length > 0) {
                String requestBody = new String(content, StandardCharsets.UTF_8);
                try {
                    return objectMapper.readTree(requestBody);
                } catch (IOException e) {
                    return requestBody;
                }
            }
        }

        return null;
    }
}
