package com.daedan.festabook.global.logging;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

@Slf4j
@Component
public class LoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        long startTime = System.currentTimeMillis();

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(httpServletRequest);

        MDC.put("traceId", UUID.randomUUID().toString());
        try {
            String httpMethod = httpServletRequest.getMethod();
            String uri = httpServletRequest.getRequestURI();
            String queryString = httpServletRequest.getQueryString();

            log.info("[API CALL] method={}, uri={}, query={}",
                    httpMethod,
                    uri,
                    queryString
            );

            chain.doFilter(requestWrapper, response);
        } finally {
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            int statusCode = httpServletResponse.getStatus();
            String requestBody = new String(requestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);

            log.info("[API END] status={}, duration={}ms, requestBody={}",
                    statusCode,
                    executionTime,
                    requestBody
            );

            MDC.clear();
        }
    }
}
