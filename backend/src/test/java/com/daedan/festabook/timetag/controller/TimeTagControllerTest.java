package com.daedan.festabook.timetag.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.global.security.JwtTestHelper;
import com.daedan.festabook.global.security.role.RoleType;
import com.daedan.festabook.timetag.domain.TimeTag;
import com.daedan.festabook.timetag.domain.TimeTagFixture;
import com.daedan.festabook.timetag.dto.TimeTagCreateRequest;
import com.daedan.festabook.timetag.dto.TimeTagCreateRequestFixture;
import com.daedan.festabook.timetag.dto.TimeTagUpdateRequest;
import com.daedan.festabook.timetag.dto.TimeTagUpdateRequestFixture;
import com.daedan.festabook.timetag.infrastructure.TimeTagJpaRepository;
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
class TimeTagControllerTest {

    private static final String FESTIVAL_HEADER_NAME = "festival";

    @Autowired
    private FestivalJpaRepository festivalJpaRepository;

    @Autowired
    private TimeTagJpaRepository timeTagJpaRepository;

    @Autowired
    private JwtTestHelper jwtTestHelper;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
    }

    @Nested
    class createTimeTag {

        @ParameterizedTest
        @EnumSource(RoleType.class)
        void 성공(RoleType roleType) {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            int expectedFieldSize = 3;

            Header authorizationHeader = jwtTestHelper.createAuthorizationHeaderWithRole(festival, roleType);
            TimeTagCreateRequest request = TimeTagCreateRequestFixture.createDefault();

            // when & then
            RestAssured
                    .given()
                    .header(authorizationHeader)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/time-tags")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("id", notNullValue())
                    .body("festivalId", equalTo(festival.getId().intValue()))
                    .body("name", equalTo(request.name()));
        }
    }

    @Nested
    class getAllTimeTagByFestivalId {

        @Test
        void 성공_응답_데이터_필드_확인() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            TimeTag timeTag = TimeTagFixture.createWithFestival(festival);
            timeTagJpaRepository.save(timeTag);

            int expectedItemSize = 1;
            int expectedFieldSize = 2;

            // when & then
            RestAssured
                    .given()
                    .header(FESTIVAL_HEADER_NAME, festival.getId())
                    .when()
                    .get("/time-tags")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedItemSize))
                    .body("[0].size()", equalTo(expectedFieldSize))
                    .body("[0].id", equalTo(timeTag.getId().intValue()))
                    .body("[0].name", equalTo(timeTag.getName()));
        }
    }

    @Nested
    class updateTimeTag {

        @ParameterizedTest
        @EnumSource(RoleType.class)
        void 성공(RoleType roleType) {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            TimeTag originalTimeTag = TimeTagFixture.createWithFestival(festival);
            timeTagJpaRepository.save(originalTimeTag);

            Header authorizationHeader = jwtTestHelper.createAuthorizationHeaderWithRole(festival, roleType);
            TimeTagUpdateRequest request = TimeTagUpdateRequestFixture.createDefault();

            // when & then
            RestAssured
                    .given()
                    .header(authorizationHeader)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .patch("/time-tags/{timeTagId}", originalTimeTag.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("id", equalTo(originalTimeTag.getId().intValue()))
                    .body("festivalId", equalTo(festival.getId().intValue()))
                    .body("name", equalTo(request.name()));
        }
    }

    @Nested
    class deleteTimeTag {

        @ParameterizedTest
        @EnumSource(RoleType.class)
        void 성공(RoleType roleType) {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            TimeTag timeTag = TimeTagFixture.createWithFestival(festival);
            timeTagJpaRepository.save(timeTag);

            Header authorizationHeader = jwtTestHelper.createAuthorizationHeaderWithRole(festival, roleType);

            // when & then
            RestAssured
                    .given()
                    .header(authorizationHeader)
                    .when()
                    .delete("/time-tags/{timeTagId}", timeTag.getId())
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            assertThat(timeTagJpaRepository.findById(timeTag.getId())).isEmpty();
        }
    }
}
