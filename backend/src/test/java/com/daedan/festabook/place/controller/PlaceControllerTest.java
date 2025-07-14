package com.daedan.festabook.place.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import com.daedan.festabook.orgnaization.domain.OrganizationFixture;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceAnnouncement;
import com.daedan.festabook.place.domain.PlaceCategory;
import com.daedan.festabook.place.domain.PlaceFixture;
import com.daedan.festabook.place.domain.PlaceImage;
import com.daedan.festabook.place.infrastructure.PlaceAnnouncementJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceImageJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceJpaRepository;
import io.restassured.RestAssured;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class PlaceControllerTest {

    private static final String ORGANIZATION_HEADER_NAME = "organization";

    @LocalServerPort
    private int port;

    @Autowired
    private OrganizationJpaRepository organizationJpaRepository;

    @Autowired
    private PlaceJpaRepository placeJpaRepository;

    @Autowired
    private PlaceAnnouncementJpaRepository placeAnnouncementJpaRepository;

    @Autowired
    private PlaceImageJpaRepository placeImageJpaRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    class getAllPlaceByOrganizationId {

        @Test
        void 성공_모든_플레이스_조회() {
            // given
            Organization organization = organizationJpaRepository.save(OrganizationFixture.create());
            Place expected = placeJpaRepository.save(new Place(
                    organization,
                    "코딩하면 한잔",
                    "시원한 맥주와 시원한 치킨!",
                    PlaceCategory.BAR,
                    "공학관 앞",
                    "C블C블",
                    LocalTime.of(9, 0),
                    LocalTime.of(18, 0)
            ));

            // when & then
            RestAssured.given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .get("/places")
                    .then()
                    .statusCode(200)
                    .body("[0].id", equalTo(expected.getId().intValue()))
                    .body("[0].title", equalTo(expected.getTitle()))
                    .body("[0].description", equalTo(expected.getDescription()))
                    .body("[0].category", equalTo(expected.getCategory().name()))
                    .body("[0].location", equalTo(expected.getLocation()))
                    .body("[0].host", equalTo(expected.getHost()))
                    .body("[0].startTime", equalTo(expected.getStartTime().toString()))
                    .body("[0].endTime", equalTo(expected.getEndTime().toString()));
        }
    }

    @Nested
    class getAllPlaceAnnouncementByPlaceId {

        @Test
        void 성공_특정_플레이스의_모든_공지_조회() {
            // given
            Organization organization = organizationJpaRepository.save(OrganizationFixture.create());
            Place place = placeJpaRepository.save(PlaceFixture.create(organization));
            PlaceAnnouncement expected = placeAnnouncementJpaRepository.save(new PlaceAnnouncement(
                    place,
                    "치킨 재고 소진되었습니다.",
                    "앞으로 더 좋은 주점으로 찾아뵙겠습니다."
            ));

            // when & then
            RestAssured.given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .get("/places/" + place.getId() + "/announcements")
                    .then()
                    .statusCode(200)
                    .body("[0].id", equalTo(expected.getId().intValue()))
                    .body("[0].title", equalTo(expected.getTitle()))
                    .body("[0].content", equalTo(expected.getContent()))
                    .body("[0].createdAt", notNullValue());
        }
    }

    @Nested
    class getAllPlaceImageByPlaceId {

        @Test
        void 성공_특정_플레이스의_모든_이미지_조회() {
            // given
            Organization organization = organizationJpaRepository.save(OrganizationFixture.create());
            Place place = placeJpaRepository.save(PlaceFixture.create(organization));
            PlaceImage expected = placeImageJpaRepository.save(new PlaceImage(
                    place,
                    "https://example.com/image.jpg"
            ));

            // when & then
            RestAssured.given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .get("/places/" + place.getId() + "/images")
                    .then()
                    .statusCode(200)
                    .body("[0].id", equalTo(expected.getId().intValue()))
                    .body("[0].imageUrl", equalTo(expected.getImageUrl()));
        }
    }
}
