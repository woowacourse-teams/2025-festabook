package com.daedan.festabook.place.controller;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceAnnouncement;
import com.daedan.festabook.place.domain.PlaceAnnouncementFixture;
import com.daedan.festabook.place.domain.PlaceCategory;
import com.daedan.festabook.place.domain.PlaceDetail;
import com.daedan.festabook.place.domain.PlaceDetailFixture;
import com.daedan.festabook.place.domain.PlaceFixture;
import com.daedan.festabook.place.domain.PlaceImage;
import com.daedan.festabook.place.domain.PlaceImageFixture;
import com.daedan.festabook.place.dto.PlaceRequest;
import com.daedan.festabook.place.dto.PlaceRequestFixture;
import com.daedan.festabook.place.infrastructure.PlaceAnnouncementJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceDetailJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceImageJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceJpaRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
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
class PlaceControllerTest {

    private static final String ORGANIZATION_HEADER_NAME = "organization";

    @Autowired
    private OrganizationJpaRepository organizationJpaRepository;

    @Autowired
    private PlaceJpaRepository placeJpaRepository;

    @Autowired
    private PlaceDetailJpaRepository placeDetailJpaRepository;

    @Autowired
    private PlaceAnnouncementJpaRepository placeAnnouncementJpaRepository;

