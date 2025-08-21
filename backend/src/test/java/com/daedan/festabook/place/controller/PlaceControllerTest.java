package com.daedan.festabook.place.controller;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.daedan.festabook.device.domain.Device;
import com.daedan.festabook.device.domain.DeviceFixture;
import com.daedan.festabook.device.infrastructure.DeviceJpaRepository;
import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.global.security.JwtTestHelper;
import com.daedan.festabook.global.infrastructure.ShuffleManager;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceAnnouncement;
import com.daedan.festabook.place.domain.PlaceAnnouncementFixture;
import com.daedan.festabook.place.domain.PlaceCategory;
import com.daedan.festabook.place.domain.PlaceFavorite;
import com.daedan.festabook.place.domain.PlaceFavoriteFixture;
import com.daedan.festabook.place.domain.PlaceFixture;
import com.daedan.festabook.place.domain.PlaceImage;
import com.daedan.festabook.place.domain.PlaceImageFixture;
import com.daedan.festabook.place.dto.PlaceRequest;
import com.daedan.festabook.place.dto.PlaceRequestFixture;
import com.daedan.festabook.place.dto.PlaceUpdateRequest;
import com.daedan.festabook.place.dto.PlaceUpdateRequestFixture;
import com.daedan.festabook.place.infrastructure.PlaceAnnouncementJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceFavoriteJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceImageJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceJpaRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PlaceControllerTest {

    private static final String FESTIVAL_HEADER_NAME = "festival";

    @Autowired
    private FestivalJpaRepository festivalJpaRepository;

    @Autowired
    private PlaceJpaRepository placeJpaRepository;

    @Autowired
    private PlaceAnnouncementJpaRepository placeAnnouncementJpaRepository;

    @Autowired
    private PlaceImageJpaRepository placeImageJpaRepository;

    @Autowired
    private PlaceFavoriteJpaRepository placeFavoriteJpaRepository;

    @Autowired
    private DeviceJpaRepository deviceJpaRepository;

    @Autowired
    private JwtTestHelper jwtTestHelper;
  
    @MockitoBean
    private ShuffleManager shuffleManager;

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
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createAuthorizationHeader(festival);

            PlaceCategory expectedPlaceCategory = PlaceCategory.BAR;
            String expectedPlaceTitle = "동문 주차장";
            PlaceRequest placeRequest = PlaceRequestFixture.create(expectedPlaceCategory, expectedPlaceTitle);

            int expectedFieldSize = 10;

            // when & then
            RestAssured
                    .given()
                    .header(FESTIVAL_HEADER_NAME, festival.getId())
                    .header(authorizationHeader)
                    .contentType(ContentType.JSON)
                    .body(placeRequest)
                    .post("/places")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("placeId", notNullValue())
                    .body("category", equalTo(expectedPlaceCategory.toString()))
                    .body("title", equalTo(expectedPlaceTitle))

                    .body("placeImages", empty())
                    .body("placeAnnouncements", empty())

                    .body("startTime", nullValue())
                    .body("endTime", nullValue())
                    .body("location", nullValue())
                    .body("host", nullValue())
                    .body("description", nullValue());
        }
    }

    @Nested
    class updatePlace {

        @Test
        void 성공() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createAuthorizationHeader(festival);

            Place place = PlaceFixture.create(festival);
            placeJpaRepository.save(place);

            PlaceUpdateRequest placeUpdateRequest = PlaceUpdateRequestFixture.create(
                    PlaceCategory.FOOD_TRUCK,
                    "수정된 제목",
                    "수정된 설명",
                    "수정된 위치",
                    "수정된 호스트",
                    LocalTime.of(12, 37),
                    LocalTime.of(13, 11)
            );

            int expectedFieldSize = 7;

            // when & then
            RestAssured
                    .given()
                    .header(authorizationHeader)
                    .contentType(ContentType.JSON)
                    .body(placeUpdateRequest)
                    .patch("/places/{placeId}", place.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("placeCategory", equalTo(placeUpdateRequest.placeCategory().name()))
                    .body("title", equalTo(placeUpdateRequest.title()))
                    .body("description", equalTo(placeUpdateRequest.description()))
                    .body("location", equalTo(placeUpdateRequest.location()))
                    .body("host", equalTo(placeUpdateRequest.host()))
                    .body("startTime", equalTo(placeUpdateRequest.startTime().toString()))
                    .body("endTime", equalTo(placeUpdateRequest.endTime().toString()));
        }
    }

    @Nested
    class getAllPlaceByFestivalId {

        @Test
        void 성공() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Place mainPlace = PlaceFixture.create(festival);
            placeJpaRepository.save(mainPlace);

            Place etcPlace = PlaceFixture.create(festival);
            placeJpaRepository.save(etcPlace);

            int expectedSize = 2;

            // when & then
            RestAssured
                    .given()
                    .header(FESTIVAL_HEADER_NAME, festival.getId())
                    .get("/places")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize));
        }

        @Test
        void 성공_MainPlace인_경우() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Place mainPlace = PlaceFixture.create(festival);
            placeJpaRepository.save(mainPlace);

            int expectedSize = 1;

            // when & then
            RestAssured
                    .given()
                    .header(FESTIVAL_HEADER_NAME, festival.getId())
                    .get("/places")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize));
        }

        @Test
        void 성공_EtcPlace인_경우() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Place etcPlace = PlaceFixture.create(festival);
            placeJpaRepository.save(etcPlace);

            int expectedSize = 1;

            // when & then
            RestAssured
                    .given()
                    .header(FESTIVAL_HEADER_NAME, festival.getId())
                    .get("/places")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize));
        }
    }

    @Nested
    class getAllPreviewPlaceByFestivalId {

        @Test
        void 성공() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Place place = PlaceFixture.create(festival);
            placeJpaRepository.save(place);

            int representativeSequence = 1;

            PlaceImage placeImage = PlaceImageFixture.create(place, representativeSequence);
            placeImageJpaRepository.save(placeImage);

            given(shuffleManager.getShuffledList(anyList()))
                    .willReturn(List.of(place));

            int expectedSize = 1;
            int expectedFieldSize = 6;

            // when & then
            RestAssured
                    .given()
                    .header(FESTIVAL_HEADER_NAME, festival.getId())
                    .when()
                    .get("/places/previews")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize))
                    .body("[0].size()", equalTo(expectedFieldSize))
                    .body("[0].placeId", equalTo(place.getId().intValue()))
                    .body("[0].imageUrl", equalTo(placeImage.getImageUrl()))
                    .body("[0].category", equalTo(place.getCategory().name()))
                    .body("[0].title", equalTo(place.getTitle()))
                    .body("[0].description", equalTo(place.getDescription()))
                    .body("[0].location", equalTo(place.getLocation()));
            then(shuffleManager).should()
                    .getShuffledList(anyList());
        }

        @Test
        void 성공_특정_축제의_모든_플레이스_리스트_조회() {
            // given
            Festival targetFestival = FestivalFixture.create();
            Festival anotherFestival = FestivalFixture.create();
            festivalJpaRepository.saveAll(List.of(targetFestival, anotherFestival));

            Place targetPlace1 = PlaceFixture.create(targetFestival);
            Place targetPlace2 = PlaceFixture.create(targetFestival);
            Place anotherPlace = PlaceFixture.create(anotherFestival);
            placeJpaRepository.saveAll(List.of(targetPlace1, targetPlace2, anotherPlace));

            given(shuffleManager.getShuffledList(anyList()))
                    .willReturn(List.of(targetPlace1, targetPlace2));

            int representativeSequence = 1;
            PlaceImage placeImage1 = PlaceImageFixture.create(targetPlace1, representativeSequence);
            PlaceImage placeImage2 = PlaceImageFixture.create(targetPlace2, representativeSequence);
            PlaceImage placeImage3 = PlaceImageFixture.create(anotherPlace, representativeSequence);
            placeImageJpaRepository.saveAll(List.of(placeImage1, placeImage2, placeImage3));

            int expectedSize = 2;

            // when & then
            RestAssured
                    .given()
                    .header(FESTIVAL_HEADER_NAME, targetFestival.getId())
                    .when()
                    .get("/places/previews")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize));
            then(shuffleManager).should()
                    .getShuffledList(anyList());
        }

        @Test
        void 성공_대표_이미지가_없다면_null_반환() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Place place1 = PlaceFixture.create(festival);
            Place place2 = PlaceFixture.create(festival);
            placeJpaRepository.saveAll(List.of(place1, place2));

            int representativeSequence = 1;
            int anotherSequence = 2;

            PlaceImage placeImage1 = PlaceImageFixture.create(place1, representativeSequence);
            PlaceImage placeImage2 = PlaceImageFixture.create(place2, anotherSequence);
            placeImageJpaRepository.saveAll(List.of(placeImage1, placeImage2));

            given(shuffleManager.getShuffledList(anyList()))
                    .willReturn(List.of(place1, place2));

            // when & then
            RestAssured
                    .given()
                    .header(FESTIVAL_HEADER_NAME, festival.getId())
                    .when()
                    .get("/places/previews")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("[0].imageUrl", equalTo(placeImage1.getImageUrl()))
                    .body("[1].imageUrl", equalTo(null));
            then(shuffleManager).should()
                    .getShuffledList(anyList());
        }
    }

    @Nested
    class getPlaceByPlaceId {

        @Test
        void 성공() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Place place = PlaceFixture.create(festival);
            placeJpaRepository.save(place);

            int representativeSequence1 = 1;
            int representativeSequence2 = 2;

            PlaceImage placeImage1 = PlaceImageFixture.create(place, representativeSequence1);
            PlaceImage placeImage2 = PlaceImageFixture.create(place, representativeSequence2);
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
                    .header(FESTIVAL_HEADER_NAME, festival.getId())
                    .when()
                    .get("/places/{placeId}", place.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("placeId", equalTo(place.getId().intValue()))
                    .body("placeImages", hasSize(expectedPlaceImagesSize))
                    .body("placeImages[0].id", equalTo(placeImage1.getId().intValue()))
                    .body("placeImages[0].imageUrl", equalTo(placeImage1.getImageUrl()))
                    .body("placeImages[0].sequence", equalTo(placeImage1.getSequence()))
                    .body("placeImages[1].id", equalTo(placeImage2.getId().intValue()))
                    .body("placeImages[1].imageUrl", equalTo(placeImage2.getImageUrl()))
                    .body("placeImages[1].sequence", equalTo(placeImage2.getSequence()))

                    .body("category", equalTo(place.getCategory().name()))
                    .body("title", equalTo(place.getTitle()))
                    .body("startTime", equalTo(place.getStartTime().toString()))
                    .body("endTime", equalTo(place.getEndTime().toString()))
                    .body("location", equalTo(place.getLocation()))
                    .body("host", equalTo(place.getHost()))
                    .body("description", equalTo(place.getDescription()))

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
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Place place = PlaceFixture.create(festival);
            placeJpaRepository.save(place);

            PlaceImage placeImage5 = PlaceImageFixture.create(place, 5);
            PlaceImage placeImage4 = PlaceImageFixture.create(place, 4);
            PlaceImage placeImage3 = PlaceImageFixture.create(place, 3);
            PlaceImage placeImage2 = PlaceImageFixture.create(place, 2);
            PlaceImage placeImage1 = PlaceImageFixture.create(place, 1);
            placeImageJpaRepository.saveAll(List.of(placeImage5, placeImage4, placeImage3, placeImage2, placeImage1));

            // when & then
            RestAssured
                    .given()
                    .header(FESTIVAL_HEADER_NAME, festival.getId())
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
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Place place = PlaceFixture.create(festival);
            placeJpaRepository.save(place);

            PlaceAnnouncement placeAnnouncement = PlaceAnnouncementFixture.create(place);
            placeAnnouncementJpaRepository.save(placeAnnouncement);

            // when & then
            RestAssured
                    .given()
                    .header(FESTIVAL_HEADER_NAME, festival.getId())
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
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Place place = PlaceFixture.create(festival);
            placeJpaRepository.save(place);

            PlaceImage placeImage = PlaceImageFixture.create(place);
            placeImageJpaRepository.save(placeImage);

            // when & then
            RestAssured
                    .given()
                    .header(FESTIVAL_HEADER_NAME, festival.getId())
                    .when()
                    .get("/places/{placeId}", place.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("placeAnnouncements", hasSize(0));
        }
    }

    @Nested
    class deleteByPlaceId {

        @Test
        void 성공() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createAuthorizationHeader(festival);

            Place place = PlaceFixture.create(festival);
            placeJpaRepository.save(place);

            Device device1 = DeviceFixture.create();
            Device device2 = DeviceFixture.create();
            deviceJpaRepository.saveAll(List.of(device1, device2));

            PlaceImage placeImage1 = PlaceImageFixture.create(place);
            PlaceImage placeImage2 = PlaceImageFixture.create(place);
            placeImageJpaRepository.saveAll(List.of(placeImage1, placeImage2));

            PlaceAnnouncement placeAnnouncement1 = PlaceAnnouncementFixture.create(place);
            PlaceAnnouncement placeAnnouncement2 = PlaceAnnouncementFixture.create(place);
            placeAnnouncementJpaRepository.saveAll(List.of(placeAnnouncement1, placeAnnouncement2));

            PlaceFavorite placeFavorite1 = PlaceFavoriteFixture.create(place, device1);
            PlaceFavorite placeFavorite2 = PlaceFavoriteFixture.create(place, device2);
            placeFavoriteJpaRepository.saveAll(List.of(placeFavorite1, placeFavorite2));

            // when & then
            RestAssured
                    .given()
                    .header(authorizationHeader)
                    .header(FESTIVAL_HEADER_NAME, festival.getId())
                    .when()
                    .delete("/places/{placeId}", place.getId())
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            assertSoftly(s -> {
                s.assertThat(placeJpaRepository.findById(place.getId())).isEmpty();

                s.assertThat(placeImageJpaRepository.findById(placeImage1.getId())).isEmpty();
                s.assertThat(placeImageJpaRepository.findById(placeImage2.getId())).isEmpty();

                s.assertThat(placeAnnouncementJpaRepository.findById(placeAnnouncement1.getId())).isEmpty();
                s.assertThat(placeAnnouncementJpaRepository.findById(placeAnnouncement2.getId())).isEmpty();

                s.assertThat(placeFavoriteJpaRepository.findById(placeFavorite1.getId())).isEmpty();
                s.assertThat(placeFavoriteJpaRepository.findById(placeFavorite2.getId())).isEmpty();
            });
        }

        @Test
        void 성공_place가_존재하지_않는_경우_204를_응답() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createAuthorizationHeader(festival);

            Long invalidPlaceId = 0L;

            // when & then
            RestAssured
                    .given()
                    .header(authorizationHeader)
                    .header(FESTIVAL_HEADER_NAME, festival.getId())
                    .when()
                    .delete("/places/{placeId}", invalidPlaceId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        }
    }
}
