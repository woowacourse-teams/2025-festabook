package com.daedan.festabook.storage.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.RestAssured;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ImageStoreControllerTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    class UploadImage {

        @Test
        void 성공() throws IOException {
            // given
            MockMultipartFile file = new MockMultipartFile(
                    "image",
                    "sample.png",
                    "image/png",
                    new byte[10]
            );

            int expectedSize = 1;

            // when & then
            RestAssured
                    .given()
                    .multiPart(
                            "image",
                            file.getOriginalFilename(),
                            file.getBytes(),
                            file.getContentType()
                    )
                    .when()
                    .post("/images")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("size()", equalTo(expectedSize))
                    .body("imageUrl", notNullValue());
        }
    }
}
