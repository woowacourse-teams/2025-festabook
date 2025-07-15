package com.daedan.festabook.place.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import com.daedan.festabook.orgnaization.domain.OrganizationFixture;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceAnnouncement;
import com.daedan.festabook.place.domain.PlaceAnnouncementFixture;
import com.daedan.festabook.place.domain.PlaceCategory;
import com.daedan.festabook.place.domain.PlaceFixture;
import com.daedan.festabook.place.domain.PlaceImage;
import com.daedan.festabook.place.domain.PlaceImageFixture;
import com.daedan.festabook.place.infrastructure.PlaceAnnouncementJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceImageJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceJpaRepository;
import io.restassured.RestAssured;
import java.time.LocalTime;
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

            Place place = PlaceFixture.create(
                    organization,
                    "코딩하며 한잔",
                    "시원한 맥주와 시원한 치킨!",
                    PlaceCategory.BAR,
                    "공학관 앞",
                    "C블C블",
                    LocalTime.of(9, 0),
                    LocalTime.of(18, 0)
            );
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
            int expectedSize = 2;

            List<Organization> organizations = OrganizationFixture.createList(expectedSize);
            organizationJpaRepository.saveAll(organizations);

            List<Place> places = List.of(
                    PlaceFixture.create(organizations.get(0)),
                    PlaceFixture.create(organizations.get(0)),
                    PlaceFixture.create(organizations.get(1))
            );
            placeJpaRepository.saveAll(places);

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organizations.get(0).getId())
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

            PlaceAnnouncement placeAnnouncement = PlaceAnnouncementFixture.create(
                    place,
                    "치킨 재고 소진되었습니다.",
                    "앞으로 더 좋은 주점으로 찾아뵙겠습니다."
            );
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
            int expectedSize = 2;

            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            List<Place> places = PlaceFixture.createList(expectedSize, organization);
            placeJpaRepository.saveAll(places);

            List<PlaceAnnouncement> placeAnnouncements = List.of(
                    PlaceAnnouncementFixture.create(places.get(0)),
                    PlaceAnnouncementFixture.create(places.get(0)),
                    PlaceAnnouncementFixture.create(places.get(1))
            );
            placeAnnouncementJpaRepository.saveAll(placeAnnouncements);

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .get("/places/{placeId}/announcements", places.get(0).getId())
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

            PlaceImage placeImage = PlaceImageFixture.create(
                    place,
                    "https://example.com/image1.jpg"
            );
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
            int expectedSize = 2;

            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            List<Place> places = PlaceFixture.createList(expectedSize, organization);
            placeJpaRepository.saveAll(places);

            List<PlaceImage> placeImages = List.of(
                    PlaceImageFixture.create(places.get(0)),
                    PlaceImageFixture.create(places.get(0)),
                    PlaceImageFixture.create(places.get(1))
            );
            placeImageJpaRepository.saveAll(placeImages);

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .get("/places/{placeId}/images", places.get(0).getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize));
        }
    }
}
