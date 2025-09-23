package com.daedan.festabook.place.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.global.security.JwtTestHelper;
import com.daedan.festabook.global.security.role.RoleType;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceCategory;
import com.daedan.festabook.place.domain.PlaceFixture;
import com.daedan.festabook.place.dto.PlaceCoordinateRequest;
import com.daedan.festabook.place.dto.PlaceCoordinateRequestFixture;
import com.daedan.festabook.place.infrastructure.PlaceJpaRepository;
import com.daedan.festabook.timetag.domain.PlaceTimeTag;
import com.daedan.festabook.timetag.domain.PlaceTimeTagFixture;
import com.daedan.festabook.timetag.domain.TimeTag;
import com.daedan.festabook.timetag.domain.TimeTagFixture;
import com.daedan.festabook.timetag.infrastructure.PlaceTimeTagJpaRepository;
import com.daedan.festabook.timetag.infrastructure.TimeTagJpaRepository;
import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.path.json.config.JsonPathConfig;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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
class PlaceGeographyControllerTest {

    private static final String FESTIVAL_HEADER_NAME = "festival";

    @Autowired
    private FestivalJpaRepository festivalJpaRepository;

    @Autowired
    private PlaceJpaRepository placeJpaRepository;

    @Autowired
    private JwtTestHelper jwtTestHelper;

    @Autowired
    private TimeTagJpaRepository timeTagJpaRepository;

    @Autowired
    private PlaceTimeTagJpaRepository placeTimeTagJpaRepository;

    @LocalServerPort
    private int port;

    @BeforeAll
    static void beforeAll() {
        RestAssured.config = RestAssured.config()
                .jsonConfig(JsonConfig.jsonConfig()
                        .numberReturnType(JsonPathConfig.NumberReturnType.DOUBLE));
    }

    @AfterAll
    static void afterAll() {
        RestAssured.config = RestAssuredConfig.config();
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    class getAllPlaceGeographyByFestivalId {

        @Test
        void 성공() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Place place = PlaceFixture.create(festival);
            placeJpaRepository.saveAll(List.of(place));

            TimeTag timeTag = TimeTagFixture.createWithFestival(festival);
            timeTagJpaRepository.save(timeTag);

            PlaceTimeTag placeTimeTag = PlaceTimeTagFixture.createWithPlaceAndTimeTag(place, timeTag);
            placeTimeTagJpaRepository.save(placeTimeTag);

            int expectedItemSize = 1;
            int expectedTimeTagsItemSize = 1;
            int expectedFieldSize = 5;
            int expectedTimeTagsFieldSize = 2;
            int expectedMarkerFieldSize = 2;

            // when & then
            RestAssured
                    .given()
                    .header(FESTIVAL_HEADER_NAME, festival.getId())
                    .when()
                    .get("/places/geographies")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedItemSize))
                    .body("[0].size()", equalTo(expectedFieldSize))
                    .body("[0].placeId", equalTo(place.getId().intValue()))
                    .body("[0].category", equalTo(place.getCategory().name()))
                    .body("[0].markerCoordinate.size()", equalTo(expectedMarkerFieldSize))
                    .body("[0].markerCoordinate.latitude", equalTo(place.getCoordinate().getLatitude()))
                    .body("[0].markerCoordinate.longitude", equalTo(place.getCoordinate().getLongitude()))
                    .body("[0].title", equalTo(place.getTitle()))
                    .body("[0].timeTags", hasSize(expectedTimeTagsItemSize))
                    .body("[0].timeTags[0].size()", equalTo(expectedTimeTagsFieldSize))
                    .body("[0].timeTags[0].timeTagId", equalTo(timeTag.getId().intValue()))
                    .body("[0].timeTags[0].name", equalTo(timeTag.getName()));
        }

        @Test
        void 성공_특정_축제의_플레이스_지리_목록_요소_개수() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Place place1 = PlaceFixture.create(festival);
            Place place2 = PlaceFixture.create(festival);
            placeJpaRepository.saveAll(List.of(place1, place2));

            int expectedSize = 2;

            // when & then
            RestAssured
                    .given()
                    .header(FESTIVAL_HEADER_NAME, festival.getId())
                    .when()
                    .get("/places/geographies")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize));
        }

        @Test
        void 성공_Coordinate가_없을_경우_응답에_포함하지_않음() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Place place = PlaceFixture.create(festival, PlaceCategory.BAR, null);
            placeJpaRepository.save(place);

            int expectedSize = 0;

            // when & then
            RestAssured
                    .given()
                    .header(FESTIVAL_HEADER_NAME, festival.getId())
                    .when()
                    .get("/places/geographies")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize));
        }
    }

    @Nested
    class updatePlaceCoordinate {

        @ParameterizedTest
        @EnumSource(RoleType.class)
        void 성공(RoleType roleType) {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createAuthorizationHeaderWithRole(festival, roleType);

            Place place = PlaceFixture.create(festival);
            placeJpaRepository.save(place);

            PlaceCoordinateRequest request = PlaceCoordinateRequestFixture.create();

            int expectedFieldSize = 2;
            int expectedCoordinateSize = 2;

            // when & then
            RestAssured
                    .given()
                    .header(authorizationHeader)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .patch("/places/{placeId}/geographies", place.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("$", hasKey("coordinate"))
                    .body("coordinate.size()", equalTo(expectedCoordinateSize))
                    .body("placeId", equalTo(place.getId().intValue()))
                    .body("coordinate.latitude", equalTo(request.latitude()))
                    .body("coordinate.longitude", equalTo(request.longitude()));
        }

        @Test
        void 예외_존재하지_않는_플레이스() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createCouncilAuthorizationHeader(festival);

            Long invalidPlaceId = 0L;
            PlaceCoordinateRequest request = PlaceCoordinateRequestFixture.create();

            // when & then
            RestAssured
                    .given()
                    .header(authorizationHeader)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .patch("/places/{placeId}/geographies", invalidPlaceId)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("message", equalTo("존재하지 않는 플레이스입니다."));
        }
    }
}
