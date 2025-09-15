package com.daedan.festabook.festival.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import com.daedan.festabook.festival.domain.Coordinate;
import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.festival.domain.FestivalImage;
import com.daedan.festabook.festival.domain.FestivalImageFixture;
import com.daedan.festabook.festival.dto.FestivalCreateRequest;
import com.daedan.festabook.festival.dto.FestivalCreateRequestFixture;
import com.daedan.festabook.festival.dto.FestivalImageRequest;
import com.daedan.festabook.festival.dto.FestivalImageRequestFixture;
import com.daedan.festabook.festival.dto.FestivalImageSequenceUpdateRequest;
import com.daedan.festabook.festival.dto.FestivalImageSequenceUpdateRequestFixture;
import com.daedan.festabook.festival.dto.FestivalInformationUpdateRequest;
import com.daedan.festabook.festival.dto.FestivalInformationUpdateRequestFixture;
import com.daedan.festabook.festival.dto.FestivalLostItemGuideUpdateRequest;
import com.daedan.festabook.festival.infrastructure.FestivalImageJpaRepository;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.global.security.JwtTestHelper;
import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.path.json.config.JsonPathConfig;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class FestivalControllerTest {

    private static final String FESTIVAL_HEADER_NAME = "festival";

    @Autowired
    private FestivalJpaRepository festivalJpaRepository;

    @Autowired
    private FestivalImageJpaRepository festivalImageJpaRepository;

    @Autowired
    private JwtTestHelper jwtTestHelper;

    @LocalServerPort
    private int port;

    @BeforeAll
    static void beforeAll() {
        RestAssured.config = RestAssured.config()
                .jsonConfig(JsonConfig.jsonConfig()
                        .numberReturnType(JsonPathConfig.NumberReturnType.DOUBLE));
    }

    @AfterAll
    static void afterAll() {
        RestAssured.config = RestAssuredConfig.config();
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    class createFestival {

        @Test
        void 성공() {
            // given
            FestivalCreateRequest request = FestivalCreateRequestFixture.create(
                    "서울시립대학교",
                    "2025 시립 Water Festival\n: AQUA WAVE",
                    LocalDate.of(2025, 8, 23),
                    LocalDate.of(2025, 8, 25),
                    7,
                    new Coordinate(37.5862037, 127.0565152),
                    List.of(new Coordinate(37.5862037, 127.0565152), new Coordinate(37.5845543, 127.0555925)),
                    "습득하신 분실물 혹은 수령 문의는 학생회 인스타그램 DM으로 전송해주세요."
            );

            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createAdminAuthorizationHeader(festival);

            int expectedFieldSize = 9;

            // when & then
            RestAssured
                    .given()
                    .header(authorizationHeader)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/festivals")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("festivalId", notNullValue())
                    .body("universityName", equalTo(request.universityName()))
                    .body("festivalName", equalTo(request.festivalName()))
                    .body("startDate", equalTo(request.startDate().toString()))
                    .body("endDate", equalTo(request.endDate().toString()))
                    .body("userVisible", equalTo(false))
                    .body("zoom", equalTo(request.zoom()))
                    .body("centerCoordinate.latitude", equalTo(request.centerCoordinate().getLatitude()))
                    .body("centerCoordinate.longitude", equalTo(request.centerCoordinate().getLongitude()))
                    .body("polygonHoleBoundary", notNullValue());
        }
    }

    @Nested
    class addFestivalImage {

        @Test
        void 성공() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createAuthorizationHeader(festival);

            FestivalImageRequest request = FestivalImageRequestFixture.create("이미지 URL");

            Integer count = festivalImageJpaRepository.findMaxSequenceByFestivalId(festival.getId())
                    .orElseGet(() -> 0);

            int expectedFieldSize = 3;
            int expectedSequence = count + 1;

            // when & then
            RestAssured
                    .given()
                    .header(authorizationHeader)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/festivals/images")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("festivalImageId", notNullValue())
                    .body("imageUrl", equalTo(request.imageUrl()))
                    .body("sequence", equalTo(expectedSequence));
        }
    }

    @Nested
    class getFestivalGeographyByFestivalId {

        @Test
        void 성공() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            int expectedFieldSize = 3;

            // when & then
            RestAssured
                    .given()
                    .header(FESTIVAL_HEADER_NAME, festival.getId())
                    .when()
                    .get("/festivals/geography")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("zoom", equalTo(festival.getZoom()))
                    .body("centerCoordinate.latitude",
                            equalTo(festival.getCenterCoordinate().getLatitude()))
                    .body("centerCoordinate.longitude",
                            equalTo(festival.getCenterCoordinate().getLongitude()))
                    .body("polygonHoleBoundary[0].latitude",
                            equalTo(festival.getPolygonHoleBoundary().get(0).getLatitude()))
                    .body("polygonHoleBoundary[0].longitude",
                            equalTo(festival.getPolygonHoleBoundary().get(0).getLongitude()))
                    .body("polygonHoleBoundary[1].latitude",
                            equalTo(festival.getPolygonHoleBoundary().get(1).getLatitude()))
                    .body("polygonHoleBoundary[1].longitude",
                            equalTo(festival.getPolygonHoleBoundary().get(1).getLongitude()))
                    .body("polygonHoleBoundary[2].latitude",
                            equalTo(festival.getPolygonHoleBoundary().get(2).getLatitude()))
                    .body("polygonHoleBoundary[2].longitude",
                            equalTo(festival.getPolygonHoleBoundary().get(2).getLongitude()));
        }
    }

    @Nested
    class getFestivalByFestivalId {

        @Test
        void 성공() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            FestivalImage festivalImage2 = FestivalImageFixture.create(festival, 2);
            FestivalImage festivalImage1 = FestivalImageFixture.create(festival, 1);
            festivalImageJpaRepository.saveAll(List.of(festivalImage2, festivalImage1));

            int festivalImageSize = 2;
            int expectedFieldSize = 7;

            // when & then
            RestAssured
                    .given()
                    .header(FESTIVAL_HEADER_NAME, festival.getId())
                    .when()
                    .get("/festivals")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("universityName", equalTo(festival.getUniversityName()))
                    .body("festivalName", equalTo(festival.getFestivalName()))
                    .body("startDate", equalTo(festival.getStartDate().toString()))
                    .body("endDate", equalTo(festival.getEndDate().toString()))

                    .body("festivalImages", hasSize(festivalImageSize))
                    .body("festivalImages[0].festivalImageId", equalTo(festivalImage1.getId().intValue()))
                    .body("festivalImages[0].sequence", equalTo(festivalImage1.getSequence()))

                    .body("festivalImages[1].festivalImageId", equalTo(festivalImage2.getId().intValue()))
                    .body("festivalImages[1].sequence", equalTo(festivalImage2.getSequence()));
        }

        @Test
        void 성공_이미지_오름차순_정렬() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            FestivalImage festivalImage3 = FestivalImageFixture.create(festival, 3);
            FestivalImage festivalImage2 = FestivalImageFixture.create(festival, 2);
            FestivalImage festivalImage1 = FestivalImageFixture.create(festival, 1);
            festivalImageJpaRepository.saveAll(List.of(festivalImage3, festivalImage2, festivalImage1));

            // when & then
            RestAssured
                    .given()
                    .header(FESTIVAL_HEADER_NAME, festival.getId())
                    .when()
                    .get("/festivals")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("festivalImages[0].sequence", equalTo(festivalImage1.getSequence()))
                    .body("festivalImages[1].sequence", equalTo(festivalImage2.getSequence()))
                    .body("festivalImages[2].sequence", equalTo(festivalImage3.getSequence()));
        }
    }

    @Nested
    class getUniversitiesByUniversityName {
        // TODO: 테스트 격리 문제

        @Test
        void 성공() {
            String universityName1 = "한양 대학교";
            String festivalName1 = "한양 축제";
            LocalDate startDate1 = LocalDate.of(2025, 11, 1);
            LocalDate endDate1 = LocalDate.of(2025, 11, 2);

            String universityName2 = "한양 에리카 대학교";
            String festivalName2 = "한양 에리카 축제";
            LocalDate startDate2 = LocalDate.of(2025, 11, 3);
            LocalDate endDate2 = LocalDate.of(2025, 11, 4);

            Festival festival1 = FestivalFixture.create(universityName1, festivalName1, startDate1, endDate1);
            Festival festival2 = FestivalFixture.create(universityName2, festivalName2, startDate2, endDate2);
            festivalJpaRepository.saveAll(List.of(festival1, festival2));

            String universityNameToSearch = "한양";

            int expectedSize = 2;
            int expectedFieldSize = 5;

            // when & then
            RestAssured
                    .given()
                    .when()
                    .get("/festivals/universities?universityName={universityName}", universityNameToSearch)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize))

                    .body("[0].size()", equalTo(expectedFieldSize))
                    .body("[0].festivalId", equalTo(festival1.getId().intValue()))
                    .body("[0].universityName", equalTo(festival1.getUniversityName()))
                    .body("[0].festivalName", equalTo(festival1.getFestivalName()))
                    .body("[0].startDate", equalTo(festival1.getStartDate().toString()))
                    .body("[0].endDate", equalTo(festival1.getEndDate().toString()))

                    .body("[1].size()", equalTo(expectedFieldSize))
                    .body("[1].festivalId", equalTo(festival2.getId().intValue()))
                    .body("[1].universityName", equalTo(festival2.getUniversityName()))
                    .body("[1].festivalName", equalTo(festival2.getFestivalName()))
                    .body("[1].startDate", equalTo(festival2.getStartDate().toString()))
                    .body("[1].endDate", equalTo(festival2.getEndDate().toString()));
        }

        @Test
        void 성공_서로_다른_대학() {
            String universityName = "한국 대학교";
            String anotherUniversityName = "서울 대학교";
            Festival festival1 = FestivalFixture.create(universityName);
            Festival festival2 = FestivalFixture.create(anotherUniversityName);
            festivalJpaRepository.saveAll(List.of(festival1, festival2));

            String universityNameToSearch = "한국";

            int expectedSize = 1;

            // when & then
            RestAssured
                    .given()
                    .when()
                    .get("/festivals/universities?universityName={universityName}", universityNameToSearch)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize))

                    .body("[0].festivalId", equalTo(festival1.getId().intValue()))
                    .body("[0].universityName", equalTo(festival1.getUniversityName()));
        }

        @ParameterizedTest
        @CsvSource({
                "비, 1",
                "비타대, 0",
                "비타 대, 1"
        })
        void 성공_여러_검색_범위(String universityNameToSearch, int expectedSize) {
            String universityName = "비타 대학교";
            Festival festival = FestivalFixture.create(universityName);
            festivalJpaRepository.save(festival);

            // when & then
            RestAssured
                    .given()
                    .when()
                    .get("/festivals/universities?universityName={universityName}", universityNameToSearch)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize));

            festivalJpaRepository.delete(festival);
        }

        @Test
        void 성공_userVisible_true만_반환됨() {
            // given
            String userVisibleFalseFestivalName = "후유바보대학교";
            String userVisibleTrueFestivalName = "미소대학교";
            Festival userVisibleFalseFestival = FestivalFixture.create(userVisibleFalseFestivalName, false);
            Festival userVisibleTrueFestival = FestivalFixture.create(userVisibleTrueFestivalName, true);
            festivalJpaRepository.saveAll(List.of(userVisibleTrueFestival, userVisibleFalseFestival));

            String universityNameToSearch = "미소";

            // when & then
            RestAssured
                    .given()
                    .when()
                    .get("/festivals/universities?universityName={universityName}", universityNameToSearch)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("[0].festivalId", equalTo(userVisibleTrueFestival.getId().intValue()))
                    .body("[0].universityName", equalTo(userVisibleTrueFestival.getUniversityName()));
        }
    }

    @Nested
    class getFestivalLostItemGuide {
        @Test
        void 성공() {
            String lostItemGuide = "습득하신 분실물은 밀러에게 전달하시기 바랍니다.";
            Festival festival = FestivalFixture.createWithLostItemGuide(lostItemGuide);
            festivalJpaRepository.save(festival);

            int expectedFieldSize = 1;

            // when & then
            RestAssured
                    .given()
                    .header("festival", festival.getId())
                    .when()
                    .get("/festivals/lost-item-guide")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("lostItemGuide", equalTo(lostItemGuide));
        }
    }

    @Nested
    class updateFestivalInformation {

        @Test
        void 성공() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createAuthorizationHeader(festival);

            String changedFestivalName = "수정 후 제목";
            LocalDate changedStartDate = LocalDate.of(2025, 11, 1);
            LocalDate changedEndDate = LocalDate.of(2025, 11, 2);
            boolean userVisible = true;
            FestivalInformationUpdateRequest request = FestivalInformationUpdateRequestFixture.create(
                    changedFestivalName,
                    changedStartDate,
                    changedEndDate,
                    userVisible
            );

            int expectedFieldSize = 5;

            // when & then
            RestAssured
                    .given()
                    .header(authorizationHeader)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .patch("/festivals/information")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("festivalId", equalTo(festival.getId().intValue()))
                    .body("festivalName", equalTo(changedFestivalName))
                    .body("startDate", equalTo(changedStartDate.toString()))
                    .body("endDate", equalTo(changedEndDate.toString()))
                    .body("userVisible", equalTo(userVisible));
        }
    }

    @Nested
    class updateFestivalLostItemGuide {

        @Test
        void 성공() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createAuthorizationHeader(festival);
            FestivalLostItemGuideUpdateRequest request = new FestivalLostItemGuideUpdateRequest("수정된 가이드");

            int expectedFieldSize = 1;

            // when & then
            RestAssured
                    .given()
                    .header(authorizationHeader)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .patch("/festivals/lost-item-guide")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("lostItemGuide", equalTo(request.lostItemGuide()));
        }
    }

    @Nested
    class updateFestivalImagesSequence {

        @Test
        void 성공_수정_후_응답값_오름차순_정렬() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createAuthorizationHeader(festival);

            FestivalImage festivalImage1 = FestivalImageFixture.create(festival, 1);
            FestivalImage festivalImage2 = FestivalImageFixture.create(festival, 2);
            FestivalImage festivalImage3 = FestivalImageFixture.create(festival, 3);
            festivalImageJpaRepository.saveAll(List.of(festivalImage1, festivalImage2, festivalImage3));

            List<FestivalImageSequenceUpdateRequest> requests = List.of(
                    FestivalImageSequenceUpdateRequestFixture.create(festivalImage1.getId(), 3),
                    FestivalImageSequenceUpdateRequestFixture.create(festivalImage2.getId(), 2),
                    FestivalImageSequenceUpdateRequestFixture.create(festivalImage3.getId(), 1)
            );

            // when & then
            RestAssured
                    .given()
                    .header(authorizationHeader)
                    .contentType(ContentType.JSON)
                    .body(requests)
                    .when()
                    .patch("/festivals/images/sequences")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("[0].festivalImageId", equalTo(festivalImage3.getId().intValue()))
                    .body("[0].sequence", equalTo(1))

                    .body("[1].festivalImageId", equalTo(festivalImage2.getId().intValue()))
                    .body("[1].sequence", equalTo(2))

                    .body("[2].festivalImageId", equalTo(festivalImage1.getId().intValue()))
                    .body("[2].sequence", equalTo(3));
        }
    }

    @Nested
    class removeFestivalImage {

        @Test
        void 성공() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createAuthorizationHeader(festival);

            FestivalImage festivalImage = FestivalImageFixture.create(festival, 1);
            festivalImageJpaRepository.save(festivalImage);

            // when & then
            RestAssured
                    .given()
                    .header(authorizationHeader)
                    .contentType(ContentType.JSON)
                    .when()
                    .delete("/festivals/images/{festivalImageId}", festivalImage.getId())
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            assertThat(festivalImageJpaRepository.findAllByFestivalIdOrderBySequenceAsc(festival.getId()))
                    .isEmpty();
        }
    }
}
