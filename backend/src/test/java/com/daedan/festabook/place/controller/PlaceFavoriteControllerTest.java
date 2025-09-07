package com.daedan.festabook.place.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.daedan.festabook.device.domain.Device;
import com.daedan.festabook.device.domain.DeviceFixture;
import com.daedan.festabook.device.infrastructure.DeviceJpaRepository;
import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceFavorite;
import com.daedan.festabook.place.domain.PlaceFavoriteFixture;
import com.daedan.festabook.place.domain.PlaceFixture;
import com.daedan.festabook.place.dto.PlaceFavoriteRequest;
import com.daedan.festabook.place.dto.PlaceFavoriteRequestFixture;
import com.daedan.festabook.place.infrastructure.PlaceFavoriteJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceJpaRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
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
class PlaceFavoriteControllerTest {

    @Autowired
    private FestivalJpaRepository festivalJpaRepository;

    @Autowired
    private DeviceJpaRepository deviceJpaRepository;

    @Autowired
    private PlaceJpaRepository placeJpaRepository;

    @Autowired
    private PlaceFavoriteJpaRepository placeFavoriteJpaRepository;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    class addPlaceFavorite {

        @Test
        void 성공() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Device device = DeviceFixture.create();
            deviceJpaRepository.save(device);

            Place place = PlaceFixture.create(festival);
            placeJpaRepository.save(place);

            PlaceFavoriteRequest request = PlaceFavoriteRequestFixture.create(device.getId());

            int expectedFieldSize = 1;

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/places/{placeId}/favorites", place.getId())
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("placeFavoriteId", notNullValue());
        }

        @Test
        void 예외_플레이스에_이미_즐겨찾기한_디바이스() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Device device = DeviceFixture.create();
            deviceJpaRepository.save(device);

            Place place = PlaceFixture.create(festival);
            placeJpaRepository.save(place);

            PlaceFavorite placeFavorite = PlaceFavoriteFixture.create(place, device);
            placeFavoriteJpaRepository.save(placeFavorite);

            PlaceFavoriteRequest request = PlaceFavoriteRequestFixture.create(device.getId());

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/places/{placeId}/favorites", place.getId())
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", equalTo("이미 즐겨찾기한 플레이스입니다."));
        }

        @Test
        void 예외_존재하지_않는_디바이스() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Place place = PlaceFixture.create(festival);
            placeJpaRepository.save(place);

            Long invalidDeviceId = 0L;
            PlaceFavoriteRequest request = PlaceFavoriteRequestFixture.create(invalidDeviceId);

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/places/{placeId}/favorites", place.getId())
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", equalTo("존재하지 않는 디바이스입니다."));
        }

        @Test
        void 예외_존재하지_않는_플레이스() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Device device = DeviceFixture.create();
            deviceJpaRepository.save(device);

            PlaceFavoriteRequest request = PlaceFavoriteRequestFixture.create(device.getId());

            Long invalidPlaceId = 0L;

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/places/{placeId}/favorites", invalidPlaceId)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", equalTo("존재하지 않는 플레이스입니다."));
        }
    }

    @Nested
    class removePlaceFavorite {

        @Test
        void 성공() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Place place = PlaceFixture.create(festival);
            placeJpaRepository.save(place);

            Device device = DeviceFixture.create();
            deviceJpaRepository.save(device);

            PlaceFavorite placeFavorite = PlaceFavoriteFixture.create(place, device);
            placeFavoriteJpaRepository.save(placeFavorite);

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .when()
                    .delete("/places/favorites/{placeFavoriteId}", placeFavorite.getId())
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            boolean exists = placeFavoriteJpaRepository.existsById(placeFavorite.getId());
            assertThat(exists).isFalse();
        }

        @Test
        void 성공_즐겨찾기_삭제시_플레이스_즐겨찾기가_존재하지_않아도_정상_처리() {
            // given
            Long placeFavoriteId = 0L;

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .when()
                    .delete("/places/favorites/{placeFavoriteId}", placeFavoriteId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        }
    }
}
