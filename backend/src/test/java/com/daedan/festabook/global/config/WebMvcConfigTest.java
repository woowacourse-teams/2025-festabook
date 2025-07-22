package com.daedan.festabook.global.config;

import static org.hamcrest.Matchers.equalTo;

import com.daedan.festabook.global.argumentresolver.OrganizationId;
import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private OrganizationJpaRepository organizationJpaRepository;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    class addArgumentResolvers {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            // when && then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .get("/test/addArgumentResolvers")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(equalTo(String.valueOf(organization.getId())));
        }
    }

    @Nested
    class addCorsMappings {

        @Test
        void 성공() {
            // given
            String origin1 = "http://localhost:5173";
            String origin2 = "http://127.0.0.1:5173";
            String expectedResponse = "addCorsMappings";
            String expectedAllowCredentials = "true";

            // when & then
            RestAssured.
                    given()
                    .header("Origin", origin1)
                    .when()
                    .get("/test/addCorsMappings")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .log().all()
                    .body(equalTo(expectedResponse))
                    .header("Access-Control-Allow-Origin", equalTo(origin1))
                    .header("Access-Control-Allow-Credentials", equalTo(expectedAllowCredentials));

            RestAssured.
                    given()
                    .header("Origin", origin2)
                    .when()
                    .get("/test/addCorsMappings")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .log().all()
                    .body(equalTo(expectedResponse))
                    .header("Access-Control-Allow-Origin", equalTo(origin2))
                    .header("Access-Control-Allow-Credentials", equalTo(expectedAllowCredentials));
        }
    }

    @RestController
    static class TestController {

        @GetMapping("/test/addArgumentResolvers")
        @ResponseStatus(HttpStatus.OK)
        public Long test(@OrganizationId Long organizationId) {
            return organizationId;
        }

        @GetMapping("/test/addCorsMappings")
        @ResponseStatus(HttpStatus.OK)
        public String test() {
            return "addCorsMappings";
        }
    }
}
