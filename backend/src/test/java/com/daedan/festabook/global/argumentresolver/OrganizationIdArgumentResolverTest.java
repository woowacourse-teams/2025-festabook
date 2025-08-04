package com.daedan.festabook.global.argumentresolver;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

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
class OrganizationIdArgumentResolverTest {

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
    class resolveArgument {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            // when & then
            RestAssured
                    .given()
                    .header("organization", organization.getId())
                    .when()
                    .get("/test/resolveArgument")
                    .then()
                    .log()
                    .all()
                    .statusCode(HttpStatus.OK.value())
                    .body(equalTo(String.valueOf(organization.getId())));
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
                    .body(containsString("Organization 헤더가 누락되었습니다"));
        }

        @Test
        void 예외_숫자가_아닌_값이_전달된_경우() {
            // given
            String invalidOrganizationId = "invalid-id";

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, invalidOrganizationId)
                    .when()
                    .get("/test/resolveArgument")
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .body(containsString("Organization 헤더의 값은 숫자여야 합니다"));
        }

        @Test
        void 예외_존재하지_않는_조직_ID가_전달된_경우_실패() {
            // given
            Long nonExistingOrganizationId = 999999L;

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, nonExistingOrganizationId)
                    .when()
                    .get("/test/resolveArgument")
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .body(containsString("존재하지 않는 OrganizationId 입니다"));
        }
    }

    @RestController
    static class TestController {

        @GetMapping("/test/resolveArgument")
        @ResponseStatus(HttpStatus.OK)
        public Long test(@OrganizationId Long organizationId) {
            return organizationId;
        }
    }
}
