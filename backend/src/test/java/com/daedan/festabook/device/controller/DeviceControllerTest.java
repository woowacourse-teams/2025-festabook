package com.daedan.festabook.device.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.daedan.festabook.device.dto.DeviceRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class DeviceControllerTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    class createDevice {

        @Test
        void 성공_신규_Device_등록_id_응답() {
            // given
            String expectedDeviceIdentifier = "AAAAAAAA-AAAA-AAAA-AAAA-AAAAAAAAAAAA";
            String expectedFcmToken = "FCM_00000000";
            DeviceRequest request = new DeviceRequest(expectedDeviceIdentifier, expectedFcmToken);

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
                    .body("id", notNullValue());
        }

        @Test
        void 성공_복귀_Device_등록_id_응답() {
            // given
            String expectedDeviceIdentifier = "BBBBBB-BBBB-BBBB-BBBB-BBBBBBBBBBBB";
            String expectedFcmToken = "FCM_11111111";
            DeviceRequest request = new DeviceRequest(expectedDeviceIdentifier, expectedFcmToken);

            Integer expectedId = RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/devices")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("id", notNullValue())
                    .extract()
                    .path("id");

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
                    .body("id", equalTo(expectedId));
        }
    }
}
