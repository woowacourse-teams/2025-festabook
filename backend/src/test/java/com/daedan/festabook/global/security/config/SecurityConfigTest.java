package com.daedan.festabook.global.security.config;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

import com.daedan.festabook.council.dto.CouncilLoginRequest;
import com.daedan.festabook.council.dto.CouncilLoginRequestFixture;
import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.global.config.TestSecurityConfig;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Import({TestSecurityConfig.class, SecurityConfig.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SecurityConfigTest {

    private static final String ORIGIN_LOCALHOST = "http://localhost:5173";
    private static final String ORIGIN_LOCALHOST_IP = "http://127.0.0.1:5173";

    @Autowired
    private FestivalJpaRepository festivalJpaRepository;

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    class CORS_검증 {

        @Test
        void 성공_CORS_POST() {
            // given
            String expectedResponse = "addCorsMappings-POST";

            // when & then
            RestAssured
                    .given()
                    .header("Origin", ORIGIN_LOCALHOST)
                    .when()
                    .post("/test/addCorsMappings")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(equalTo(expectedResponse))
                    .header("Access-Control-Allow-Origin", equalTo(ORIGIN_LOCALHOST));

            RestAssured
                    .given()
                    .header("Origin", ORIGIN_LOCALHOST_IP)
                    .when()
                    .post("/test/addCorsMappings")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(equalTo(expectedResponse))
                    .header("Access-Control-Allow-Origin", equalTo(ORIGIN_LOCALHOST_IP));
        }

        @Test
        void 성공_CORS_GET() {
            // given
            String expectedResponse = "addCorsMappings-GET";

            // when & then
            RestAssured
                    .given()
                    .header("Origin", ORIGIN_LOCALHOST)
                    .when()
                    .get("/test/addCorsMappings")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(equalTo(expectedResponse))
                    .header("Access-Control-Allow-Origin", equalTo(ORIGIN_LOCALHOST));

            RestAssured
                    .given()
                    .header("Origin", ORIGIN_LOCALHOST_IP)
                    .when()
                    .get("/test/addCorsMappings")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(equalTo(expectedResponse))
                    .header("Access-Control-Allow-Origin", equalTo(ORIGIN_LOCALHOST_IP));
        }

        @Test
        void 성공_CORS_PUT() {
            // given
            String expectedResponse = "addCorsMappings-PUT";

            // when & then
            RestAssured
                    .given()
                    .header("Origin", ORIGIN_LOCALHOST)
                    .when()
                    .put("/test/addCorsMappings")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(equalTo(expectedResponse))
                    .header("Access-Control-Allow-Origin", equalTo(ORIGIN_LOCALHOST));

            RestAssured
                    .given()
                    .header("Origin", ORIGIN_LOCALHOST_IP)
                    .when()
                    .put("/test/addCorsMappings")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(equalTo(expectedResponse))
                    .header("Access-Control-Allow-Origin", equalTo(ORIGIN_LOCALHOST_IP));
        }

        @Test
        void 성공_CORS_PATCH() {
            // given
            String expectedResponse = "addCorsMappings-PATCH";

            // when & then
            RestAssured
                    .given()
                    .header("Origin", ORIGIN_LOCALHOST)
                    .when()
                    .patch("/test/addCorsMappings")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(equalTo(expectedResponse))
                    .header("Access-Control-Allow-Origin", equalTo(ORIGIN_LOCALHOST));

            RestAssured
                    .given()
                    .header("Origin", ORIGIN_LOCALHOST_IP)
                    .when()
                    .patch("/test/addCorsMappings")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(equalTo(expectedResponse))
                    .header("Access-Control-Allow-Origin", equalTo(ORIGIN_LOCALHOST_IP));
        }

        @Test
        void 성공_CORS_DELETE() {
            // given
            String expectedResponse = "addCorsMappings-DELETE";

            // when & then
            RestAssured
                    .given()
                    .header("Origin", ORIGIN_LOCALHOST)
                    .when()
                    .delete("/test/addCorsMappings")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(equalTo(expectedResponse))
                    .header("Access-Control-Allow-Origin", equalTo(ORIGIN_LOCALHOST));

            RestAssured
                    .given()
                    .header("Origin", ORIGIN_LOCALHOST_IP)
                    .when()
                    .delete("/test/addCorsMappings")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(equalTo(expectedResponse))
                    .header("Access-Control-Allow-Origin", equalTo(ORIGIN_LOCALHOST_IP));
        }

        @Test
        void 성공_CORS_Preflight_요청() {
            // given & when & then
            RestAssured
                    .given()
                    .header("Access-Control-Request-Method", "POST")
                    .header("Access-Control-Request-Headers", "Content-Type,festival")
                    .when()
                    .options("/test/addCorsMappings")
                    .then()
                    .log().all()
                    .statusCode(HttpStatus.OK.value())
                    .header("Allow", containsString("POST"))
                    .header("Allow", containsString("GET"))
                    .header("Allow", containsString("PUT"))
                    .header("Allow", containsString("PATCH"))
                    .header("Allow", containsString("DELETE"));
        }

        @Test
        void 예외_CORS_허용되지않은_origin() {
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

    @Nested
    class 화이트리스트_검증 {

        @Test
        void 성공_GET_화이트리스트_허용() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            // when & then
            RestAssured
                    .given()
                    .header("festival", festival.getId())
                    .when()
                    .get("/announcements")
                    .then()
                    .statusCode(HttpStatus.OK.value());
        }

        @Test
        void 성공_POST_화이트리스트_허용() {
            // given &
            CouncilLoginRequest request = CouncilLoginRequestFixture.create("test123456", "1234");

            // when & then
            RestAssured
                    .given()
                    .header("festival", 1L)
                    .contentType("application/json")
                    .body(request)
                    .when()
                    .post("/councils/login")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        void 성공_DELETE_화이트리스트_허용() {
            // given & when & then
            RestAssured
                    .given()
                    .header("festival", 1L)
                    .when()
                    .delete("/places/favorites/123")
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        }
    }

    @Nested
    class 보호자원_검증 {

        @Test
        void 성공_인증_필요하다면_401_반환() {
            RestAssured
                    .given()
                    .when()
                    .post("/protected")
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                    .body(containsString("인증이 필요합니다."));
        }
    }

    @RestController
    static class TestController {

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
