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
import com.daedan.festabook.organization.domain.OrganizationFixture;
import com.daedan.festabook.organization.domain.OrganizationNotification;
import com.daedan.festabook.organization.domain.OrganizationNotificationFixture;
import com.daedan.festabook.organization.domain.OrganizationNotificationRequestFixture;
import com.daedan.festabook.organization.dto.OrganizationNotificationRequest;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import com.daedan.festabook.organization.infrastructure.OrganizationNotificationJpaRepository;
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
class OrganizationNotificationControllerTest {

    @Autowired
    private OrganizationJpaRepository organizationJpaRepository;

    @Autowired
    private DeviceJpaRepository deviceJpaRepository;

    @Autowired
    private OrganizationNotificationJpaRepository organizationNotificationJpaRepository;

    @MockitoBean
    private FcmNotificationManager fcmNotificationManager;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    class subscribeOrganizationNotification {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Device device = DeviceFixture.create();
            deviceJpaRepository.save(device);

            OrganizationNotificationRequest request = OrganizationNotificationRequestFixture.create(device.getId());

            int expectedFieldSize = 1;

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/organizations/{organizationId}/notifications", organization.getId())
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("id", notNullValue());

            then(fcmNotificationManager).should()
                    .subscribeOrganizationTopic(any(), any());
        }

        @Test
        void 예외_조직에_이미_알림을_구독한_디바이스() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Device device = DeviceFixture.create();
            deviceJpaRepository.save(device);

            OrganizationNotification organizationNotification = OrganizationNotificationFixture.create(organization,
                    device);
            organizationNotificationJpaRepository.save(organizationNotification);

            OrganizationNotificationRequest request = OrganizationNotificationRequestFixture.create(device.getId());

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/organizations/{organizationId}/notifications", organization.getId())
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", equalTo("이미 알림을 구독한 조직입니다."));

            then(fcmNotificationManager).shouldHaveNoInteractions();
        }

        @Test
        void 예외_존재하지_않는_디바이스() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Long invalidDeviceId = 0L;
            OrganizationNotificationRequest request = OrganizationNotificationRequestFixture.create(invalidDeviceId);

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/organizations/{organizationId}/notifications", organization.getId())
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

            OrganizationNotificationRequest request = OrganizationNotificationRequestFixture.create(device.getId());

            Long invalidOrganizationId = 0L;

            // when & then
            RestAssured.
                    given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/organizations/{organizationId}/notifications", invalidOrganizationId)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", equalTo("존재하지 않는 조직입니다."));

            then(fcmNotificationManager).shouldHaveNoInteractions();
        }
    }

    @Nested
    class unsubscribeOrganizationNotification {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Device device = DeviceFixture.create();
            deviceJpaRepository.save(device);

            OrganizationNotification organizationNotification = OrganizationNotificationFixture.create(organization,
                    device);
            organizationNotificationJpaRepository.save(organizationNotification);

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .when()
                    .delete("/organizations/notifications/{organizationNotificationId}",
                            organizationNotification.getId())
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            boolean exists = organizationNotificationJpaRepository.existsById(organizationNotification.getId());
            assertThat(exists).isFalse();
            then(fcmNotificationManager).should()
                    .unsubscribeOrganizationTopic(any(), any());

        }

        @Test
        void 성공_알림_삭제시_조직_알림이_존재하지_않아도_정상_처리() {
            // given
            Long invalidOrganizationNotificationId = 0L;

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .when()
                    .delete("/organizations/notifications/{organizationNotificationId}",
                            invalidOrganizationNotificationId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            then(fcmNotificationManager).shouldHaveNoInteractions();
        }
    }
}
