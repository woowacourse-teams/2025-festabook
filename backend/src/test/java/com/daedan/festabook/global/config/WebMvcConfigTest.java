package com.daedan.festabook.global.config;

import static org.hamcrest.Matchers.equalTo;

import com.daedan.festabook.global.argumentresolver.OrganizationId;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class WebMvcConfigTest {

    private static final String ORGANIZATION_HEADER_NAME = "organization";

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    class addArgumentResolvers {

        @Test
        void 성공_축제_조직_ID_파라미터_바인딩() {
            // given
            Long organizationId = 1L;

            String expected = String.valueOf(organizationId);

            // when && then
            RestAssured.given()
                    .header(ORGANIZATION_HEADER_NAME, organizationId)
                    .when()
                    .get("/test")
                    .then()
                    .statusCode(200)
                    .body(equalTo(expected));
        }
    }

    @RestController
    static class TestController {

        @GetMapping("/test")
        @ResponseStatus(HttpStatus.OK)
        public Long test(@OrganizationId Long organizationId) {
            return organizationId;
        }
    }
}
