package com.daedan.festabook.announcement.controller;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.verify;

import com.daedan.festabook.announcement.domain.Announcement;
import com.daedan.festabook.announcement.domain.AnnouncementFixture;
import com.daedan.festabook.announcement.dto.AnnouncementRequest;
import com.daedan.festabook.announcement.infrastructure.AnnouncementJpaRepository;
import com.daedan.festabook.notification.constants.TopicConstants;
import com.daedan.festabook.notification.dto.NotificationRequest;
import com.daedan.festabook.notification.service.NotificationService;
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
    private NotificationService notificationService;

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

            AnnouncementRequest announcementRequest = new AnnouncementRequest(
                    "폭우가 내립니다.",
                    "우산을 챙겨주세요.",
                    true
            );

            NotificationRequest notificationRequest = new NotificationRequest(
                    TopicConstants.getOrganizationTopicById(organization.getId()),
                    announcementRequest.title(),
                    announcementRequest.content()
            );

            int expectedFieldSize = 5;

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .contentType(ContentType.JSON)
                    .body(announcementRequest)
                    .when()
                    .post("/announcements")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("title", equalTo(announcementRequest.title()))
                    .body("content", equalTo(announcementRequest.content()))
                    .body("isPinned", equalTo(announcementRequest.isPinned()))
                    .body("createdAt", notNullValue());

            // then
            verify(notificationService).sendToTopic(notificationRequest);
        }
    }

    @Nested
    class getAllAnnouncementByOrganizationId {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Announcement announcement = AnnouncementFixture.create(organization);
            announcementJpaRepository.save(announcement);

            int expectedSize = 1;
            int expectedFieldSize = 5;

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .get("/announcements")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize))
                    .body("[0].size()", equalTo(expectedFieldSize))
                    .body("[0].id", equalTo(announcement.getId().intValue()))
                    .body("[0].title", equalTo(announcement.getTitle()))
                    .body("[0].content", equalTo(announcement.getContent()))
                    .body("[0].isPinned", equalTo(announcement.isPinned()))
                    .body("[0].createdAt", notNullValue());
        }

        @Test
        void 성공_여러_값_조회() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            int expectedSize = 3;
            List<Announcement> announcements = AnnouncementFixture.createList(expectedSize, organization);
            announcementJpaRepository.saveAll(announcements);

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .get("/announcements")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize))
                    .body("id", containsInAnyOrder(
                            announcements.get(0).getId().intValue(),
                            announcements.get(1).getId().intValue(),
                            announcements.get(2).getId().intValue()
                    ));
        }

        @Test
        void 성공_서로_다른_조직() {
            // given
            Organization targetOrganization = OrganizationFixture.create();
            Organization anotherOrganization = OrganizationFixture.create();
            organizationJpaRepository.saveAll(List.of(targetOrganization, anotherOrganization));

            int expectedSize = 3;
            List<Announcement> targetAnnouncements = AnnouncementFixture.createList(expectedSize, targetOrganization);
            announcementJpaRepository.saveAll(targetAnnouncements);

            int notExpectedSize = 4;
            List<Announcement> anotherAnnouncements = AnnouncementFixture.createList(notExpectedSize,
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
                    .body("$", hasSize(expectedSize))
                    .body("id", containsInAnyOrder(
                            targetAnnouncements.get(0).getId().intValue(),
                            targetAnnouncements.get(1).getId().intValue(),
                            targetAnnouncements.get(2).getId().intValue()
                    ));
        }
    }
}
