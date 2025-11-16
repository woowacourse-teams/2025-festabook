package com.daedan.festabook.global.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.daedan.festabook.global.config.TestSecurityConfig;
import io.restassured.RestAssured;
import java.sql.SQLException;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.query.BadJpqlGrammarException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Import(TestSecurityConfig.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class GlobalExceptionHandlerTest {

    private static final String MESSAGE_PARAM_NAME = "message";
    private static final String STATUS_CODE_PARAM_NAME = "status-code";

    @Autowired
    private ExceptionController exceptionController;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    class handleDatabaseException {

        private ListAppender<ILoggingEvent> listAppender;

        @BeforeEach
        void setUpLogger() {
            Logger logger = (Logger) LoggerFactory.getLogger(GlobalExceptionHandler.class);
            listAppender = new ListAppender<>();

            listAppender.start();
            logger.addAppender(listAppender);
        }

        @AfterEach
        void tearDownLogger() {
            Logger logger = (Logger) LoggerFactory.getLogger(GlobalExceptionHandler.class);
            logger.detachAppender(listAppender);
            listAppender.stop();
        }

        @Test
        void 성공_데이터베이스_예외_정상_처리() {
            // given
            String expectedMessage = "데이터 베이스 에러가 발생했습니다.";
            exceptionController.injectException(new TestDatabaseException(expectedMessage, HttpStatus.CONFLICT));
            int expectedStatusCode = HttpStatus.CONFLICT.value();
            int expectedFieldSize = 1;

            // when & then
            RestAssured
                    .given()
                    .when()
                    .get("/test/exception-controller/inject-exception")
                    .then()
                    .log()
                    .all()
                    .statusCode(expectedStatusCode)
                    .body("size()", equalTo(expectedFieldSize))
                    .body("message", equalTo(expectedMessage));
        }

        @Test
        void 성공_데이터베이스_예외가_4xx이면_warn로그() {
            // given
            HttpStatus statusCode = HttpStatus.BAD_REQUEST;
            exceptionController.injectException(new TestDatabaseException("데이터 베이스 에러가 발생했습니다.", statusCode));

            // when & then
            RestAssured
                    .given()
                    .when()
                    .get("/test/exception-controller/inject-exception")
                    .then()
                    .log()
                    .all()
                    .statusCode(statusCode.value());

            assertThat(listAppender.list).hasSize(1);
            assertThat(listAppender.list.getFirst().getLevel()).isEqualTo(Level.WARN);
        }

        @Test
        void 성공_데이터베이스_예외가_5xx이면_error로그() {
            // given
            HttpStatus statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
            exceptionController.injectException(new TestDatabaseException("데이터 베이스 에러가 발생했습니다.", statusCode));

            // when & then
            RestAssured
                    .given()
                    .when()
                    .get("/test/exception-controller/inject-exception")
                    .then()
                    .log()
                    .all()
                    .statusCode(statusCode.value());

            assertThat(listAppender.list).hasSize(1);
            assertThat(listAppender.list.getFirst().getLevel()).isEqualTo(Level.ERROR);
        }
    }

    @Nested
    class handleBusinessException {

        @Test
        void 성공_커스텀_예외_정상_처리() {
            // given
            String expectedMessage = "예외가 발생했습니다.";
            int expectedStatusCode = HttpStatus.I_AM_A_TEAPOT.value();
            int expectedFieldSize = 1;

            // when & then
            RestAssured
                    .given()
                    .queryParam(MESSAGE_PARAM_NAME, expectedMessage)
                    .queryParam(STATUS_CODE_PARAM_NAME, expectedStatusCode)
                    .when()
                    .get("/test/exception-controller/custom-exception")
                    .then()
                    .log()
                    .all()
                    .statusCode(expectedStatusCode)
                    .body("size()", equalTo(expectedFieldSize))
                    .body("message", equalTo(expectedMessage));
        }
    }

    @Nested
    class handleAuthenticationCredentialsNotFoundException {

        @Test
        void 인증_실패_예외_발생시_401_응답() {
            // given
            exceptionController.injectException(new AuthenticationCredentialsNotFoundException(""));
            String expectedMessage = "인증되지 않은 사용자입니다.";
            int expectedStatusCode = HttpStatus.UNAUTHORIZED.value();
            int expectedFieldSize = 1;

            // when & then
            RestAssured
                    .given()
                    .when()
                    .get("/test/exception-controller/inject-exception")
                    .then()
                    .log()
                    .all()
                    .statusCode(expectedStatusCode)
                    .body("size()", equalTo(expectedFieldSize))
                    .body("message", containsString(expectedMessage));
        }
    }

    @Nested
    class handleAccessDeniedException {

        @Test
        void 인가_실패_예외_발생시_403_응답() {
            // given
            exceptionController.injectException(new AccessDeniedException(""));
            String expectedMessage = "인가되지 않은 사용자입니다.";
            int expectedStatusCode = HttpStatus.FORBIDDEN.value();
            int expectedFieldSize = 1;

            // when & then
            RestAssured
                    .given()
                    .when()
                    .get("/test/exception-controller/inject-exception")
                    .then()
                    .log()
                    .all()
                    .statusCode(expectedStatusCode)
                    .body("size()", equalTo(expectedFieldSize))
                    .body("message", containsString(expectedMessage));
        }
    }

    @Nested
    class handleWarnDatabaseException {

        @ParameterizedTest
        @MethodSource
        void 데이터베이스_일시적인_예외_발생시_500_응답(DataAccessException exception) {
            // given
            exceptionController.injectException(exception);
            int expectedStatusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            int expectedFieldSize = 1;

            // when & then
            RestAssured
                    .given()
                    .when()
                    .get("/test/exception-controller/inject-exception")
                    .then()
                    .log()
                    .all()
                    .statusCode(expectedStatusCode)
                    .body("size()", equalTo(expectedFieldSize));
        }

        private static Stream<DataAccessException> 데이터베이스_일시적인_예외_발생시_500_응답() {
            return Stream.of(
                    new DataIntegrityViolationException(""),
                    new DataAccessResourceFailureException("")
            );
        }
    }

    @Nested
    class handleErrorDatabaseException {

        @ParameterizedTest
        @MethodSource
        void 데이터베이스_치명적인_예외_발생시_500_응답(DataAccessException exception) {
            // given
            exceptionController.injectException(exception);
            int expectedStatusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            int expectedFieldSize = 1;

            // when & then
            RestAssured
                    .given()
                    .when()
                    .get("/test/exception-controller/inject-exception")
                    .then()
                    .log()
                    .all()
                    .statusCode(expectedStatusCode)
                    .body("size()", equalTo(expectedFieldSize));
        }

        private static Stream<DataAccessException> 데이터베이스_치명적인_예외_발생시_500_응답() {
            return Stream.of(
                    new BadSqlGrammarException("", "", new SQLException()),
                    new BadJpqlGrammarException("", "", new Throwable())
            );
        }
    }

    @Nested
    class handleRuntimeException {

        @Test
        void 성공_예상치_못한_예외_발생시_안내_메시지를_응답() {
            // given
            exceptionController.injectException(new RuntimeException());
            String expectedMessage = "서버에 오류가 발생하였습니다. 관리자에게 문의해주세요.";
            int expectedStatusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            int expectedFieldSize = 1;

            // when & then
            RestAssured
                    .given()
                    .when()
                    .get("/test/exception-controller/inject-exception")
                    .then()
                    .log()
                    .all()
                    .statusCode(expectedStatusCode)
                    .body("size()", equalTo(expectedFieldSize))
                    .body("message", equalTo(expectedMessage));
        }
    }

    @RestController
    static public class ExceptionController {

        private Exception exception;

        @GetMapping("/test/exception-controller/custom-exception")
        public void test1(
                @RequestParam(MESSAGE_PARAM_NAME) String message,
                @RequestParam(STATUS_CODE_PARAM_NAME) int statusCode
        ) {
            throw new BusinessException(message, HttpStatus.valueOf(statusCode));
        }

        @GetMapping("/test/exception-controller/inject-exception")
        public void test() throws Exception {
            throw exception;
        }

        public void injectException(Exception exception) {
            this.exception = exception;
        }
    }

    private static class TestDatabaseException extends DatabaseException {
        TestDatabaseException(String message, HttpStatus status) {
            super(message, "original", status);
        }
    }
}
