package com.daedan.festabook.global.exception;

import static net.logstash.logback.argument.StructuredArguments.kv;

import com.daedan.festabook.global.logging.dto.ExceptionLog;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String INTERNAL_ERROR_MESSAGE = "서버에 오류가 발생하였습니다. 관리자에게 문의해주세요.";

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ExceptionResponse> handleBusinessException(BusinessException businessException) {
        try {
            return ResponseEntity
                    .status(businessException.getStatus())
                    .body(businessException.toResponse());
        } finally {
            ExceptionLog exceptionLog = new ExceptionLog(
                    "exception",
                    businessException.getStatus().value(),
                    businessException.getMessage(),
                    ""
            );
            log.info("", kv("event", exceptionLog));
        }
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionResponse> handleRuntimeException(RuntimeException runtimeException) {
        StringWriter stringWriter = new StringWriter();
        try (PrintWriter printWriter = new PrintWriter(stringWriter);) {

            runtimeException.printStackTrace(printWriter);
            return ResponseEntity
                    .internalServerError()
                    .body(new ExceptionResponse(INTERNAL_ERROR_MESSAGE));
        } finally {
            ExceptionLog exceptionLog = new ExceptionLog(
                    "exception",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    runtimeException.getMessage(),
                    stringWriter.toString()
            );
            log.warn("", kv("event", exceptionLog));
            try {
                stringWriter.close();
            } catch (IOException e) {
                log.warn("잘못된 스택트레이스 입니다.");
            }
        }
    }
}
