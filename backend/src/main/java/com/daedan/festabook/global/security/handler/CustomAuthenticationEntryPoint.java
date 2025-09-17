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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException {
        try {
            AuthenticationErrorResponse errorResponse = new AuthenticationErrorResponse(
                    String.valueOf(HttpStatus.UNAUTHORIZED.value()),
                    "인증이 필요합니다."
            );

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        } finally {
            SecurityMessage securityMessage = new SecurityMessage(
                    "authentication",
                    request.getRequestURI(),
                    request.getMethod(),
                    exception.getMessage()
            );
            log.info("", kv("event", securityMessage));
        }
    }

    private record AuthenticationErrorResponse(
            String errorCode,
            String errorMessage
    ) {
    }
}
