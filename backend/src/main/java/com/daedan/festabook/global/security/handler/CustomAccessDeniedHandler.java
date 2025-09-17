package com.daedan.festabook.global.security.handler;

import static net.logstash.logback.argument.StructuredArguments.kv;

import com.daedan.festabook.global.logging.dto.SecurityMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception)
            throws IOException {
        try {
            AccessDeniedErrorResponse errorResponse = new AccessDeniedErrorResponse(
                    String.valueOf(HttpStatus.FORBIDDEN.value()),
                    "접근 권한이 없습니다."
            );

            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        } finally {
            SecurityMessage securityMessage = new SecurityMessage(
                    "authorization",
                    request.getRequestURI(),
                    request.getMethod(),
                    exception.getMessage()
            );
            log.info("", kv("event", securityMessage));
        }
    }

    private record AccessDeniedErrorResponse(
            String errorCode,
            String errorMessage
    ) {
    }
}
