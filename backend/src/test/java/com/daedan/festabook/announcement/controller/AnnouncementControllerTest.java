package com.daedan.festabook.announcement.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

import com.daedan.festabook.announcement.domain.Announcement;
import com.daedan.festabook.announcement.domain.AnnouncementFixture;
import com.daedan.festabook.announcement.dto.AnnouncementPinUpdateRequest;
import com.daedan.festabook.announcement.dto.AnnouncementPinUpdateRequestFixture;
import com.daedan.festabook.announcement.dto.AnnouncementRequest;
import com.daedan.festabook.announcement.dto.AnnouncementRequestFixture;
import com.daedan.festabook.announcement.dto.AnnouncementUpdateRequest;
import com.daedan.festabook.announcement.dto.AnnouncementUpdateRequestFixture;
import com.daedan.festabook.announcement.infrastructure.AnnouncementJpaRepository;
import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.global.security.JwtTestHelper;
import com.daedan.festabook.notification.infrastructure.FcmNotificationManager;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AnnouncementControllerTest {

    private static final String FESTIVAL_HEADER_NAME = "festival";

    @Autowired
    private AnnouncementJpaRepository announcementJpaRepository;

    @Autowired
    private FestivalJpaRepository festivalJpaRepository;

    @Autowired
    private JwtTestHelper jwtTestHelper;

    @MockitoBean
    private FcmNotificationManager fcmNotificationManager;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    class createAnnouncement {

        @Test
        void 성공() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createAuthorizationHeader(festival);

            AnnouncementRequest request = new AnnouncementRequest(
                    "폭우가 내립니다.",
                    "우산을 챙겨주세요.",
                    true
            );

            int expectedFieldSize = 5;

            // when & then
            RestAssured
                    .given()
                    .header(authorizationHeader)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/announcements")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("title", equalTo(request.title()))
                    .body("content", equalTo(request.content()))
                    .body("isPinned", equalTo(request.isPinned()))
                    .body("createdAt", notNullValue());
        }

        @Test
        void 예외_고정_공지_최대치_초과() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createAuthorizationHeader(festival);

            boolean isPinned = true;
            announcementJpaRepository.saveAll(AnnouncementFixture.createList(3, isPinned, festival));

            AnnouncementRequest request = AnnouncementRequestFixture.create(isPinned);

            // when & then
            RestAssured
                    .given()
                    .header(authorizationHeader)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/announcements")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", equalTo("공지글은 최대 3개까지 고정할 수 있습니다."));
        }
    }

    @Nested
    class getGroupedAnnouncementByFestivalId {

        @Test
        void 성공() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Announcement announcement1 = AnnouncementFixture.create(true, festival);
            Announcement announcement2 = AnnouncementFixture.create(false, festival);
            announcementJpaRepository.saveAll(List.of(announcement1, announcement2));

            int expectedSize = 2;
            int expectedPinnedSize = 1;
            int expectedUnpinnedSize = 1;
            int expectedFieldSize = 5;

            // when & then
            RestAssured
                    .given()
                    .header(FESTIVAL_HEADER_NAME, festival.getId())
                    .when()
                    .get("/announcements")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasKey("pinned"))
                    .body("$", hasKey("unpinned"))
                    .body("size()", equalTo(expectedSize))
                    .body("pinned", hasSize(expectedPinnedSize))
                    .body("unpinned.size()", equalTo(expectedUnpinnedSize))
                    .body("pinned[0].size()", equalTo(expectedFieldSize))
                    .body("pinned[0].announcementId", equalTo(announcement1.getId().intValue()))
                    .body("pinned[0].title", equalTo(announcement1.getTitle()))
                    .body("pinned[0].content", equalTo(announcement1.getContent()))
                    .body("pinned[0].isPinned", equalTo(announcement1.isPinned()))
                    .body("pinned[0].createdAt", notNullValue())
                    .body("unpinned[0].size()", equalTo(expectedFieldSize))
                    .body("unpinned[0].announcementId", equalTo(announcement2.getId().intValue()))
                    .body("unpinned[0].title", equalTo(announcement2.getTitle()))
                    .body("unpinned[0].content", equalTo(announcement2.getContent()))
                    .body("unpinned[0].isPinned", equalTo(announcement2.isPinned()))
                    .body("unpinned[0].createdAt", notNullValue());
        }

        @Test
        void 성공_여러_값_조회() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            int expectedPinnedSize = 3;
            int expectedUnpinnedSize = 4;
            List<Announcement> pinnedAnnouncements = AnnouncementFixture.createList(expectedPinnedSize, true,
                    festival);
            List<Announcement> unpinnedAnnouncements = AnnouncementFixture.createList(expectedUnpinnedSize, false,
                    festival);

            announcementJpaRepository.saveAll(pinnedAnnouncements);
            announcementJpaRepository.saveAll(unpinnedAnnouncements);

            // when & then
            RestAssured
                    .given()
                    .header(FESTIVAL_HEADER_NAME, festival.getId())
                    .when()
                    .get("/announcements")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("pinned.size()", equalTo(expectedPinnedSize))
                    .body("unpinned.size()", equalTo(expectedUnpinnedSize));
        }

        @Test
        void 성공_생성일_역순_정렬() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Announcement announcement1 = AnnouncementFixture.create(true, festival);
            Announcement announcement2 = AnnouncementFixture.create(true, festival);
            Announcement announcement3 = AnnouncementFixture.create(true, festival);
            announcementJpaRepository.saveAll(List.of(announcement1, announcement2, announcement3));

            // when & then
            List<Long> result = RestAssured
                    .given()
                    .header(FESTIVAL_HEADER_NAME, festival.getId())
                    .when()
                    .get("/announcements")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .jsonPath()
                    .getList("pinned.announcementId", Long.class);

            assertSoftly(s -> {
                s.assertThat(result.get(0)).isEqualTo(announcement3.getId());
                s.assertThat(result.get(1)).isEqualTo(announcement2.getId());
                s.assertThat(result.get(2)).isEqualTo(announcement1.getId());
            });
        }

        @Test
        void 성공_서로_다른_축제() {
            // given
            Festival targetFestival = FestivalFixture.create();
            Festival anotherFestival = FestivalFixture.create();
            festivalJpaRepository.saveAll(List.of(targetFestival, anotherFestival));

            int expectedSize = 3;
            List<Announcement> targetAnnouncements = AnnouncementFixture.createList(expectedSize, true,
                    targetFestival);
            announcementJpaRepository.saveAll(targetAnnouncements);

            int notExpectedSize = 4;
            List<Announcement> anotherAnnouncements = AnnouncementFixture.createList(notExpectedSize, true,
                    anotherFestival);
            announcementJpaRepository.saveAll(anotherAnnouncements);

            // when & then
            RestAssured
                    .given()
                    .header(FESTIVAL_HEADER_NAME, targetFestival.getId())
                    .when()
                    .get("/announcements")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("pinned.size()", equalTo(expectedSize))
                    .body("pinned.announcementId", containsInAnyOrder(
                            targetAnnouncements.get(0).getId().intValue(),
                            targetAnnouncements.get(1).getId().intValue(),
                            targetAnnouncements.get(2).getId().intValue()
                    ));
        }
    }

    @Nested
    class updateAnnouncement {

        @Test
        void 성공() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createAuthorizationHeader(festival);

            Announcement announcement = AnnouncementFixture.create(festival);
            announcementJpaRepository.save(announcement);

            AnnouncementUpdateRequest request = AnnouncementUpdateRequestFixture.create("수정된 제목", "수정된 내용");

            int expectedFieldSize = 3;

            // when & then
            RestAssured
                    .given()
                    .header(authorizationHeader)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .patch("/announcements/{announcementId}", announcement.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("announcementId", notNullValue())
                    .body("title", equalTo(request.title()))
                    .body("content", equalTo(request.content()));
        }

        @Test
        void 예외_존재하지_않는_공지() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createAuthorizationHeader(festival);

            Long notExistId = 0L;
            AnnouncementUpdateRequest request = AnnouncementUpdateRequestFixture.create();

            // when & then
            RestAssured
                    .given()
                    .header(authorizationHeader)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .patch("/announcements/{announcementId}", notExistId)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", equalTo("존재하지 않는 공지입니다."));
        }
    }

    @Nested
    class updateAnnouncementPin {

        @ParameterizedTest(name = "초기 고정 상태: {0}, 변경할 상태: {1}")
        @CsvSource({
                "true, false",   // 고정 해제
                "false, true"    // 고정 설정
        })
        void 성공_고정_상태_변경(boolean initialPinned, boolean expectedPinned) {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createAuthorizationHeader(festival);

            Announcement announcement = AnnouncementFixture.create(initialPinned, festival);
            announcementJpaRepository.save(announcement);

            AnnouncementPinUpdateRequest request = AnnouncementPinUpdateRequestFixture.create(expectedPinned);

            int expectedFieldSize = 2;

            // when & then
            RestAssured
                    .given()
                    .header(authorizationHeader)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .patch("/announcements/{announcementId}/pin", announcement.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("announcementId", notNullValue())
                    .body("isPinned", equalTo(request.pinned()));

            Announcement updatedAnnouncement = announcementJpaRepository.findById(announcement.getId()).get();
            assertThat(updatedAnnouncement.isPinned()).isEqualTo(expectedPinned);
        }
    }

    @Nested
    class deleteAnnouncementByAnnouncementId {

        @Test
        void 성공() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createAuthorizationHeader(festival);

            Announcement announcement = AnnouncementFixture.create(festival);
            announcementJpaRepository.save(announcement);

            // when & then
            RestAssured
                    .given()
                    .header(authorizationHeader)
                    .when()
                    .delete("/announcements/{announcementId}", announcement.getId())
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        }
    }

    @Nested
    class sendAnnouncementNotification {

        @Test
        void 성공() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createAuthorizationHeader(festival);

            Announcement announcement = AnnouncementFixture.create(festival);
            announcementJpaRepository.save(announcement);

            willDoNothing().given(fcmNotificationManager)
                    .sendToFestivalTopic(any(), any());

            // when & then
            RestAssured
                    .given()
                    .header(authorizationHeader)
                    .when()
                    .post("/announcements/{announcementId}/notification", announcement.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value());

            then(fcmNotificationManager).should()
                    .sendToFestivalTopic(any(), any());
        }
    }
}
