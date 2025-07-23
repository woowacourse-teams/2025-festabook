package com.daedan.festabook.announcement.controller;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;

import com.daedan.festabook.announcement.domain.Announcement;
import com.daedan.festabook.announcement.domain.AnnouncementFixture;
import com.daedan.festabook.announcement.dto.AnnouncementRequest;
import com.daedan.festabook.announcement.infrastructure.AnnouncementJpaRepository;
import com.daedan.festabook.notification.infrastructure.FcmNotificationManager;
import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDateTime;
import java.util.List;
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
                    .body("pinned.size()", equalTo(expectedPinnedSize))
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
            List<String> dateTime = RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .get("/announcements")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .jsonPath()
                    .getList("pinned.createdAt", String.class);

            List<LocalDateTime> result = dateTime.stream()
                    .map(LocalDateTime::parse)
                    .toList();

            System.out.println("===== createdAt 출력 시작 =====");
            for (int i = 0; i < result.size(); i++) {
                System.out.println("pinned[" + i + "] createdAt: " + result.get(i));
            }
            System.out.println("announcement1: " + announcement1.getCreatedAt());
            System.out.println("announcement2: " + announcement2.getCreatedAt());
            System.out.println("announcement3: " + announcement3.getCreatedAt());
            System.out.println("===== createdAt 출력 끝 =====");

            assertSoftly(s -> {
                s.assertThat(result.get(0)).isEqualTo(announcement3.getCreatedAt());
                s.assertThat(result.get(1)).isEqualTo(announcement2.getCreatedAt());
                s.assertThat(result.get(2)).isEqualTo(announcement1.getCreatedAt());
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
}
