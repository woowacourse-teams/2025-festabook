package com.daedan.festabook.place.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;

import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceCoordinateRequestFixture;
import com.daedan.festabook.place.domain.PlaceFixture;
import com.daedan.festabook.place.dto.PlaceCoordinateRequest;
import com.daedan.festabook.place.infrastructure.PlaceJpaRepository;
import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.path.json.config.JsonPathConfig;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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
class PlaceGeographyControllerTest {

    @Autowired
    private OrganizationJpaRepository organizationJpaRepository;

    @Autowired
    private PlaceJpaRepository placeJpaRepository;

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
    class getAllPlaceGeographyByOrganizationId {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Place place = PlaceFixture.create(organization);
            placeJpaRepository.saveAll(List.of(place));

            int expectedSize = 1;
            int expectedFieldSize = 3;
            int expectedMarkerFieldSize = 2;

            // when & then
            RestAssured
                    .given()
                    .header("organization", organization.getId())
                    .when()
                    .get("/places/geographies")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize))
                    .body("[0].size()", equalTo(expectedFieldSize))
                    .body("[0].id", equalTo(place.getId().intValue()))
                    .body("[0].category", equalTo(place.getCategory().name()))
                    .body("[0].markerCoordinate.size()", equalTo(expectedMarkerFieldSize))
                    .body("[0].markerCoordinate.latitude", equalTo(place.getCoordinate().getLatitude()))
                    .body("[0].markerCoordinate.longitude", equalTo(place.getCoordinate().getLongitude()));
        }

        @Test
        void 성공_특정_조직의_플레이스_지리_목록() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Place place1 = PlaceFixture.create(organization);
            Place place2 = PlaceFixture.create(organization);
            placeJpaRepository.saveAll(List.of(place1, place2));

            int expectedSize = 2;

            // when & then
            RestAssured
                    .given()
                    .header("organization", organization.getId())
                    .when()
                    .get("/places/geographies")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize));
        }
    }

    @Nested
    class updatePlaceCoordinate {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Place place = PlaceFixture.create(organization);
            placeJpaRepository.save(place);

            PlaceCoordinateRequest request = PlaceCoordinateRequestFixture.create();

            int expectedFieldSize = 2;
            int expectedCoordinateSize = 2;

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .patch("/places/{placeId}/geographies", place.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("$", hasKey("markerCoordinate"))
                    .body("markerCoordinate.size()", equalTo(expectedCoordinateSize))
                    .body("id", equalTo(place.getId().intValue()))
                    .body("markerCoordinate.latitude", equalTo(request.latitude()))
                    .body("markerCoordinate.longitude", equalTo(request.longitude()));
        }

        @Test
        void 예외_존재하지_않는_플레이스() {
            // given
            Long invalidPlaceId = 0L;
            PlaceCoordinateRequest request = PlaceCoordinateRequestFixture.create();

            // when & then
            RestAssured
                    .given()
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
