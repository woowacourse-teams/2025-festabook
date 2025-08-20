package com.daedan.festabook.global.logging;

import static net.logstash.logback.argument.StructuredArguments.kv;

import com.daedan.festabook.global.logging.dto.ApiCallMessage;
import com.daedan.festabook.global.logging.dto.ApiEndMessage;
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
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

@Slf4j
@Component
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

            ApiCallMessage apiCallMessage = ApiCallMessage.from(httpMethod, queryString, uri);
            log.info("", kv("event", apiCallMessage));

            filterChain.doFilter(request, response);
        } finally {
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            int statusCode = response.getStatus();
            Object requestBody = extractBodyFromCache(request);

            ApiEndMessage apiEndMessage = ApiEndMessage.from(statusCode, requestBody, executionTime);
            log.info("", kv("event", apiEndMessage));

            MDC.clear();
        }
    }

    private boolean isSkipLoggingForPath(String uri) {
        return LOGGING_SKIP_PATH_PREFIX.stream()
                .anyMatch(skipPath -> uri.startsWith(skipPath));
    }

    private Object extractBodyFromCache(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper) {
            ContentCachingRequestWrapper requestWrapper = (ContentCachingRequestWrapper) request;
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
