package com.daedan.festabook.global.exception;

import static org.hamcrest.Matchers.containsString;
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
                    .get("/business-exception-test")
                    .then()
                    .log()
                    .all()
                    .statusCode(expectedStatusCode)
                    .body("size()", equalTo(expectedFieldSize))
                    .body("message", equalTo(expectedMessage));
        }

        @Test
        void 성공_예상치_못한_예외_발생시_안내_메시지를_응답() {
            // given
            String expectedMessage = "관리자에게";
            int expectedStatusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            int expectedFieldSize = 1;

            // when & then
            RestAssured
                    .given()
                    .when()
                    .get("/runtime-exception-test")
                    .then()
                    .log()
                    .all()
                    .statusCode(expectedStatusCode)
                    .body("size()", equalTo(expectedFieldSize))
                    .body("message", containsString(expectedMessage));
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
