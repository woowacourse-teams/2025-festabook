package com.daedan.festabook.place.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceFixture;
import com.daedan.festabook.place.infrastructure.PlaceJpaRepository;
import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.config.RestAssuredConfig;
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

            Place place1 = PlaceFixture.create(organization);
            Place place2 = PlaceFixture.create(organization);
            placeJpaRepository.saveAll(List.of(place1, place2));

            int expectedSize = 2;
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
                    .body("[0].id", equalTo(place1.getId().intValue()))
                    .body("[0].category", equalTo(place1.getCategory().name()))
                    .body("[0].markerCoordinate.size()", equalTo(expectedMarkerFieldSize))
                    .body("[0].markerCoordinate.latitude", equalTo(place1.getCoordinate().getLatitude()))
                    .body("[0].markerCoordinate.longitude", equalTo(place1.getCoordinate().getLongitude()))
                    .body("[1].size()", equalTo(expectedFieldSize))
                    .body("[1].id", equalTo(place2.getId().intValue()))
                    .body("[1].category", equalTo(place2.getCategory().name()))
                    .body("[1].markerCoordinate.size()", equalTo(expectedMarkerFieldSize))
                    .body("[1].markerCoordinate.latitude", equalTo(place2.getCoordinate().getLatitude()))
                    .body("[1].markerCoordinate.longitude", equalTo(place2.getCoordinate().getLongitude()));
        }
    }
}
