package com.daedan.festabook.global.config;

import static org.hamcrest.Matchers.containsString;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
        void 성공_POST() {
            // given
            String origin1 = "http://localhost:5173";
            String origin2 = "http://127.0.0.1:5173";
            String expectedResponse = "addCorsMappings-POST";
            String expectedAllowCredentials = "true";

            // when & then
            RestAssured.
                    given()
                    .header("Origin", origin1)
                    .when()
                    .post("/test/addCorsMappings")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(equalTo(expectedResponse))
                    .header("Access-Control-Allow-Origin", equalTo(origin1))
                    .header("Access-Control-Allow-Credentials", equalTo(expectedAllowCredentials));

            RestAssured.
                    given()
                    .header("Origin", origin2)
                    .when()
                    .post("/test/addCorsMappings")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(equalTo(expectedResponse))
                    .header("Access-Control-Allow-Origin", equalTo(origin2))
                    .header("Access-Control-Allow-Credentials", equalTo(expectedAllowCredentials));
        }

        @Test
        void 성공_GET() {
            // given
            String origin1 = "http://localhost:5173";
            String origin2 = "http://127.0.0.1:5173";
            String expectedResponse = "addCorsMappings-GET";
            String expectedAllowCredentials = "true";

            // when & then
            RestAssured.
                    given()
                    .header("Origin", origin1)
                    .when()
                    .get("/test/addCorsMappings")
                    .then()
                    .statusCode(HttpStatus.OK.value())
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
                    .body(equalTo(expectedResponse))
                    .header("Access-Control-Allow-Origin", equalTo(origin2))
                    .header("Access-Control-Allow-Credentials", equalTo(expectedAllowCredentials));
        }

        @Test
        void 성공_PUT() {
            // given
            String origin1 = "http://localhost:5173";
            String origin2 = "http://127.0.0.1:5173";
            String expectedResponse = "addCorsMappings-PUT";
            String expectedAllowCredentials = "true";

            // when & then
            RestAssured.
                    given()
                    .header("Origin", origin1)
                    .when()
                    .put("/test/addCorsMappings")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(equalTo(expectedResponse))
                    .header("Access-Control-Allow-Origin", equalTo(origin1))
                    .header("Access-Control-Allow-Credentials", equalTo(expectedAllowCredentials));

            RestAssured.
                    given()
                    .header("Origin", origin2)
                    .when()
                    .put("/test/addCorsMappings")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(equalTo(expectedResponse))
                    .header("Access-Control-Allow-Origin", equalTo(origin2))
                    .header("Access-Control-Allow-Credentials", equalTo(expectedAllowCredentials));
        }

        @Test
        void 성공_PATCH() {
            // given
            String origin1 = "http://localhost:5173";
            String origin2 = "http://127.0.0.1:5173";
            String expectedResponse = "addCorsMappings-PATCH";
            String expectedAllowCredentials = "true";

            // when & then
            RestAssured.
                    given()
                    .header("Origin", origin1)
                    .when()
                    .patch("/test/addCorsMappings")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(equalTo(expectedResponse))
                    .header("Access-Control-Allow-Origin", equalTo(origin1))
                    .header("Access-Control-Allow-Credentials", equalTo(expectedAllowCredentials));

            RestAssured.
                    given()
                    .header("Origin", origin2)
                    .when()
                    .patch("/test/addCorsMappings")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(equalTo(expectedResponse))
                    .header("Access-Control-Allow-Origin", equalTo(origin2))
                    .header("Access-Control-Allow-Credentials", equalTo(expectedAllowCredentials));
        }

        @Test
        void 성공_DELETE() {
            // given
            String origin1 = "http://localhost:5173";
            String origin2 = "http://127.0.0.1:5173";
            String expectedResponse = "addCorsMappings-DELETE";
            String expectedAllowCredentials = "true";

            // when & then
            RestAssured.
                    given()
                    .header("Origin", origin1)
                    .when()
                    .delete("/test/addCorsMappings")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(equalTo(expectedResponse))
                    .header("Access-Control-Allow-Origin", equalTo(origin1))
                    .header("Access-Control-Allow-Credentials", equalTo(expectedAllowCredentials));

            RestAssured.
                    given()
                    .header("Origin", origin2)
                    .when()
                    .delete("/test/addCorsMappings")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(equalTo(expectedResponse))
                    .header("Access-Control-Allow-Origin", equalTo(origin2))
                    .header("Access-Control-Allow-Credentials", equalTo(expectedAllowCredentials));
        }

        @Test
        void 성공_Preflight_요청() {
            // given & when & then
            RestAssured.
                    given()
                    .header("Access-Control-Request-Method", "POST")
                    .header("Access-Control-Request-Headers", "Content-Type,organization")
                    .when()
                    .options("/test/addCorsMappings")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .header("Allow", containsString("POST"))
                    .header("Allow", containsString("GET"))
                    .header("Allow", containsString("PUT"))
                    .header("Allow", containsString("PATCH"))
                    .header("Allow", containsString("DELETE"));
        }

        @Test
        void 실패_허용되지않은_origin() {
            // given
            String invalidOrigin = "https://naver.com";

            // when & then
            RestAssured.
                    given()
                    .header("Origin", invalidOrigin)
                    .when()
                    .get("/test/addCorsMappings")
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }
    }

    @RestController
    static class TestController {

        @GetMapping("/test/addArgumentResolvers")
        @ResponseStatus(HttpStatus.OK)
        public Long test(@OrganizationId Long organizationId) {
            return organizationId;
        }

        @PostMapping("/test/addCorsMappings")
        @ResponseStatus(HttpStatus.OK)
        public String test1() {
            return "addCorsMappings-POST";
        }

        @GetMapping("/test/addCorsMappings")
        @ResponseStatus(HttpStatus.OK)
        public String test2() {
            return "addCorsMappings-GET";
        }

        @PutMapping("/test/addCorsMappings")
        @ResponseStatus(HttpStatus.OK)
        public String test3() {
            return "addCorsMappings-PUT";
        }

        @PatchMapping("/test/addCorsMappings")
        @ResponseStatus(HttpStatus.OK)
        public String test4() {
            return "addCorsMappings-PATCH";
        }

        @DeleteMapping("/test/addCorsMappings")
        @ResponseStatus(HttpStatus.OK)
        public String test5() {
            return "addCorsMappings-DELETE";
        }
    }
}
