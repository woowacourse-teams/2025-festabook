package com.daedan.festabook.organization.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;

import com.daedan.festabook.event.domain.EventDate;
import com.daedan.festabook.event.domain.EventDateFixture;
import com.daedan.festabook.event.infrastructure.EventDateJpaRepository;
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
import java.time.LocalDate;
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

    @Autowired
    private EventDateJpaRepository eventDateJpaRepository;

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

            FestivalImage festivalImage2 = FestivalImageFixture.create(organization, 2);
            FestivalImage festivalImage1 = FestivalImageFixture.create(organization, 1);
            festivalImageJpaRepository.saveAll(List.of(festivalImage2, festivalImage1));

            EventDate thirdEventDate = EventDateFixture.create(organization, LocalDate.of(2025, 8, 3));
            EventDate secondEventDate = EventDateFixture.create(organization, LocalDate.of(2025, 8, 2));
            EventDate firstEventDate = EventDateFixture.create(organization, LocalDate.of(2025, 8, 1));
            eventDateJpaRepository.saveAll(List.of(thirdEventDate, secondEventDate, firstEventDate));

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
                    .body("startDate", equalTo(firstEventDate.getDate().toString()))
                    .body("endDate", equalTo(thirdEventDate.getDate().toString()))

                    .body("festivalImages[0].id", equalTo(festivalImage1.getId().intValue()))
                    .body("festivalImages[0].imageUrl", equalTo(festivalImage1.getImageUrl()))
                    .body("festivalImages[0].sequence", equalTo(festivalImage1.getSequence()))

                    .body("festivalImages[1].id", equalTo(festivalImage2.getId().intValue()))
                    .body("festivalImages[1].imageUrl", equalTo(festivalImage2.getImageUrl()))
                    .body("festivalImages[1].sequence", equalTo(festivalImage2.getSequence()));
        }

        @Test
        void 성공_축제_날짜_중_가장_빠른_날짜와_가장_늦은_날짜가_응답됨() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            EventDate firstEventDate = EventDateFixture.create(organization, LocalDate.of(2025, 8, 1));
            EventDate secondEventDate = EventDateFixture.create(organization, LocalDate.of(2025, 8, 4));
            EventDate thirdEventDate = EventDateFixture.create(organization, LocalDate.of(2025, 8, 5));
            EventDate fourthEventDate = EventDateFixture.create(organization, LocalDate.of(2025, 8, 6));
            eventDateJpaRepository.saveAll(List.of(fourthEventDate, thirdEventDate, secondEventDate, firstEventDate));

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .get("/organizations")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("startDate", equalTo(firstEventDate.getDate().toString()))
                    .body("endDate", equalTo(fourthEventDate.getDate().toString()));
        }

        @Test
        void 성공_축제_날짜는_null_가능() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .get("/organizations")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("universityName", equalTo(organization.getUniversityName()))
                    .body("festivalImages", hasSize(0))
                    .body("festivalName", equalTo(organization.getFestivalName()))
                    .body("startDate", nullValue())
                    .body("endDate", nullValue());
        }

        @Test
        void 성공_이미지_오름차순_정렬() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            FestivalImage festivalImage3 = FestivalImageFixture.create(organization, 3);
            FestivalImage festivalImage2 = FestivalImageFixture.create(organization, 2);
            FestivalImage festivalImage1 = FestivalImageFixture.create(organization, 1);
            festivalImageJpaRepository.saveAll(List.of(festivalImage3, festivalImage2, festivalImage1));

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .get("/organizations")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("festivalImages[0].sequence", equalTo(festivalImage1.getSequence()))
                    .body("festivalImages[1].sequence", equalTo(festivalImage2.getSequence()))
                    .body("festivalImages[2].sequence", equalTo(festivalImage3.getSequence()));
        }
    }
}
