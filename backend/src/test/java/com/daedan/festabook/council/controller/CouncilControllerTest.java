package com.daedan.festabook.council.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;

import com.daedan.festabook.council.dto.CouncilLoginRequest;
import com.daedan.festabook.council.dto.CouncilLoginRequestFixture;
import com.daedan.festabook.council.dto.CouncilRequest;
import com.daedan.festabook.council.dto.CouncilRequestFixture;
import com.daedan.festabook.council.dto.CouncilResponse;
import com.daedan.festabook.council.dto.CouncilUpdateRequest;
import com.daedan.festabook.council.dto.CouncilUpdateRequestFixture;
import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.global.security.JwtTestHelper;
import com.daedan.festabook.global.security.role.RoleType;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CouncilControllerTest {

    @Autowired
    private FestivalJpaRepository festivalJpaRepository;

    @Autowired
    private JwtTestHelper jwtTestHelper;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    class createCouncil {

        @Test
        void 성공_ADMIN_계정() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createAdminAuthorizationHeader(festival);

            String username = "hello";
            CouncilRequest request = CouncilRequestFixture.create(festival.getId(), username, "1234");

            // when & then
            RestAssured
                    .given()
                    .header(authorizationHeader)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/councils")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("councilId", notNullValue())
                    .body("username", equalTo(username))
                    .body("roleTypes", hasItem(RoleType.ROLE_COUNCIL.name()));
        }

        @Test
        void 실패_권한() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createAuthorizationHeader(festival);

            String username = "hello";
            CouncilRequest request = CouncilRequestFixture.create(festival.getId(), username, "1234");

            // when & then
            RestAssured
                    .given()
                    .header(authorizationHeader)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/councils")
                    .then()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Nested
    class loginCouncil {

        @Test
        void 성공() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createAdminAuthorizationHeader(festival);

            String username = "test";
            String password = "1234";
            CouncilRequest councilRequest = CouncilRequestFixture.create(festival.getId(), username, password);

            RestAssured
                    .given()
                    .header(authorizationHeader)
                    .contentType(ContentType.JSON)
                    .body(councilRequest)
                    .when()
                    .post("/councils")
                    .then()
                    .statusCode(HttpStatus.CREATED.value());

            CouncilLoginRequest request = CouncilLoginRequestFixture.create(username, password);

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/councils/login")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("festivalId", equalTo(festival.getId().intValue()))
                    .body("accessToken", notNullValue());
        }
    }

    @Nested
    class updatePassword {

        @Test
        void 성공() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createAdminAuthorizationHeader(festival);

            String username = "user";
            String currentPassword = "1234";
            CouncilRequest councilRequest = CouncilRequestFixture.create(festival.getId(), username, currentPassword);
            CouncilResponse response = RestAssured
                    .given()
                    .header(authorizationHeader)
                    .contentType(ContentType.JSON)
                    .body(councilRequest)
                    .when()
                    .post("/councils")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .extract()
                    .as(CouncilResponse.class);

            CouncilLoginRequest loginRequest = CouncilLoginRequestFixture.create(username, currentPassword);
            String accessToken = RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body(loginRequest)
                    .when()
                    .post("/councils/login")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .extract()
                    .path("accessToken");

            String newPassword = "5678";
            CouncilUpdateRequest councilUpdateRequest = CouncilUpdateRequestFixture.create(
                    currentPassword,
                    newPassword
            );

            // when & then
            RestAssured
                    .given()
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(ContentType.JSON)
                    .body(councilUpdateRequest)
                    .when()
                    .patch("/councils/password")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("councilId", equalTo(response.councilId().intValue()))
                    .body("username", equalTo(username));
        }

        @ParameterizedTest
        @EnumSource(RoleType.class)
        void 성공_권한(RoleType roleType) {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            String currentPassword = "1234";
            String newPassword = "5678";
            CouncilUpdateRequest councilUpdateRequest = CouncilUpdateRequestFixture.create(
                    currentPassword,
                    newPassword
            );

            Header authorizationHeader = jwtTestHelper
                    .createAuthorizationHeaderWithRoleAndPassword(festival, roleType, currentPassword);

            // when & then
            RestAssured
                    .given()
                    .header(authorizationHeader)
                    .contentType(ContentType.JSON)
                    .body(councilUpdateRequest)
                    .when()
                    .patch("/councils/password")
                    .then()
                    .statusCode(HttpStatus.OK.value());
        }
    }
}
