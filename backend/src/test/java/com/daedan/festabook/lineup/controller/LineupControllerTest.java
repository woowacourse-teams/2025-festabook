package com.daedan.festabook.lineup.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.global.security.JwtTestHelper;
import com.daedan.festabook.lineup.domain.Lineup;
import com.daedan.festabook.lineup.domain.LineupFixture;
import com.daedan.festabook.lineup.dto.LineupRequest;
import com.daedan.festabook.lineup.dto.LineupRequestFixture;
import com.daedan.festabook.lineup.dto.LineupUpdateRequest;
import com.daedan.festabook.lineup.dto.LineupUpdateRequestFixture;
import com.daedan.festabook.lineup.infrastructure.LineupJpaRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import java.time.LocalDateTime;
import java.util.List;
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
class LineupControllerTest {

    private static final String FESTIVAL_HEADER_NAME = "festival";

    @Autowired
    private FestivalJpaRepository festivalJpaRepository;

    @Autowired
    private LineupJpaRepository lineupJpaRepository;

    @Autowired
    private JwtTestHelper jwtTestHelper;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    class addLineup {

        @Test
        void 성공() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header header = jwtTestHelper.createAuthorizationHeader(festival);

            String lineupName = "이미소";
            String imageUrl = "https://image.example/a.jpg";
            LocalDateTime performanceDateTime = LocalDateTime.of(2025, 5, 20, 20, 0);

            LineupRequest request = LineupRequestFixture.create(
                    lineupName,
                    imageUrl,
                    performanceDateTime
            );

            int expectedFieldSize = 4;

            // when & then
            RestAssured
                    .given()
                    .header(header)
                    .header(FESTIVAL_HEADER_NAME, festival.getId())
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/lineups")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("lineupId", notNullValue())
                    .body("name", equalTo(request.name()))
                    .body("imageUrl", equalTo(request.imageUrl()))
                    .body("performanceAt", notNullValue());
        }
    }

    @Nested
    class getAllLineupByFestivalId {

        @Test
        void 성공_날짜_오름차순_정렬_반환() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header header = jwtTestHelper.createAuthorizationHeader(festival);

            LocalDateTime dateTime1 = LocalDateTime.of(2025, 5, 20, 20, 0);
            LocalDateTime dateTime2 = LocalDateTime.of(2025, 5, 19, 20, 0);
            LocalDateTime dateTime3 = LocalDateTime.of(2025, 5, 18, 20, 0);

            Lineup lineup1 = LineupFixture.create(festival, "이미소", dateTime1);
            Lineup lineup2 = LineupFixture.create(festival, "후유", dateTime2);
            Lineup lineup3 = LineupFixture.create(festival, "부기", dateTime3);

            lineupJpaRepository.saveAll(List.of(lineup1, lineup2, lineup3));

            int expectedFieldSize = 3;

            // when & then
            RestAssured
                    .given()
                    .header(header)
                    .header(FESTIVAL_HEADER_NAME, festival.getId())
                    .when()
                    .get("/lineups")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("[0].performanceAt", notNullValue())
                    .body("[0].name", equalTo("부기"))

                    .body("[1].performanceAt", notNullValue())
                    .body("[1].name", equalTo("후유"))

                    .body("[2].performanceAt", notNullValue())
                    .body("[2].name", equalTo("이미소"));
        }
    }

    @Nested
    class updateLineup {

        @Test
        void 성공() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header header = jwtTestHelper.createAuthorizationHeader(festival);

            Lineup lineup = LineupFixture.create(festival);
            lineupJpaRepository.save(lineup);

            LineupUpdateRequest request = LineupUpdateRequestFixture.create(
                    "수정된이름",
                    "https://updated-image.example/image.jpg",
                    LocalDateTime.of(2025, 5, 21, 21, 0)
            );

            int expectedFieldSize = 4;

            // when & then
            RestAssured
                    .given()
                    .header(header)
                    .header(FESTIVAL_HEADER_NAME, festival.getId())
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .patch("/lineups/{lineupId}", lineup.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("lineupId", equalTo(lineup.getId().intValue()))
                    .body("name", equalTo(request.name()))
                    .body("imageUrl", equalTo(request.imageUrl()))
                    .body("performanceAt", notNullValue());
        }
    }

    @Nested
    class deleteLineupByLineupId {

        @Test
        void 성공() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header header = jwtTestHelper.createAuthorizationHeader(festival);

            Lineup lineup = LineupFixture.create(festival);
            lineupJpaRepository.save(lineup);

            // when & then
            RestAssured
                    .given()
                    .header(header)
                    .when()
                    .delete("/lineups/{lineupId}", lineup.getId())
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // 실제 삭제 확인
            assertThat(lineupJpaRepository.findById(lineup.getId())).isEmpty();
        }

        @Test
        void 성공_존재하지_않는_라인업() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header header = jwtTestHelper.createAuthorizationHeader(festival);

            Long invalidLineupId = 0L;

            // when & then
            RestAssured
                    .given()
                    .header(header)
                    .when()
                    .delete("/lineups/{lineupId}", invalidLineupId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        }
    }
}
