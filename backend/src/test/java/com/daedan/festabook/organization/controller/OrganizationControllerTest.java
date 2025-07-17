package com.daedan.festabook.organization.controller;

import static org.hamcrest.Matchers.equalTo;

import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import com.daedan.festabook.organization.dto.OrganizationGeographyResponse;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.path.json.config.JsonPathConfig;
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

            OrganizationGeographyResponse expected = OrganizationGeographyResponse.from(organization);

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
}
