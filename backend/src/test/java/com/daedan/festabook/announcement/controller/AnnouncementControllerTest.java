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

import com.daedan.festabook.announcement.domain.Announcement;
import com.daedan.festabook.announcement.domain.AnnouncementFixture;
import com.daedan.festabook.announcement.dto.AnnouncementPinUpdateRequest;
import com.daedan.festabook.announcement.dto.AnnouncementPinUpdateRequestFixture;
import com.daedan.festabook.announcement.dto.AnnouncementRequest;
import com.daedan.festabook.announcement.dto.AnnouncementRequestFixture;
import com.daedan.festabook.announcement.dto.AnnouncementUpdateRequest;
import com.daedan.festabook.announcement.dto.AnnouncementUpdateRequestFixture;
import com.daedan.festabook.announcement.infrastructure.AnnouncementJpaRepository;
import com.daedan.festabook.notification.infrastructure.FcmNotificationManager;
import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
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

    private static final String ORGANIZATION_HEADER_NAME = "organization";

    @Autowired
    private AnnouncementJpaRepository announcementJpaRepository;

    @Autowired
    private OrganizationJpaRepository organizationJpaRepository;

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
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            AnnouncementRequest request = new AnnouncementRequest(
                    "폭우가 내립니다.",
                    "우산을 챙겨주세요.",
                    true
            );

            int expectedFieldSize = 5;

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
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

            then(fcmNotificationManager).should()
                    .sendToOrganizationTopic(any(), any());
        }

        @Test
        void 예외_고정_공지_최대치_초과() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            boolean isPinned = true;
            announcementJpaRepository.saveAll(AnnouncementFixture.createList(3, isPinned, organization));

            AnnouncementRequest request = AnnouncementRequestFixture.create(isPinned);

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
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
    class getGroupedAnnouncementByOrganizationId {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Announcement announcement1 = AnnouncementFixture.create(true, organization);
            Announcement announcement2 = AnnouncementFixture.create(false, organization);
            announcementJpaRepository.saveAll(List.of(announcement1, announcement2));

            int expectedSize = 2;
            int expectedPinnedSize = 1;
            int expectedUnpinnedSize = 1;
            int expectedFieldSize = 5;

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
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
                    .body("pinned[0].id", equalTo(announcement1.getId().intValue()))
                    .body("pinned[0].title", equalTo(announcement1.getTitle()))
                    .body("pinned[0].content", equalTo(announcement1.getContent()))
                    .body("pinned[0].isPinned", equalTo(announcement1.isPinned()))
                    .body("pinned[0].createdAt", notNullValue())
                    .body("unpinned[0].size()", equalTo(expectedFieldSize))
                    .body("unpinned[0].id", equalTo(announcement2.getId().intValue()))
                    .body("unpinned[0].title", equalTo(announcement2.getTitle()))
                    .body("unpinned[0].content", equalTo(announcement2.getContent()))
                    .body("unpinned[0].isPinned", equalTo(announcement2.isPinned()))
                    .body("unpinned[0].createdAt", notNullValue());
        }

        @Test
        void 성공_여러_값_조회() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            int expectedPinnedSize = 3;
            int expectedUnpinnedSize = 4;
            List<Announcement> pinnedAnnouncements = AnnouncementFixture.createList(expectedPinnedSize, true,
                    organization);
            List<Announcement> unpinnedAnnouncements = AnnouncementFixture.createList(expectedUnpinnedSize, false,
                    organization);

            announcementJpaRepository.saveAll(pinnedAnnouncements);
            announcementJpaRepository.saveAll(unpinnedAnnouncements);

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
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
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Announcement announcement1 = AnnouncementFixture.create(true, organization);
            Announcement announcement2 = AnnouncementFixture.create(true, organization);
            Announcement announcement3 = AnnouncementFixture.create(true, organization);
            announcementJpaRepository.saveAll(List.of(announcement1, announcement2, announcement3));

            // when & then
            List<Long> result = RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .get("/announcements")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .jsonPath()
                    .getList("pinned.id", Long.class);

            assertSoftly(s -> {
                s.assertThat(result.get(0)).isEqualTo(announcement3.getId());
                s.assertThat(result.get(1)).isEqualTo(announcement2.getId());
                s.assertThat(result.get(2)).isEqualTo(announcement1.getId());
            });
        }

        @Test
        void 성공_서로_다른_조직() {
            // given
            Organization targetOrganization = OrganizationFixture.create();
            Organization anotherOrganization = OrganizationFixture.create();
            organizationJpaRepository.saveAll(List.of(targetOrganization, anotherOrganization));

            int expectedSize = 3;
            List<Announcement> targetAnnouncements = AnnouncementFixture.createList(expectedSize, true,
                    targetOrganization);
            announcementJpaRepository.saveAll(targetAnnouncements);

            int notExpectedSize = 4;
            List<Announcement> anotherAnnouncements = AnnouncementFixture.createList(notExpectedSize, true,
                    anotherOrganization);
            announcementJpaRepository.saveAll(anotherAnnouncements);

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, targetOrganization.getId())
                    .when()
                    .get("/announcements")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("pinned.size()", equalTo(expectedSize))
                    .body("pinned.id", containsInAnyOrder(
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
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Announcement announcement = AnnouncementFixture.create(organization);
            announcementJpaRepository.save(announcement);

            AnnouncementUpdateRequest request = AnnouncementUpdateRequestFixture.create("수정된 제목", "수정된 내용");

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .patch("/announcements/{announcementId}", announcement.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("title", equalTo(request.title()))
                    .body("content", equalTo(request.content()));
        }

        @Test
        void 실패_존재하지_않는_공지() {
            // given
            Long notExistId = 0L;
            AnnouncementUpdateRequest request = AnnouncementUpdateRequestFixture.create();

            // when & then
            RestAssured
                    .given()
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
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Announcement announcement = AnnouncementFixture.create(initialPinned, organization);
            announcementJpaRepository.save(announcement);

            AnnouncementPinUpdateRequest request = AnnouncementPinUpdateRequestFixture.create(expectedPinned);

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .patch("/announcements/{announcementId}/pin", announcement.getId())
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            Announcement updatedAnnouncement = announcementJpaRepository.findById(announcement.getId()).get();
            assertThat(updatedAnnouncement.isPinned()).isEqualTo(expectedPinned);
        }
    }

    @Nested
    class deleteAnnouncementByAnnouncementId {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Announcement announcement = AnnouncementFixture.create(organization);
            announcementJpaRepository.save(announcement);

            // when & then
            RestAssured
                    .given()
                    .when()
                    .delete("/announcements/{announcementId}", announcement.getId())
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        }

        @Test
        void 성공_존재하지_않는_공지지만_예외_없음() {
            // given
            Long notExistId = 0L;

            // when & then
            RestAssured
                    .given()
                    .when()
                    .delete("/announcements/{announcementId}", notExistId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        }
    }
}
