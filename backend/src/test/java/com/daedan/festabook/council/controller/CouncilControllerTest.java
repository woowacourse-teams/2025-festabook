package com.daedan.festabook.council.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;

import com.daedan.festabook.council.dto.CouncilLoginRequest;
import com.daedan.festabook.council.dto.CouncilLoginRequestFixture;
import com.daedan.festabook.council.dto.CouncilRequest;
import com.daedan.festabook.council.dto.CouncilRequestFixture;
import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.global.security.role.RoleType;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
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

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CouncilControllerTest {

    @Autowired
    private FestivalJpaRepository festivalJpaRepository;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    class createCouncil {

        @Test
        void 성공() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            String username = "hello";
            CouncilRequest request = CouncilRequestFixture.create(festival.getId(), username, "1234");

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/councils")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("festivalId", notNullValue())
                    .body("username", equalTo(username))
                    .body("roleTypes", hasItem(RoleType.ROLE_COUNCIL.name()));
        }
    }

    @Nested
    class loginCouncil {

        @Test
        void 성공() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            String username = "test";
            String password = "1234";
            CouncilRequest councilRequest = CouncilRequestFixture.create(festival.getId(), username, password);

            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body(councilRequest)
                    .when()
                    .post("/councils")
                    .then()
                    .statusCode(HttpStatus.CREATED.value());

            CouncilLoginRequest councilLoginRequest = CouncilLoginRequestFixture.create(username, password);

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body(councilLoginRequest)
                    .when()
                    .post("/councils/login")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("festivalId", equalTo(festival.getId().intValue()))
                    .body("accessToken", notNullValue());
        }
    }
}