    @Autowired
    private PlaceImageJpaRepository placeImageJpaRepository;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    class createPlace {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            PlaceCategory expectedPlaceCategory = PlaceCategory.BAR;
            PlaceRequest placeRequest = PlaceRequestFixture.create(expectedPlaceCategory);

            int expectedFieldSize = 10;

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .contentType(ContentType.JSON)
                    .body(placeRequest)
                    .post("/places")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("id", notNullValue())
                    .body("category", equalTo(expectedPlaceCategory.toString()))

                    .body("placeImages", empty())
                    .body("placeAnnouncements", empty())

                    .body("title", nullValue())
                    .body("startTime", nullValue())
                    .body("endTime", nullValue())
                    .body("location", nullValue())
                    .body("host", nullValue())
                    .body("description", nullValue());
        }
    }

    @Nested
    class getALlPlaces {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Place mainPlace = PlaceFixture.create(organization);
            placeJpaRepository.save(mainPlace);

            PlaceDetail mainPlaceDetail = PlaceDetailFixture.create(mainPlace);
            placeDetailJpaRepository.save(mainPlaceDetail);

            Place etcPlace = PlaceFixture.create(organization);
            placeJpaRepository.save(etcPlace);

            int expectedSize = 2;

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .get("/places")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize));
        }

        @Test
        void 성공_MainPlace인_경우() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Place mainPlace = PlaceFixture.create(organization);
            placeJpaRepository.save(mainPlace);

            PlaceDetail mainPlaceDetail = PlaceDetailFixture.create(mainPlace);
            placeDetailJpaRepository.save(mainPlaceDetail);

            int expectedSize = 1;

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .get("/places")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize));
        }

        @Test
        void 성공_EtcPlace인_경우() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Place etcPlace = PlaceFixture.create(organization);
            placeJpaRepository.save(etcPlace);

            int expectedSize = 1;

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .get("/places")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize));
        }
    }

    @Nested
    class getAllPreviewPlaceByOrganizationId {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Place place = PlaceFixture.create(organization);
            placeJpaRepository.save(place);

            PlaceDetail placeDetail = PlaceDetailFixture.create(place);
            placeDetailJpaRepository.save(placeDetail);

            int representativeSequence = 1;

            PlaceImage placeImage = PlaceImageFixture.create(place, representativeSequence);
            placeImageJpaRepository.save(placeImage);

            int expectedSize = 1;
            int expectedFieldSize = 6;

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .get("/places/previews")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize))
                    .body("[0].size()", equalTo(expectedFieldSize))
                    .body("[0].id", equalTo(place.getId().intValue()))
                    .body("[0].imageUrl", equalTo(placeImage.getImageUrl()))
                    .body("[0].category", equalTo(place.getCategory().name()))
                    .body("[0].title", equalTo(placeDetail.getTitle()))
                    .body("[0].description", equalTo(placeDetail.getDescription()))
                    .body("[0].location", equalTo(placeDetail.getLocation()));
        }

        @Test
        void 성공_특정_조직의_모든_플레이스_리스트_조회() {
            // given
            Organization targetOrganization = OrganizationFixture.create();
            Organization anotherOrganization = OrganizationFixture.create();
            organizationJpaRepository.saveAll(List.of(targetOrganization, anotherOrganization));

            Place targetPlace1 = PlaceFixture.create(targetOrganization);
            Place targetPlace2 = PlaceFixture.create(targetOrganization);
            Place anotherPlace = PlaceFixture.create(anotherOrganization);
            placeJpaRepository.saveAll(List.of(targetPlace1, targetPlace2, anotherPlace));

            PlaceDetail targetPlaceDetail1 = PlaceDetailFixture.create(targetPlace1);
            PlaceDetail targetPlaceDetail2 = PlaceDetailFixture.create(targetPlace2);
            PlaceDetail anotherPlaceDetail = PlaceDetailFixture.create(anotherPlace);
            placeDetailJpaRepository.saveAll(List.of(targetPlaceDetail1, targetPlaceDetail2, anotherPlaceDetail));

            int representativeSequence = 1;
            PlaceImage placeImage1 = PlaceImageFixture.create(targetPlace1, representativeSequence);
            PlaceImage placeImage2 = PlaceImageFixture.create(targetPlace2, representativeSequence);
            PlaceImage placeImage3 = PlaceImageFixture.create(anotherPlace, representativeSequence);
            placeImageJpaRepository.saveAll(List.of(placeImage1, placeImage2, placeImage3));

            int expectedSize = 2;

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, targetOrganization.getId())
                    .when()
                    .get("/places/previews")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize));
        }

        @Test
        void 성공_대표_이미지가_없다면_null_반환() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Place place1 = PlaceFixture.create(organization);
            Place place2 = PlaceFixture.create(organization);
            placeJpaRepository.saveAll(List.of(place1, place2));

            PlaceDetail placeDetail1 = PlaceDetailFixture.create(place1);
            PlaceDetail placeDetail2 = PlaceDetailFixture.create(place2);
            placeDetailJpaRepository.saveAll(List.of(placeDetail1, placeDetail2));

            int representativeSequence = 1;
            int anotherSequence = 2;

            PlaceImage placeImage1 = PlaceImageFixture.create(place1, representativeSequence);
            PlaceImage placeImage2 = PlaceImageFixture.create(place2, anotherSequence);
            placeImageJpaRepository.saveAll(List.of(placeImage1, placeImage2));

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .get("/places/previews")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("[0].imageUrl", equalTo(placeImage1.getImageUrl()))
                    .body("[1].imageUrl", equalTo(null));
        }
    }

    @Nested
    class getPlaceWithDetailByPlaceId {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Place place = PlaceFixture.create(organization);
            placeJpaRepository.save(place);

            PlaceDetail placeDetail = PlaceDetailFixture.create(place);
            placeDetailJpaRepository.save(placeDetail);

            int representativeSequence = 1;

            PlaceImage placeImage1 = PlaceImageFixture.create(place, representativeSequence);
            PlaceImage placeImage2 = PlaceImageFixture.create(place, representativeSequence);
            placeImageJpaRepository.saveAll(List.of(placeImage1, placeImage2));

            PlaceAnnouncement placeAnnouncement1 = PlaceAnnouncementFixture.create(place);
            PlaceAnnouncement placeAnnouncement2 = PlaceAnnouncementFixture.create(place);
            placeAnnouncementJpaRepository.saveAll(List.of(placeAnnouncement1, placeAnnouncement2));

            int expectedFieldSize = 10;
            int expectedPlaceImagesSize = 2;
            int expectedPlaceAnnouncementsSize = 2;

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .get("/places/{placeId}", place.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("id", equalTo(place.getId().intValue()))
                    .body("placeImages", hasSize(expectedPlaceImagesSize))
                    .body("placeImages[0].id", equalTo(placeImage1.getId().intValue()))
                    .body("placeImages[0].imageUrl", equalTo(placeImage1.getImageUrl()))
                    .body("placeImages[0].sequence", equalTo(placeImage1.getSequence()))
                    .body("placeImages[1].id", equalTo(placeImage2.getId().intValue()))
                    .body("placeImages[1].imageUrl", equalTo(placeImage2.getImageUrl()))
                    .body("placeImages[1].sequence", equalTo(placeImage1.getSequence()))
                    .body("category", equalTo(place.getCategory().name()))
                    .body("title", equalTo(placeDetail.getTitle()))
                    .body("startTime", equalTo(placeDetail.getStartTime().toString()))
                    .body("endTime", equalTo(placeDetail.getEndTime().toString()))
                    .body("location", equalTo(placeDetail.getLocation()))
                    .body("host", equalTo(placeDetail.getHost()))
                    .body("description", equalTo(placeDetail.getDescription()))
                    .body("placeAnnouncements", hasSize(expectedPlaceAnnouncementsSize))
                    .body("placeAnnouncements[0].id", equalTo(placeAnnouncement1.getId().intValue()))
                    .body("placeAnnouncements[0].title", equalTo(placeAnnouncement1.getTitle()))
                    .body("placeAnnouncements[0].content", equalTo(placeAnnouncement1.getContent()))
                    .body("placeAnnouncements[0].createdAt", notNullValue())
                    .body("placeAnnouncements[1].id", equalTo(placeAnnouncement2.getId().intValue()))
                    .body("placeAnnouncements[1].title", equalTo(placeAnnouncement2.getTitle()))
                    .body("placeAnnouncements[1].content", equalTo(placeAnnouncement2.getContent()))
                    .body("placeAnnouncements[1].createdAt", notNullValue());
        }

        @Test
        void 성공_이미지_오름차순_정렬() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Place place = PlaceFixture.create(organization);
            placeJpaRepository.save(place);

            PlaceDetail placeDetail = PlaceDetailFixture.create(place);
            placeDetailJpaRepository.save(placeDetail);

            PlaceImage placeImage5 = PlaceImageFixture.create(place, 5);
            PlaceImage placeImage4 = PlaceImageFixture.create(place, 4);
            PlaceImage placeImage3 = PlaceImageFixture.create(place, 3);
            PlaceImage placeImage2 = PlaceImageFixture.create(place, 2);
            PlaceImage placeImage1 = PlaceImageFixture.create(place, 1);
            placeImageJpaRepository.saveAll(List.of(placeImage5, placeImage4, placeImage3, placeImage2, placeImage1));

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .get("/places/{placeId}", place.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("placeImages[0].sequence", equalTo(placeImage1.getSequence()))
                    .body("placeImages[1].sequence", equalTo(placeImage2.getSequence()))
                    .body("placeImages[2].sequence", equalTo(placeImage3.getSequence()))
                    .body("placeImages[3].sequence", equalTo(placeImage4.getSequence()))
                    .body("placeImages[4].sequence", equalTo(placeImage5.getSequence()));
        }

        @Test
        void 성공_이미지가_없는_경우_빈_배열_반환() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Place place = PlaceFixture.create(organization);
            placeJpaRepository.save(place);

            PlaceDetail placeDetail = PlaceDetailFixture.create(place);
            placeDetailJpaRepository.save(placeDetail);

            PlaceAnnouncement placeAnnouncement = PlaceAnnouncementFixture.create(place);
            placeAnnouncementJpaRepository.save(placeAnnouncement);

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .get("/places/{placeId}", place.getId())
                    .then()
                    .log()
                    .all()
                    .statusCode(HttpStatus.OK.value())
                    .body("placeImages", hasSize(0));
        }

        @Test
        void 성공_공지가_없는_경우_빈_배열_반환() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Place place = PlaceFixture.create(organization);
            placeJpaRepository.save(place);

            PlaceDetail placeDetail = PlaceDetailFixture.create(place);
            placeDetailJpaRepository.save(placeDetail);

            PlaceImage placeImage = PlaceImageFixture.create(place);
            placeImageJpaRepository.save(placeImage);

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .get("/places/{placeId}", place.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("placeAnnouncements", hasSize(0));
        }

        @Test
        void 실패_placeDetail이_존재하지_않는_place_id() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Long placeId = 0L;
            int expectedFieldSize = 1;

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .get("/places/{placeId}", placeId)
                    .then()
                    .log()
                    .all()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("message", equalTo("존재하지 않는 플레이스입니다."));
        }
    }
}
