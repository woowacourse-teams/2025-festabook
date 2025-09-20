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
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

@Slf4j
@Component
@Profile("prod | dev")
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class LoggingFilter extends OncePerRequestFilter {

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
    private static final String REQUEST_USER_IP_HEADER_NAME = "X-Forwarded-For";
    private static final String AUTHENTICATION_HEADER = "Authorization";
    private static final String AUTHENTICATION_SCHEME = "Bearer ";

    private final ObjectMapper objectMapper;
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        MDC.put("traceId", UUID.randomUUID().toString());
        String uri = request.getRequestURI();
        if (isSkipLoggingForPath(uri)) {
            filterChain.doFilter(request, response);
            return;
        }

        StopWatch stopWatch = new StopWatch();
        String httpMethod = request.getMethod();
        String ipAddress = request.getHeader(REQUEST_USER_IP_HEADER_NAME);
        String token = extractTokenFromHeader(request);
        String username = extractUsernameFromToken(token);

        stopWatch.start();
        try {
            ApiEventLog apiEvent = ApiEventLog.from(httpMethod, uri, ipAddress, username);
            log.info("", kv("event", apiEvent));

            filterChain.doFilter(request, response);
        } finally {
            stopWatch.stop();
            String queryString = request.getQueryString();
            int statusCode = response.getStatus();
            long executionTime = stopWatch.getTotalTimeMillis();
            Object requestBody = extractBodyFromCache(request);

            ApiLog apiLog = ApiLog.from(
                    httpMethod,
                    queryString,
                    uri,
                    ipAddress,
                    username,
                    statusCode,
                    maskIfContainsMaskingPath(uri, httpMethod, requestBody),
                    executionTime
            );
            log.info("", kv("event", apiLog));
            MDC.clear();
        }
    }

    private boolean isSkipLoggingForPath(String uri) {
        return LOGGING_SKIP_PATH_PREFIX.stream().anyMatch(uri::startsWith);
    }

    private Object maskIfContainsMaskingPath(String uri, String httpMethod, Object requestBody) {
        if (BODY_MASKING_PATH.contains(new MaskingPath(uri, httpMethod))) {
            return "MASKING";
        }

        return requestBody;
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

        if (jwtProvider.isValidToken(token)) {
            return jwtProvider.extractBody(token).getSubject();
        }

        return null;
    }
}
