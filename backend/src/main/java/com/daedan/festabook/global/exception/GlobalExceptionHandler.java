package com.daedan.festabook.global.exception;

import static net.logstash.logback.argument.StructuredArguments.kv;

import com.daedan.festabook.global.logging.dto.ExceptionLog;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.query.BadJpqlGrammarException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String INTERNAL_ERROR_MESSAGE = "서버에 오류가 발생하였습니다. 관리자에게 문의해주세요.";

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<ExceptionResponse> handleDatabaseException(DatabaseException databaseException) {
        try {
            return ResponseEntity
                    .status(databaseException.getStatus())
                    .body(databaseException.toResponse());
        } finally {
            ExceptionLog exceptionLog = ExceptionLog.from(
                    databaseException.getStatus().value(),
                    databaseException.getMessage(),
                    databaseException.getClass().getSimpleName(),
                    databaseException.getOriginalExceptionMessage()
            );
            log.warn("", kv("event", exceptionLog));
        }
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ExceptionResponse> handleBusinessException(BusinessException businessException) {
        try {
            return ResponseEntity
                    .status(businessException.getStatus())
                    .body(businessException.toResponse());
        } finally {
            ExceptionLog exceptionLog = ExceptionLog.from(
                    businessException.getStatus().value(),
                    businessException.getMessage(),
                    businessException.getClass().getSimpleName(),
                    ""
            );
            log.info("", kv("event", exceptionLog));
        }
    }

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleAuthenticationCredentialsNotFoundException(
            AuthenticationCredentialsNotFoundException exception
    ) {
        try {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .body(new ExceptionResponse("인증되지 않은 사용자입니다."));
        } finally {
            ExceptionLog exceptionLog = ExceptionLog.from(
                    HttpStatus.UNAUTHORIZED.value(),
                    exception.getMessage(),
                    exception.getClass().getSimpleName(),
                    ""
            );
            log.info("", kv("event", exceptionLog));
        }
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> handleAccessDeniedException(AccessDeniedException exception) {
        try {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN.value())
                    .body(new ExceptionResponse("인가되지 않은 사용자입니다."));
        } finally {
            ExceptionLog exceptionLog = ExceptionLog.from(
                    HttpStatus.FORBIDDEN.value(),
                    exception.getMessage(),
                    exception.getClass().getSimpleName(),
                    ""
            );
            log.info("", kv("event", exceptionLog));
        }
    }

    @ExceptionHandler({
            DataIntegrityViolationException.class,
            DataAccessResourceFailureException.class
    })
    public ResponseEntity<ExceptionResponse> handleWarnDatabaseException(DataAccessException exception) {
        try {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ExceptionResponse("서버에 일시적으로 문제가 발생하였습니다. 잠시후 재시도 해주세요."));
        } finally {
            ExceptionLog exceptionLog = ExceptionLog.from(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    exception.getMessage(),
                    exception.getClass().getSimpleName(),
                    ""
            );
            log.warn("", kv("event", exceptionLog));
        }
    }

    @ExceptionHandler({
            BadSqlGrammarException.class,
            BadJpqlGrammarException.class
    })
    public ResponseEntity<ExceptionResponse> handleErrorDatabaseException(DataAccessException exception) {
        StringWriter stringWriter = new StringWriter();
        try (PrintWriter printWriter = new PrintWriter(stringWriter)) {

            exception.printStackTrace(printWriter);
            return ResponseEntity
                    .internalServerError()
                    .body(new ExceptionResponse(INTERNAL_ERROR_MESSAGE));
        } finally {
            ExceptionLog exceptionLog = ExceptionLog.from(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    exception.getMessage(),
                    exception.getClass().getSimpleName(),
                    stringWriter.toString()
            );
            log.error("", kv("event", exceptionLog));
            try {
                stringWriter.close();
            } catch (IOException e) {
                log.warn("자원할당 해제에 실패하였습니다.");
            }
        }
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionResponse> handleRuntimeException(RuntimeException runtimeException) {
        StringWriter stringWriter = new StringWriter();
        try (PrintWriter printWriter = new PrintWriter(stringWriter)) {

            runtimeException.printStackTrace(printWriter);
            return ResponseEntity
                    .internalServerError()
                    .body(new ExceptionResponse(INTERNAL_ERROR_MESSAGE));
        } finally {
            ExceptionLog exceptionLog = ExceptionLog.from(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    runtimeException.getMessage(),
                    runtimeException.getClass().getSimpleName(),
                    stringWriter.toString()
            );
            log.error("", kv("event", exceptionLog));
            try {
                stringWriter.close();
            } catch (IOException e) {
                log.warn("자원할당 해제에 실패하였습니다.");
            }
        }
    }
}
