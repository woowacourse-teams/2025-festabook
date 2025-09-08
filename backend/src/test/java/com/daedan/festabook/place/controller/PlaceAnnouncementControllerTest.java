package com.daedan.festabook.place.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.global.security.JwtTestHelper;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceAnnouncement;
import com.daedan.festabook.place.domain.PlaceAnnouncementFixture;
import com.daedan.festabook.place.domain.PlaceFixture;
import com.daedan.festabook.place.dto.PlaceAnnouncementRequest;
import com.daedan.festabook.place.dto.PlaceAnnouncementRequestFixture;
import com.daedan.festabook.place.dto.PlaceAnnouncementUpdateRequest;
import com.daedan.festabook.place.dto.PlaceAnnouncementUpdateRequestFixture;
import com.daedan.festabook.place.infrastructure.PlaceAnnouncementJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceJpaRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
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
class PlaceAnnouncementControllerTest {

    @Autowired
    private FestivalJpaRepository festivalJpaRepository;

    @Autowired
    private PlaceJpaRepository placeJpaRepository;

    @Autowired
    private PlaceAnnouncementJpaRepository placeAnnouncementJpaRepository;

    @Autowired
    private JwtTestHelper jwtTestHelper;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    class createPlaceAnnouncement {

        @Test
        void 성공() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createAuthorizationHeader(festival);

            Place place = PlaceFixture.create(festival);
            placeJpaRepository.save(place);

            String expectedTitle = "공지입니다.";
            String expectedContent = "공지내용입니다.";
            PlaceAnnouncementRequest request = PlaceAnnouncementRequestFixture.create(
                    expectedTitle,
                    expectedContent
            );

            int expectedFieldSize = 4;

            // when & then
            RestAssured
                    .given()
                    .header(authorizationHeader)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .post("/places/{placeId}/announcements", place.getId())
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("id", notNullValue())
                    .body("title", equalTo(expectedTitle))
                    .body("content", equalTo(expectedContent))
                    .body("createdAt", notNullValue());
        }
    }

    @Nested
    class getAllPlaceAnnouncementsByPlaceId {

        @Test
        void 성공() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Place place = PlaceFixture.create(festival);
            placeJpaRepository.save(place);

            PlaceAnnouncement placeAnnouncement1 = PlaceAnnouncementFixture.create(place);
            PlaceAnnouncement placeAnnouncement2 = PlaceAnnouncementFixture.create(place);
            placeAnnouncementJpaRepository.saveAll(List.of(placeAnnouncement1, placeAnnouncement2));

            int expectedSize = 2;

            // when & then
            RestAssured
                    .given()
                    .when()
                    .get("/places/{placeId}/announcements", place.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize))

                    .body("[0].id", notNullValue())
                    .body("[0].title", equalTo(placeAnnouncement1.getTitle()))
                    .body("[0].content", equalTo(placeAnnouncement1.getContent()))
                    .body("[0].createdAt", notNullValue())

                    .body("[1].id", notNullValue())
                    .body("[1].title", equalTo(placeAnnouncement2.getTitle()))
                    .body("[1].content", equalTo(placeAnnouncement2.getContent()))
                    .body("[1].createdAt", notNullValue());
        }

        @Test
        void 성공_다른_플레이스_공지는_조회되지_않음() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Place place1 = PlaceFixture.create(festival);
            placeJpaRepository.save(place1);

            Place place2 = PlaceFixture.create(festival);
            placeJpaRepository.save(place2);

            PlaceAnnouncement placeAnnouncement1 = PlaceAnnouncementFixture.create(place1);
            PlaceAnnouncement placeAnnouncement2 = PlaceAnnouncementFixture.create(place2);
            placeAnnouncementJpaRepository.saveAll(List.of(placeAnnouncement1, placeAnnouncement2));

            int expectedSize = 1;

            // when & then
            RestAssured
                    .given()
                    .when()
                    .get("/places/{placeId}/announcements", place1.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize));
        }
    }

    @Nested
    class updatePlaceAnnouncement {

        @Test
        void 성공() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createAuthorizationHeader(festival);

            Place place = PlaceFixture.create(festival);
            placeJpaRepository.save(place);

            PlaceAnnouncement placeAnnouncement = PlaceAnnouncementFixture.create(place);
            placeAnnouncementJpaRepository.save(placeAnnouncement);

            int expectedFieldSize = 3;

            PlaceAnnouncementUpdateRequest request = PlaceAnnouncementUpdateRequestFixture.create("수정된 공지", "수정된 내용");

            // when & then
            RestAssured
                    .given()
                    .header(authorizationHeader)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .patch("/places/announcements/{placeAnnouncementId}", placeAnnouncement.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("id", notNullValue())
                    .body("title", equalTo(request.title()))
                    .body("content", equalTo(request.content()));
        }
    }

    @Nested
    class deleteByPlaceAnnouncementId {

        @Test
        void 성공() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createAuthorizationHeader(festival);

            Place place = PlaceFixture.create(festival);
            placeJpaRepository.save(place);

            PlaceAnnouncement placeAnnouncement = PlaceAnnouncementFixture.create(place);
            placeAnnouncementJpaRepository.save(placeAnnouncement);

            // when & then
            RestAssured
                    .given()
                    .header(authorizationHeader)
                    .when()
                    .delete("/places/announcements/{placeAnnouncementId}", placeAnnouncement.getId())
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            assertThat(placeAnnouncementJpaRepository.findById(placeAnnouncement.getId())).isEmpty();
        }
    }
}
