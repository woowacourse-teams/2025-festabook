package com.daedan.festabook.global.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

@Slf4j
@Component
@Profile("!prod & !dev")
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class LocalLoggingFilter extends OncePerRequestFilter {

    private static final List<String> LOGGING_SKIP_PATH_PREFIX = List.of(
            "/api/swagger-ui",
            "/api/v3/api-docs",
            "/api/api-docs",
            "/api/actuator"
    );
    private static final Set<MaskingPath> BODY_MASKING_PATH = Set.of(
            new MaskingPath("/api/councils", "POST"),
            new MaskingPath("/api/councils/login", "POST"),
            new MaskingPath("/api/councils/password", "PATCH")
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        StopWatch stopWatch = new StopWatch();
        String uri = request.getRequestURI();
        String httpMethod = request.getMethod();
        String queryString = request.getQueryString();

        if (isSkipLoggingForPath(uri)) {
            filterChain.doFilter(request, response);
            return;
        }

        stopWatch.start();
        try {
            log.info("[API Call] method={} queryString={} uri={}", httpMethod, queryString, uri);
            filterChain.doFilter(request, response);
        } finally {
            stopWatch.stop();
            long executionTime = stopWatch.getTotalTimeMillis();
            int statusCode = response.getStatus();
            String requestBody = extractBodyFromCache(request);

            log.info(
                    "[API End] statusCode={} requestBody={} executionTime={}ms",
                    statusCode,
                    maskingIfContainsMaskingPath(uri, httpMethod, requestBody),
                    executionTime
            );
        }
    }

    private boolean isSkipLoggingForPath(String uri) {
        return LOGGING_SKIP_PATH_PREFIX.stream().anyMatch(uri::startsWith);
    }

    private Object maskingIfContainsMaskingPath(String uri, String httpMethod, Object requestBody) {
        if (BODY_MASKING_PATH.contains(new MaskingPath(uri, httpMethod))) {
            return "MASKING";
        }

        return requestBody;
    }

    private String extractBodyFromCache(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper requestWrapper) {
            byte[] content = requestWrapper.getContentAsByteArray();

            if (content.length > 0) {
                return new String(content, StandardCharsets.UTF_8);
            }
        }

        return null;
    }
}
