package com.daedan.festabook.festival.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;

import com.daedan.festabook.device.domain.Device;
import com.daedan.festabook.device.domain.DeviceFixture;
import com.daedan.festabook.device.infrastructure.DeviceJpaRepository;
import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.festival.domain.FestivalNotification;
import com.daedan.festabook.festival.domain.FestivalNotificationFixture;
import com.daedan.festabook.festival.dto.FestivalNotificationRequest;
import com.daedan.festabook.festival.dto.FestivalNotificationRequestFixture;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.festival.infrastructure.FestivalNotificationJpaRepository;
import com.daedan.festabook.notification.infrastructure.FcmNotificationManager;
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
class FestivalNotificationControllerTest {

    @Autowired
    private FestivalJpaRepository festivalJpaRepository;

    @Autowired
    private DeviceJpaRepository deviceJpaRepository;

    @Autowired
    private FestivalNotificationJpaRepository festivalNotificationJpaRepository;

    @MockitoBean
    private FcmNotificationManager fcmNotificationManager;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    class subscribeFestivalNotification {

        @Test
        void 성공() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Device device = DeviceFixture.create();
            deviceJpaRepository.save(device);

            FestivalNotificationRequest request = FestivalNotificationRequestFixture.create(device.getId());

            int expectedFieldSize = 1;

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/festivals/{festivalId}/notifications", festival.getId())
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("festivalNotificationId", notNullValue());

            then(fcmNotificationManager).should()
                    .subscribeFestivalTopic(any(), any());
        }

        @Test
        void 예외_축제에_이미_알림을_구독한_디바이스() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Device device = DeviceFixture.create();
            deviceJpaRepository.save(device);

            FestivalNotification festivalNotification = FestivalNotificationFixture.create(festival,
                    device);
            festivalNotificationJpaRepository.save(festivalNotification);

            FestivalNotificationRequest request = FestivalNotificationRequestFixture.create(device.getId());

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/festivals/{festivalId}/notifications", festival.getId())
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", equalTo("이미 알림을 구독한 축제입니다."));

            then(fcmNotificationManager).shouldHaveNoInteractions();
        }

        @Test
        void 예외_존재하지_않는_디바이스() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Long invalidDeviceId = 0L;
            FestivalNotificationRequest request = FestivalNotificationRequestFixture.create(invalidDeviceId);

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/festivals/{festivalId}/notifications", festival.getId())
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", equalTo("존재하지 않는 디바이스입니다."));

            then(fcmNotificationManager).shouldHaveNoInteractions();
        }

        @Test
        void 예외_존재하지_않는_축제() {
            // given
            Device device = DeviceFixture.create();
            deviceJpaRepository.save(device);

            FestivalNotificationRequest request = FestivalNotificationRequestFixture.create(device.getId());

            Long invalidFestivalId = 0L;

            // when & then
            RestAssured.
                    given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/festivals/{festivalId}/notifications", invalidFestivalId)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", equalTo("존재하지 않는 축제입니다."));

            then(fcmNotificationManager).shouldHaveNoInteractions();
        }
    }

    @Nested
    class unsubscribeFestivalNotification {

        @Test
        void 성공() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Device device = DeviceFixture.create();
            deviceJpaRepository.save(device);

            FestivalNotification festivalNotification = FestivalNotificationFixture.create(festival,
                    device);
            festivalNotificationJpaRepository.save(festivalNotification);

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .when()
                    .delete("/festivals/notifications/{festivalNotificationId}",
                            festivalNotification.getId())
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            boolean exists = festivalNotificationJpaRepository.existsById(festivalNotification.getId());
            assertThat(exists).isFalse();
            then(fcmNotificationManager).should()
                    .unsubscribeFestivalTopic(any(), any());

        }

        @Test
        void 성공_알림_삭제시_축제_알림이_존재하지_않아도_정상_처리() {
            // given
            Long invalidFestivalNotificationId = 0L;

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .when()
                    .delete("/festivals/notifications/{festivalNotificationId}",
                            invalidFestivalNotificationId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            then(fcmNotificationManager).shouldHaveNoInteractions();
        }
    }
}
