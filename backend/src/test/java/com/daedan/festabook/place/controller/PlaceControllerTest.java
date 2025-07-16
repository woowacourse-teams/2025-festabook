package com.daedan.festabook.place.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceAnnouncement;
import com.daedan.festabook.place.domain.PlaceAnnouncementFixture;
import com.daedan.festabook.place.domain.PlaceFixture;
import com.daedan.festabook.place.domain.PlaceImage;
import com.daedan.festabook.place.domain.PlaceImageFixture;
import com.daedan.festabook.place.infrastructure.PlaceAnnouncementJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceImageJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceJpaRepository;
import io.restassured.RestAssured;
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
    class getAllPlaceByOrganizationId {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Place place = PlaceFixture.create(organization);
            placeJpaRepository.save(place);

            int expectedSize = 1;
            int expectedFieldSize = 8;

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .get("/places")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize))
                    .body("[0].size()", equalTo(expectedFieldSize))
                    .body("[0].id", equalTo(place.getId().intValue()))
                    .body("[0].title", equalTo(place.getTitle()))
                    .body("[0].description", equalTo(place.getDescription()))
                    .body("[0].category", equalTo(place.getCategory().name()))
                    .body("[0].location", equalTo(place.getLocation()))
                    .body("[0].host", equalTo(place.getHost()))
                    .body("[0].startTime", equalTo(place.getStartTime().toString()))
                    .body("[0].endTime", equalTo(place.getEndTime().toString()));
        }

        @Test
        void 성공_특정_조직의_모든_플레이스_조회() {
            // given
            Organization targetOrganization = OrganizationFixture.create();
            Organization anotherOrganization = OrganizationFixture.create();
            organizationJpaRepository.saveAll(List.of(targetOrganization, anotherOrganization));

            List<Place> places = List.of(
                    PlaceFixture.create(targetOrganization),
                    PlaceFixture.create(targetOrganization),
                    PlaceFixture.create(anotherOrganization)
            );
            placeJpaRepository.saveAll(places);

            int expectedSize = 2;

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, targetOrganization.getId())
                    .when()
                    .get("/places")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize));
        }
    }

    @Nested
    class getAllPlaceAnnouncementByPlaceId {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Place place = PlaceFixture.create(organization);
            placeJpaRepository.save(place);

            PlaceAnnouncement placeAnnouncement = PlaceAnnouncementFixture.create(place);
            placeAnnouncementJpaRepository.save(placeAnnouncement);

            int expectedSize = 1;
            int expectedFieldSize = 4;

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .get("/places/{placeId}/announcements", place.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize))
                    .body("[0].size()", equalTo(expectedFieldSize))
                    .body("[0].id", equalTo(placeAnnouncement.getId().intValue()))
                    .body("[0].title", equalTo(placeAnnouncement.getTitle()))
                    .body("[0].content", equalTo(placeAnnouncement.getContent()))
                    .body("[0].createdAt", notNullValue());
        }

        @Test
        void 성공_특정_플레이스의_모든_공지_조회() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Place targetPlace = PlaceFixture.create(organization);
            Place anotherPlace = PlaceFixture.create(organization);
            placeJpaRepository.saveAll(List.of(targetPlace, anotherPlace));

            List<PlaceAnnouncement> placeAnnouncements = List.of(
                    PlaceAnnouncementFixture.create(targetPlace),
                    PlaceAnnouncementFixture.create(targetPlace),
                    PlaceAnnouncementFixture.create(anotherPlace)
            );
            placeAnnouncementJpaRepository.saveAll(placeAnnouncements);

            int expectedSize = 2;

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .get("/places/{placeId}/announcements", targetPlace.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize));
        }
    }

    @Nested
    class getAllPlaceImageByPlaceId {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Place place = PlaceFixture.create(organization);
            placeJpaRepository.save(place);

            PlaceImage placeImage = PlaceImageFixture.create(place);
            placeImageJpaRepository.save(placeImage);

            int expectedSize = 1;
            int expectedFieldSize = 2;

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .get("/places/{placeId}/images", place.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize))
                    .body("[0].size()", equalTo(expectedFieldSize))
                    .body("[0].id", equalTo(placeImage.getId().intValue()))
                    .body("[0].imageUrl", equalTo(placeImage.getImageUrl()));
        }

        @Test
        void 성공_특정_플레이스의_모든_이미지_조회() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Place targetPlace = PlaceFixture.create(organization);
            Place anotherPlace = PlaceFixture.create(organization);
            placeJpaRepository.saveAll(List.of(targetPlace, anotherPlace));

            List<PlaceImage> placeImages = List.of(
                    PlaceImageFixture.create(targetPlace),
                    PlaceImageFixture.create(targetPlace),
                    PlaceImageFixture.create(anotherPlace)
            );
            placeImageJpaRepository.saveAll(placeImages);

            int expectedSize = 2;

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .get("/places/{placeId}/images", targetPlace.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize));
        }
    }
}
