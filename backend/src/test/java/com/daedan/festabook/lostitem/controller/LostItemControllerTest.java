package com.daedan.festabook.lostitem.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.lostitem.domain.LostItem;
import com.daedan.festabook.lostitem.domain.LostItemFixture;
import com.daedan.festabook.lostitem.domain.PickupStatus;
import com.daedan.festabook.lostitem.dto.LostItemRequest;
import com.daedan.festabook.lostitem.dto.LostItemRequestFixture;
import com.daedan.festabook.lostitem.infrastructure.LostItemJpaRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LostItemControllerTest {

    private static final String FESTIVAL_HEADER_NAME = "festival";

    @Autowired
    private LostItemJpaRepository lostItemJpaRepository;

    @Autowired
    private FestivalJpaRepository festivalJpaRepository;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    class createLostItem {

        @Test
        void 성공() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            LostItemRequest request = LostItemRequestFixture.create();

            int expectedFieldSize = 5;

            // when & then
            given()
                    .header(FESTIVAL_HEADER_NAME, festival.getId())
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/lost-items")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("lostItemId", notNullValue())
                    .body("imageUrl", equalTo(request.imageUrl()))
                    .body("storageLocation", equalTo(request.storageLocation()))
                    .body("status", equalTo("PENDING"))
                    .body("createdAt", notNullValue());
        }
    }

    @Nested
    class getAllLostItemByFestivalId {

        @Test
        void 성공_사이즈_검증() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            LostItem lostItem1 = LostItemFixture.create(festival);
            LostItem lostItem2 = LostItemFixture.create(festival);
            lostItemJpaRepository.saveAll(List.of(lostItem1, lostItem2));

            int expectedSize = 2;

            // when & then
            RestAssured
                    .given()
                    .header(FESTIVAL_HEADER_NAME, festival.getId())
                    .when()
                    .get("/lost-items")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize));
        }

        @Test
        void 성공_필드값_검증() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            LostItem lostItem1 = LostItemFixture.create(
                    festival,
                    "http://example.com/image1.png",
                    "창고A",
                    PickupStatus.PENDING
            );
            LostItem lostItem2 = LostItemFixture.create(
                    festival,
                    "http://example.com/image2.png",
                    "창고B",
                    PickupStatus.COMPLETED
            );

            lostItemJpaRepository.saveAll(List.of(lostItem1, lostItem2));

            int expectedFieldSize = 5;

            // when & then
            given()
                    .header(FESTIVAL_HEADER_NAME, festival.getId())
                    .when()
                    .get("/lost-items")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("[0].size()", equalTo(expectedFieldSize))

                    .body("[0].imageUrl", equalTo(lostItem1.getImageUrl()))
                    .body("[0].storageLocation", equalTo(lostItem1.getStorageLocation()))
                    .body("[0].status", equalTo(lostItem1.getStatus().name()))

                    .body("[1].imageUrl", equalTo(lostItem2.getImageUrl()))
                    .body("[1].storageLocation", equalTo(lostItem2.getStorageLocation()))
                    .body("[1].status", equalTo(lostItem2.getStatus().name()));
        }
    }
}
