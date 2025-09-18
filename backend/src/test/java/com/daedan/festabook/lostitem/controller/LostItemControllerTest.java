package com.daedan.festabook.lostitem.controller;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.global.security.JwtTestHelper;
import com.daedan.festabook.global.security.role.RoleType;
import com.daedan.festabook.lostitem.domain.LostItem;
import com.daedan.festabook.lostitem.domain.LostItemFixture;
import com.daedan.festabook.lostitem.domain.PickupStatus;
import com.daedan.festabook.lostitem.dto.LostItemRequest;
import com.daedan.festabook.lostitem.dto.LostItemRequestFixture;
import com.daedan.festabook.lostitem.dto.LostItemStatusUpdateRequest;
import com.daedan.festabook.lostitem.dto.LostItemStatusUpdateRequestFixture;
import com.daedan.festabook.lostitem.infrastructure.LostItemJpaRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
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

    @Autowired
    private JwtTestHelper jwtTestHelper;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    class createLostItem {

        @ParameterizedTest
        @EnumSource(RoleType.class)
        void 성공(RoleType roleType) {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createAuthorizationHeaderWithRole(festival, roleType);

            LostItemRequest request = LostItemRequestFixture.create();

            int expectedFieldSize = 5;

            // when & then
            given()
                    .header(authorizationHeader)
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
                    .body("pickupStatus", equalTo("PENDING"))
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
                    .body("[0].pickupStatus", equalTo(lostItem1.getStatus().name()))

                    .body("[1].imageUrl", equalTo(lostItem2.getImageUrl()))
                    .body("[1].storageLocation", equalTo(lostItem2.getStorageLocation()))
                    .body("[1].pickupStatus", equalTo(lostItem2.getStatus().name()));
        }
    }

    @Nested
    class updateLostItem {

        @ParameterizedTest
        @EnumSource(RoleType.class)
        void 성공(RoleType roleType) {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createAuthorizationHeaderWithRole(festival, roleType);

            LostItem lostItem = LostItemFixture.create(festival);
            lostItemJpaRepository.save(lostItem);

            LostItemRequest request = LostItemRequestFixture.create(
                    "http://example.com/updated-image.png",
                    "수정된 보관장소"
            );

            int expectedFieldSize = 3;

            // when & then
            RestAssured
                    .given()
                    .header(authorizationHeader)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .patch("/lost-items/{lostItemId}", lostItem.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("lostItemId", notNullValue())
                    .body("storageLocation", equalTo(request.storageLocation()))
                    .body("imageUrl", equalTo(request.imageUrl()));
        }
    }

    @Nested
    class updateLostItemStatus {

        @ParameterizedTest
        @CsvSource({
                "PENDING, PENDING",
                "COMPLETED, COMPLETED",
                "PENDING, COMPLETED",
                "COMPLETED, PENDING"
        })
        void 성공(PickupStatus previousStatus, PickupStatus updatedStatus) {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createAuthorizationHeader(festival);

            LostItem lostItem = LostItemFixture.create(festival, previousStatus);
            lostItemJpaRepository.save(lostItem);

            LostItemStatusUpdateRequest request = LostItemStatusUpdateRequestFixture.create(updatedStatus);

            int expectedFieldSize = 2;

            // when & then
            RestAssured
                    .given()
                    .header(authorizationHeader)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .patch("/lost-items/{lostItemId}/status", lostItem.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("lostItemId", notNullValue())
                    .body("pickupStatus", equalTo(request.pickupStatus().name()));
        }

        @ParameterizedTest
        @EnumSource(RoleType.class)
        void 성공_권한(RoleType roleType) {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createAuthorizationHeaderWithRole(festival, roleType);

            PickupStatus previousStatus = PickupStatus.PENDING;
            PickupStatus updatedStatus = PickupStatus.COMPLETED;

            LostItem lostItem = LostItemFixture.create(festival, previousStatus);
            lostItemJpaRepository.save(lostItem);

            LostItemStatusUpdateRequest request = LostItemStatusUpdateRequestFixture.create(updatedStatus);

            // when & then
            RestAssured
                    .given()
                    .header(authorizationHeader)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .patch("/lost-items/{lostItemId}/status", lostItem.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value());
        }
    }

    @Nested
    class deleteLostItemByLostItemId {

        @ParameterizedTest
        @EnumSource(RoleType.class)
        void 성공(RoleType roleType) {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createAuthorizationHeaderWithRole(festival, roleType);

            LostItem lostItem = LostItemFixture.create(festival);
            lostItemJpaRepository.save(lostItem);

            // when & then
            RestAssured
                    .given()
                    .header(authorizationHeader)
                    .when()
                    .delete("/lost-items/{lostItemId}", lostItem.getId())
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            assertThat(lostItemJpaRepository.findById(lostItem.getId())).isEmpty();
        }
    }
}
