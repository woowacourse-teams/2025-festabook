package com.daedan.festabook.device.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.daedan.festabook.device.domain.Device;
import com.daedan.festabook.device.domain.DeviceFixture;
import com.daedan.festabook.device.dto.DeviceRequest;
import com.daedan.festabook.device.dto.DeviceRequestFixture;
import com.daedan.festabook.device.dto.DeviceUpdateRequest;
import com.daedan.festabook.device.dto.DeviceUpdateRequestFixture;
import com.daedan.festabook.device.infrastructure.DeviceJpaRepository;
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
class DeviceControllerTest {

    @Autowired
    private DeviceJpaRepository deviceJpaRepository;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    class registerDevice {

        @Test
        void 성공_Device_등록() {
            // given
            String deviceIdentifier = "AAAAAAAA-AAAA-AAAA-AAAA-AAAAAAAAAAAA";
            String fcmToken = "FCM_00000000";
            DeviceRequest request = DeviceRequestFixture.create(deviceIdentifier, fcmToken);

            int expectedFieldSize = 1;

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/devices")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("deviceId", notNullValue());
        }

        @Test
        void 성공_기존_디바이스_재등록시_동일한_ID_반환() {
            // given
            String deviceIdentifier = "BBBBBB-BBBB-BBBB-BBBB-BBBBBBBBBBBB";
            String fcmToken = "FCM_11111111";
            DeviceRequest request = DeviceRequestFixture.create(deviceIdentifier, fcmToken);

            Integer expectedId = RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/devices")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("deviceId", notNullValue())
                    .extract()
                    .path("deviceId");

            int expectedFieldSize = 1;

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/devices")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("deviceId", equalTo(expectedId));
        }
    }

    @Nested
    class updateDevice {

        @Test
        void 성공() {
            // given
            Long deviceId = 1L;
            Device device = DeviceFixture.create();
            deviceJpaRepository.save(device);

            String fcmToken = "FCM_00000000";
            DeviceUpdateRequest request = DeviceUpdateRequestFixture.create(fcmToken);

            int expectedFieldSize = 1;

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .patch("/devices/{deviceId}", deviceId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("deviceId", notNullValue());
        }
    }
}
