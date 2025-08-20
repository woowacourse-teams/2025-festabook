package com.daedan.festabook.lineup.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.lineup.dto.LineupRequest;
import com.daedan.festabook.lineup.dto.LineupRequestFixture;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDateTime;
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
}
