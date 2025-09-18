package com.daedan.festabook.storage.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.global.security.JwtTestHelper;
import com.daedan.festabook.global.security.role.RoleType;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ImageStoreControllerTest {

    @Autowired
    private FestivalJpaRepository festivalJpaRepository;

    @Autowired
    private JwtTestHelper jwtTestHelper;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    class UploadImage {

        @ParameterizedTest
        @EnumSource(RoleType.class)
        void 성공(RoleType roleType) throws IOException {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createAuthorizationHeaderWithRole(festival, roleType);

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
                    .header(authorizationHeader)
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
