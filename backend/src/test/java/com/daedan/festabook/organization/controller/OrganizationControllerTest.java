package com.daedan.festabook.organization.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import com.daedan.festabook.organization.domain.FestivalImage;
import com.daedan.festabook.organization.domain.FestivalImageFixture;
import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import com.daedan.festabook.organization.dto.FestivalImageDeleteRequest;
import com.daedan.festabook.organization.dto.FestivalImageDeleteRequestFixture;
import com.daedan.festabook.organization.dto.FestivalImageRequest;
import com.daedan.festabook.organization.dto.FestivalImageRequestFixture;
import com.daedan.festabook.organization.dto.FestivalImageSequenceUpdateRequest;
import com.daedan.festabook.organization.dto.FestivalImageSequenceUpdateRequestFixture;
import com.daedan.festabook.organization.dto.OrganizationInformationUpdateRequest;
import com.daedan.festabook.organization.dto.OrganizationInformationUpdateRequestFixture;
import com.daedan.festabook.organization.infrastructure.FestivalImageJpaRepository;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
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
    class addFestivalImage {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            FestivalImageRequest request = FestivalImageRequestFixture.create("이미지 URL");

            int expectedFieldSize = 3;

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/organizations/images")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("festivalImageId", notNullValue())
                    .body("imageUrl", equalTo(request.imageUrl()))
                    .body("sequence", equalTo(1));
        }
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

                    .body("festivalImages[0].festivalImageId", equalTo(festivalImage1.getId().intValue()))
                    .body("festivalImages[0].imageUrl", equalTo(festivalImage1.getImageUrl()))
                    .body("festivalImages[0].sequence", equalTo(festivalImage1.getSequence()))

                    .body("festivalImages[1].festivalImageId", equalTo(festivalImage2.getId().intValue()))
                    .body("festivalImages[1].imageUrl", equalTo(festivalImage2.getImageUrl()))
                    .body("festivalImages[1].sequence", equalTo(festivalImage2.getSequence()));
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

    @Nested
    class updateOrganizationInformation {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            String changedFestivalName = "수정 후 제목";
            LocalDate changedStartDate = LocalDate.of(2025, 11, 1);
            LocalDate changedEndDate = LocalDate.of(2025, 11, 2);
            OrganizationInformationUpdateRequest request = OrganizationInformationUpdateRequestFixture.create(
                    changedFestivalName,
                    changedStartDate,
                    changedEndDate
            );

            int expectedFieldSize = 4;

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .patch("/organizations")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("organizationId", equalTo(organization.getId().intValue()))
                    .body("festivalName", equalTo(changedFestivalName))
                    .body("startDate", equalTo(changedStartDate.toString()))
                    .body("endDate", equalTo(changedEndDate.toString()));
        }
    }

    @Nested
    class updateFestivalImagesSequence {

        @Test
        void 성공_수정_후_응답값_오름차순_정렬() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            FestivalImage festivalImage1 = FestivalImageFixture.create(organization, 1);
            FestivalImage festivalImage2 = FestivalImageFixture.create(organization, 2);
            FestivalImage festivalImage3 = FestivalImageFixture.create(organization, 3);
            festivalImageJpaRepository.saveAll(List.of(festivalImage1, festivalImage2, festivalImage3));

            List<FestivalImageSequenceUpdateRequest> requests = List.of(
                    FestivalImageSequenceUpdateRequestFixture.create(festivalImage1.getId(), 2),
                    FestivalImageSequenceUpdateRequestFixture.create(festivalImage2.getId(), 3),
                    FestivalImageSequenceUpdateRequestFixture.create(festivalImage3.getId(), 1)
            );

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .contentType(ContentType.JSON)
                    .body(requests)
                    .when()
                    .patch("/organizations/images/sequences")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("[0].festivalImageId", equalTo(festivalImage3.getId().intValue()))
                    .body("[0].sequence", equalTo(1))

                    .body("[1].festivalImageId", equalTo(festivalImage1.getId().intValue()))
                    .body("[1].sequence", equalTo(2))

                    .body("[2].festivalImageId", equalTo(festivalImage2.getId().intValue()))
                    .body("[2].sequence", equalTo(3));
        }
    }

    @Nested
    class removeFestivalImages {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            FestivalImage image1 = FestivalImageFixture.create(organization, 1);
            FestivalImage image2 = FestivalImageFixture.create(organization, 2);
            FestivalImage image3 = FestivalImageFixture.create(organization, 3);
            festivalImageJpaRepository.saveAll(List.of(image1, image2, image3));

            List<FestivalImageDeleteRequest> requests = List.of(
                    FestivalImageDeleteRequestFixture.create(image1.getId()),
                    FestivalImageDeleteRequestFixture.create(image2.getId()),
                    FestivalImageDeleteRequestFixture.create(image3.getId())
            );

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .contentType(ContentType.JSON)
                    .body(requests)
                    .when()
                    .delete("/organizations/images")
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            assertThat(festivalImageJpaRepository.findAllByOrganizationIdOrderBySequenceAsc(organization.getId()))
                    .isEqualTo(List.of());
        }

        @Test
        void 성공_없는_리소스_삭제() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            List<FestivalImageDeleteRequest> requests = FestivalImageDeleteRequestFixture.createList(3);

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .contentType(ContentType.JSON)
                    .body(requests)
                    .when()
                    .delete("/organizations/images")
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        }
    }
}
