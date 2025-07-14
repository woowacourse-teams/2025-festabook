package com.daedan.festabook.place.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
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
import java.util.List;
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
        void 성공_특정_조직의_모든_플레이스_조회() {
            // given
            Organization organization = organizationJpaRepository.save(OrganizationFixture.create());
            List<Place> places = placeJpaRepository.saveAll(List.of(
                    new Place(
                            organization,
                            "코딩하며 한잔",
                            "시원한 맥주와 시원한 치킨!",
                            PlaceCategory.BAR,
                            "공학관 앞",
                            "C블C블",
                            LocalTime.of(9, 0),
                            LocalTime.of(18, 0)
                    ),
                    new Place(
                            organization,
                            "레트로 뽑기방",
                            "추억의 장난감과 인형을 뽑아가세요!",
                            PlaceCategory.BOOTH,
                            "학생회관 뒤편",
                            "응답하라 부스",
                            LocalTime.of(10, 0),
                            LocalTime.of(19, 0)
                    )
            ));

            // when & then
            RestAssured.given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .get("/places")
                    .then()
                    .statusCode(200)
                    .body("$", hasSize(places.size()))
                    .body("[0].id", equalTo(places.get(0).getId().intValue()))
                    .body("[0].title", equalTo(places.get(0).getTitle()))
                    .body("[0].description", equalTo(places.get(0).getDescription()))
                    .body("[0].category", equalTo(places.get(0).getCategory().name()))
                    .body("[0].location", equalTo(places.get(0).getLocation()))
                    .body("[0].host", equalTo(places.get(0).getHost()))
                    .body("[0].startTime", equalTo(places.get(0).getStartTime().toString()))
                    .body("[0].endTime", equalTo(places.get(0).getEndTime().toString()))
                    .body("[1].id", equalTo(places.get(1).getId().intValue()))
                    .body("[1].title", equalTo(places.get(1).getTitle()))
                    .body("[1].description", equalTo(places.get(1).getDescription()))
                    .body("[1].category", equalTo(places.get(1).getCategory().name()))
                    .body("[1].location", equalTo(places.get(1).getLocation()))
                    .body("[1].host", equalTo(places.get(1).getHost()))
                    .body("[1].startTime", equalTo(places.get(1).getStartTime().toString()))
                    .body("[1].endTime", equalTo(places.get(1).getEndTime().toString()));
        }
    }

    @Nested
    class getAllPlaceAnnouncementByPlaceId {

        @Test
        void 성공_특정_플레이스의_모든_공지_조회() {
            // given
            Organization organization = organizationJpaRepository.save(OrganizationFixture.create());
            Place place = placeJpaRepository.save(PlaceFixture.create(organization));
            List<PlaceAnnouncement> placeAnnouncements = placeAnnouncementJpaRepository.saveAll(List.of(
                    new PlaceAnnouncement(
                            place,
                            "치킨 재고 소진되었습니다.",
                            "앞으로 더 좋은 주점으로 찾아뵙겠습니다."
                    ),
                    new PlaceAnnouncement(
                            place,
                            "운영 시간이 변경되었습니다.",
                            "우천으로 인해 부스 운영을 오후 4시까지로 단축합니다. 양해 부탁드립니다."
                    )
            ));

            // when & then
            RestAssured.given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .get("/places/" + place.getId() + "/announcements")
                    .then()
                    .statusCode(200)
                    .body("$", hasSize(placeAnnouncements.size()))
                    .body("[0].id", equalTo(placeAnnouncements.get(0).getId().intValue()))
                    .body("[0].title", equalTo(placeAnnouncements.get(0).getTitle()))
                    .body("[0].content", equalTo(placeAnnouncements.get(0).getContent()))
                    .body("[0].createdAt", notNullValue())
                    .body("[1].id", equalTo(placeAnnouncements.get(1).getId().intValue()))
                    .body("[1].title", equalTo(placeAnnouncements.get(1).getTitle()))
                    .body("[1].content", equalTo(placeAnnouncements.get(1).getContent()))
                    .body("[1].createdAt", notNullValue());
        }
    }

    @Nested
    class getAllPlaceImageByPlaceId {

        @Test
        void 성공_특정_플레이스의_모든_이미지_조회() {
            // given
            Organization organization = organizationJpaRepository.save(OrganizationFixture.create());
            Place place = placeJpaRepository.save(PlaceFixture.create(organization));
            List<PlaceImage> placeImages = placeImageJpaRepository.saveAll(List.of(
                    new PlaceImage(
                            place,
                            "https://example.com/image1.jpg"
                    ),
                    new PlaceImage(
                            place,
                            "https://example.com/image2.jpg"
                    )
            ));

            // when & then
            RestAssured.given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .get("/places/" + place.getId() + "/images")
                    .then()
                    .statusCode(200)
                    .body("$", hasSize(placeImages.size()))
                    .body("[0].id", equalTo(placeImages.get(0).getId().intValue()))
                    .body("[0].imageUrl", equalTo(placeImages.get(0).getImageUrl()))
                    .body("[1].id", equalTo(placeImages.get(1).getId().intValue()))
                    .body("[1].imageUrl", equalTo(placeImages.get(1).getImageUrl()));
        }
    }
}
