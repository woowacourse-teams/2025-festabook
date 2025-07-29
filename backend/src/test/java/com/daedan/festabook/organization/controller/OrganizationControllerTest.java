package com.daedan.festabook.organization.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import com.daedan.festabook.organization.domain.FestivalImage;
import com.daedan.festabook.organization.domain.FestivalImageFixture;
import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import com.daedan.festabook.organization.infrastructure.FestivalImageJpaRepository;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
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
class OrganizationControllerTest {

    private static final String ORGANIZATION_HEADER_NAME = "organization";

    @Autowired
    private OrganizationJpaRepository organizationJpaRepository;

    @Autowired
    private FestivalImageJpaRepository festivalImageJpaRepository;

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
    class getOrganizationGeographyByOrganizationId {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            int expectedFieldSize = 3;

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .get("/organizations/geography")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("zoom", equalTo(organization.getZoom()))
                    .body("centerCoordinate.latitude",
                            equalTo(organization.getCenterCoordinate().getLatitude()))
                    .body("centerCoordinate.longitude",
                            equalTo(organization.getCenterCoordinate().getLongitude()))
                    .body("polygonHoleBoundary[0].latitude",
                            equalTo(organization.getPolygonHoleBoundary().get(0).getLatitude()))
                    .body("polygonHoleBoundary[0].longitude",
                            equalTo(organization.getPolygonHoleBoundary().get(0).getLongitude()))
                    .body("polygonHoleBoundary[1].latitude",
                            equalTo(organization.getPolygonHoleBoundary().get(1).getLatitude()))
                    .body("polygonHoleBoundary[1].longitude",
                            equalTo(organization.getPolygonHoleBoundary().get(1).getLongitude()))
                    .body("polygonHoleBoundary[2].latitude",
                            equalTo(organization.getPolygonHoleBoundary().get(2).getLatitude()))
                    .body("polygonHoleBoundary[2].longitude",
                            equalTo(organization.getPolygonHoleBoundary().get(2).getLongitude()));
        }
    }

    @Nested
    class getOrganizationByOrganizationId {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            FestivalImage festivalImage1 = FestivalImageFixture.create(organization);
            FestivalImage festivalImage2 = FestivalImageFixture.create(organization);
            festivalImageJpaRepository.saveAll(List.of(festivalImage1, festivalImage2));

            int festivalImageSize = 2;
            int expectedFieldSize = 6;

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .get("/organizations")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("universityName", equalTo(organization.getUniversityName()))
                    .body("festivalImages", hasSize(festivalImageSize))
                    .body("festivalName", equalTo(organization.getFestivalName()))
                    .body("startDate", equalTo(organization.getStartDate().toString()))
                    .body("endDate", equalTo(organization.getEndDate().toString()))

                    .body("festivalImages[0].id", equalTo(festivalImage1.getId().intValue()))
                    .body("festivalImages[0].imageUrl", equalTo(festivalImage1.getImageUrl()))
                    .body("festivalImages[0].sequence", equalTo(festivalImage1.getSequence()))

                    .body("festivalImages[1].id", equalTo(festivalImage2.getId().intValue()))
                    .body("festivalImages[1].imageUrl", equalTo(festivalImage2.getImageUrl()))
                    .body("festivalImages[1].sequence", equalTo(festivalImage2.getSequence()));
        }
    }
}
