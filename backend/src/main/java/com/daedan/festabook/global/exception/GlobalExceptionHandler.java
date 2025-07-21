package com.daedan.festabook.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String INTERNAL_ERROR_MESSAGE = "서버에 오류가 발생하였습니다. 관리자에게 문의해주세요.";

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ExceptionResponse> handleBusinessException(BusinessException businessException) {
        log.info(businessException.getMessage());
        return ResponseEntity.status(businessException.getStatus())
                .body(businessException.toResponse());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception exception) {
        log.warn(exception.getMessage());
        return ResponseEntity.internalServerError()
                .body(new ExceptionResponse(INTERNAL_ERROR_MESSAGE));
    }
}
