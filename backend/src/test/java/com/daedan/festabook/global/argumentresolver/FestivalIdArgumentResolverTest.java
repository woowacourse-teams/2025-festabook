package com.daedan.festabook.global.argumentresolver;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

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
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Import(TestSecurityConfig.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class FestivalIdArgumentResolverTest {

    private static final String FESTIVAL_HEADER_NAME = "festival";

    @Autowired
    private FestivalJpaRepository festivalJpaRepository;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    class resolveArgument {

        @Test
        void 성공() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            // when & then
            RestAssured
                    .given()
                    .header("festival", festival.getId())
                    .when()
                    .get("/test/resolveArgument")
                    .then()
                    .log()
                    .all()
                    .statusCode(HttpStatus.OK.value())
                    .body(equalTo(String.valueOf(festival.getId())));
        }

        @Test
        void 예외_헤더가_누락된_경우() {
            // when & then
            RestAssured
                    .given()
                    .when()
                    .get("/test/resolveArgument")
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .body(containsString("Festival 헤더가 누락되었습니다"));
        }

        @Test
        void 예외_숫자가_아닌_값이_전달된_경우() {
            // given
            String invalidFestivalId = "invalid-id";

            // when & then
            RestAssured
                    .given()
                    .header(FESTIVAL_HEADER_NAME, invalidFestivalId)
                    .when()
                    .get("/test/resolveArgument")
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .body(containsString("Festival 헤더의 값은 숫자여야 합니다"));
        }

        @Test
        void 예외_존재하지_않는_축제_ID가_전달된_경우_실패() {
            // given
            Long nonExistingFestivalId = 999999L;

            // when & then
            RestAssured
                    .given()
                    .header(FESTIVAL_HEADER_NAME, nonExistingFestivalId)
                    .when()
                    .get("/test/resolveArgument")
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .body(containsString("존재하지 않는 FestivalId 입니다"));
        }
    }

    @RestController
    static class TestController {

        @GetMapping("/test/resolveArgument")
        @ResponseStatus(HttpStatus.OK)
        public Long test(@FestivalId Long festivalId) {
            return festivalId;
        }
    }
}
