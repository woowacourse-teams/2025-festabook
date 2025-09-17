package com.daedan.festabook.global.logging;

import static net.logstash.logback.argument.StructuredArguments.kv;

import com.daedan.festabook.global.logging.dto.ApiEventLog;
import com.daedan.festabook.global.logging.dto.ApiLog;
import com.daedan.festabook.global.security.util.JwtProvider;
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
import org.springframework.util.StringUtils;
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
    private static final String REQUEST_USER_IP_HEADER_NAME = "X-Forwarded-For";
    private static final String AUTHENTICATION_HEADER = "Authorization";
    private static final String AUTHENTICATION_SCHEME = "Bearer ";

    private final ObjectMapper objectMapper;
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();
        String uri = request.getRequestURI();
        String httpMethod = request.getMethod();
        String ipAddress = request.getHeader(REQUEST_USER_IP_HEADER_NAME);
        String token = extractTokenFromHeader(request);
        String username = extractUsernameFromToken(token);

        if (isSkipLoggingForPath(uri)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            MDC.put("traceId", UUID.randomUUID().toString());

            ApiEventLog apiEvent = ApiEventLog.from(httpMethod, uri, ipAddress, username);
            log.info("", kv("event", apiEvent));

            filterChain.doFilter(request, response);
        } finally {

            String queryString = request.getQueryString();
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            int statusCode = response.getStatus();
            Object requestBody = extractBodyFromCache(request);

            ApiLog apiLog = ApiLog.from(
                    httpMethod,
                    queryString,
                    uri,
                    ipAddress,
                    username,
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

    private String extractTokenFromHeader(HttpServletRequest request) {
        String token = request.getHeader(AUTHENTICATION_HEADER);
        if (StringUtils.hasText(token) && token.startsWith(AUTHENTICATION_SCHEME)) {
            return token.substring(AUTHENTICATION_SCHEME.length());
        }
        return null;
    }

    private String extractUsernameFromToken(String token) {
        if (token == null) {
            return null;
        }

        return jwtProvider.extractBody(token).getSubject();
    }
}
