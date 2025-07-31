package com.daedan.festabook.image.controller;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.daedan.festabook.image.domain.ImageManager;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ImageControllerTest {

    @MockitoBean
    private ImageManager imageManager;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    class createPlaceImage {

        @Test
        void 성공() {
            // given
            byte[] imageByteValue = "테스트용 이미지 바이트".getBytes();
            String mimeType = "image/jpeg";
            String fileName = "fake-image.jpg";
            String multipartName = "image";

            int expectedFiledSize = 1;
            String expectedImageUrl = "https://example.org/image/1298301849012840";

            given(imageManager.upload(any()))
                    .willReturn(expectedImageUrl);

            // when & then
            RestAssured
                    .given()
                    .multiPart(multipartName, fileName, imageByteValue, mimeType)
                    .post("/images")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("size()", equalTo(expectedFiledSize))
                    .body("imageUrl", equalTo(expectedImageUrl));
        }
    }
}
