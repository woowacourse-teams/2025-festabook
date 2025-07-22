package com.daedan.festabook.organization.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;

import com.daedan.festabook.device.domain.Device;
import com.daedan.festabook.device.domain.DeviceFixture;
import com.daedan.festabook.device.infrastructure.DeviceJpaRepository;
import com.daedan.festabook.notification.infrastructure.FcmNotificationManager;
import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationBookmark;
import com.daedan.festabook.organization.domain.OrganizationBookmarkFixture;
import com.daedan.festabook.organization.domain.OrganizationBookmarkRequestFixture;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import com.daedan.festabook.organization.dto.OrganizationBookmarkRequest;
import com.daedan.festabook.organization.infrastructure.OrganizationBookmarkJpaRepository;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
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
class OrganizationBookmarkControllerTest {

    @Autowired
    private OrganizationJpaRepository organizationJpaRepository;

    @Autowired
    private DeviceJpaRepository deviceJpaRepository;

    @Autowired
    private OrganizationBookmarkJpaRepository organizationBookmarkJpaRepository;

    @MockitoBean
    private FcmNotificationManager fcmNotificationManager;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    class createOrganizationBookmark {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Device device = DeviceFixture.create();
            deviceJpaRepository.save(device);

            OrganizationBookmarkRequest request = OrganizationBookmarkRequestFixture.create(device.getId());

            int expectedFieldSize = 1;

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/organizations/{organizationId}/bookmarks", organization.getId())
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("id", notNullValue());

            then(fcmNotificationManager).should()
                    .subscribeOrganizationTopic(any(), any());
        }

        @Test
        void 예외_이미_북마크한_조직() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Device device = DeviceFixture.create();
            deviceJpaRepository.save(device);

            OrganizationBookmark organizationBookmark = OrganizationBookmarkFixture.create(organization, device);
            organizationBookmarkJpaRepository.save(organizationBookmark);

            OrganizationBookmarkRequest request = OrganizationBookmarkRequestFixture.create(device.getId());

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/organizations/{organizationId}/bookmarks", organization.getId())
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", equalTo("이미 북마크한 조직입니다."));

            then(fcmNotificationManager).shouldHaveNoInteractions();
        }

        @Test
        void 예외_존재하지_않는_디바이스() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Long invalidDeviceId = 0L;
            OrganizationBookmarkRequest request = OrganizationBookmarkRequestFixture.create(invalidDeviceId);

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/organizations/{organizationId}/bookmarks", organization.getId())
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", equalTo("존재하지 않는 디바이스입니다."));

            then(fcmNotificationManager).shouldHaveNoInteractions();
        }

        @Test
        void 예외_존재하지_않는_조직() {
            // given
            Device device = DeviceFixture.create();
            deviceJpaRepository.save(device);

            OrganizationBookmarkRequest request = OrganizationBookmarkRequestFixture.create(device.getId());

            Long invalidOrganizationId = 0L;

            // when & then
            RestAssured.
                    given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/organizations/{organizationId}/bookmarks", invalidOrganizationId)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", equalTo("존재하지 않는 조직입니다."));

            then(fcmNotificationManager).shouldHaveNoInteractions();
        }
    }

    @Nested
    class deleteOrganizationBookmark {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Device device = DeviceFixture.create();
            deviceJpaRepository.save(device);

            OrganizationBookmark organizationBookmark = OrganizationBookmarkFixture.create(organization, device);
            organizationBookmarkJpaRepository.save(organizationBookmark);

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .when()
                    .delete("/organizations/bookmarks/" + organizationBookmark.getId())
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            boolean exists = organizationBookmarkJpaRepository.existsById(organizationBookmark.getId());
            assertThat(exists).isFalse();
            then(fcmNotificationManager).should()
                    .unsubscribeOrganizationTopic(any(), any());

        }

        @Test
        void 성공_북마크_삭제시_조직_북마크가_존재하지_않아도_정상_처리() {
            // given
            Long invalidOrganizationBookmarkId = 0L;

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .when()
                    .delete("/organizations/bookmarks/" + invalidOrganizationBookmarkId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            then(fcmNotificationManager).shouldHaveNoInteractions();
        }
    }
}
