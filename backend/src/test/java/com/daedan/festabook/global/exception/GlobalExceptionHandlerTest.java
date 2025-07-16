package com.daedan.festabook.global.exception;

import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class GlobalExceptionHandlerTest {

    private static final String MESSAGE_PARAM_NAME = "message";
    private static final String STATUS_CODE_PARAM_NAME = "status-code";

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    class handleBusinessException {

        @Test
        void 예외를_성공적으로_잡는다() {
            // given
            String expectedMessage = "예외가 발생했습니다.";
            int expectedStatusCode = 418;

            // when & then
            RestAssured
                    .given()
                    .queryParam(MESSAGE_PARAM_NAME, expectedMessage)
                    .queryParam(STATUS_CODE_PARAM_NAME, expectedStatusCode)
                    .when()
                    .get("/business-exception-test")
                    .then()
                    .log()
                    .all()
                    .statusCode(expectedStatusCode)
                    .body(equalTo(expectedMessage));
        }

        @Test
        void BusinessException과_하위_예외를_제외한_예외는_잡히지_않는다() {
            // given
            int expectedStatusCode = 500;

            // when & then
            RestAssured
                    .given()
                    .when()
                    .get("/runtime-exception-test")
                    .then()
                    .log()
                    .all()
                    .statusCode(expectedStatusCode);
        }
    }

    @RestController
    static private class ExceptionController {

        @GetMapping("/business-exception-test")
        public void test1(
                @RequestParam(MESSAGE_PARAM_NAME) String message,
                @RequestParam(STATUS_CODE_PARAM_NAME) int statusCode
        ) {
            throw new BusinessException(message, HttpStatus.valueOf(statusCode));
        }

        @GetMapping("/runtime-exception-test")
        public void test2() {
            throw new RuntimeException();
        }
    }
}
