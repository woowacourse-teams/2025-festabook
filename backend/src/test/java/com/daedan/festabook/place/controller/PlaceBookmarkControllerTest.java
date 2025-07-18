package com.daedan.festabook.place.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.daedan.festabook.device.domain.Device;
import com.daedan.festabook.device.domain.DeviceFixture;
import com.daedan.festabook.device.infrastructure.DeviceJpaRepository;
import com.daedan.festabook.notification.service.NotificationService;
import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceBookmark;
import com.daedan.festabook.place.domain.PlaceBookmarkFixture;
import com.daedan.festabook.place.domain.PlaceFixture;
import com.daedan.festabook.place.dto.PlaceBookmarkRequest;
import com.daedan.festabook.place.infrastructure.PlaceBookmarkJpaRepository;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PlaceBookmarkControllerTest {

    private static final String ORGANIZATION_HEADER_NAME = "organization";

    @Autowired
    private OrganizationJpaRepository organizationJpaRepository;

    @Autowired
    private DeviceJpaRepository deviceJpaRepository;

    @Autowired
    private PlaceJpaRepository placeJpaRepository;

    @Autowired
    private PlaceBookmarkJpaRepository placeBookmarkJpaRepository;

    @MockitoBean
    private NotificationService notificationService;

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    class createPlaceBookmark {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Device device = DeviceFixture.create();
            deviceJpaRepository.save(device);

            Place place = PlaceFixture.create(organization);
            placeJpaRepository.save(place);

            PlaceBookmarkRequest request = new PlaceBookmarkRequest(device.getId());

            int expectedFieldSize = 1;

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/places/bookmarks/" + place.getId())
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("id", notNullValue());
        }

        @Test
        void 예외_존재하지_않는_디바이스() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Place place = PlaceFixture.create(organization);
            placeJpaRepository.save(place);

            Long invalidDeviceId = 0L;

            PlaceBookmarkRequest request = new PlaceBookmarkRequest(invalidDeviceId);

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/places/bookmarks/" + place.getId())
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        void 예외_존재하지_않는_플레이스() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Device device = DeviceFixture.create();
            deviceJpaRepository.save(device);

            PlaceBookmarkRequest request = new PlaceBookmarkRequest(device.getId());

            Long invalidPlaceId = 0L;

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/places/bookmarks/" + invalidPlaceId)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    @Nested
    class deletePlaceBookmark {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Place place = PlaceFixture.create(organization);
            placeJpaRepository.save(place);

            Device device = DeviceFixture.create();
            deviceJpaRepository.save(device);

            PlaceBookmark placeBookmark = PlaceBookmarkFixture.create(place, device);
            placeBookmarkJpaRepository.save(placeBookmark);

            PlaceBookmarkRequest request = new PlaceBookmarkRequest(device.getId());

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .delete("/places/bookmarks/" + place.getId())
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        }

        @Test
        void 예외_존재하지_않는_디바이스() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Place place = PlaceFixture.create(organization);
            placeJpaRepository.save(place);

            Long invalidDeviceId = 0L;

            PlaceBookmarkRequest request = new PlaceBookmarkRequest(invalidDeviceId);

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .delete("/places/bookmarks/" + place.getId())
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }
}
