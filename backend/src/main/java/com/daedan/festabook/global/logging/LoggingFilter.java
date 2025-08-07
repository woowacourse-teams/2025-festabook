package com.daedan.festabook.global.logging;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;

            MDC.put("requestId", UUID.randomUUID().toString());
            log.info("[API CALL] method={}, uri={}, query={}, contentType={}, ip={}",
                    httpServletRequest.getMethod(),
                    httpServletRequest.getRequestURI(),
                    httpServletRequest.getQueryString(),
                    httpServletRequest.getContentType(),
                    httpServletRequest.getRemoteAddr()
            );

            chain.doFilter(request, response);
        } finally {
            HttpServletResponse  httpServletResponse = (HttpServletResponse) response;
            log.info("[API END] status={}", httpServletResponse.getStatus());
            MDC.clear();
        }
    }
}
