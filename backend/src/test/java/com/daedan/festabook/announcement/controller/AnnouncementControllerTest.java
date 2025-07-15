package com.daedan.festabook.announcement.controller;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import com.daedan.festabook.announcement.domain.Announcement;
import com.daedan.festabook.announcement.domain.AnnouncementFixture;
import com.daedan.festabook.announcement.infrastructure.AnnouncementJpaRepository;
import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import io.restassured.RestAssured;
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

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AnnouncementControllerTest {

    private static final String ORGANIZATION_HEADER_NAME = "organization";

    @Autowired
    private AnnouncementJpaRepository announcementJpaRepository;

    @Autowired
    private OrganizationJpaRepository organizationJpaRepository;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    class getAllAnnouncementByOrganizationId {

        @Test
        void 성공() {
            // given
            Organization organization = organizationJpaRepository.save(OrganizationFixture.create());

            Announcement announcement = announcementJpaRepository.save(AnnouncementFixture.create(organization));

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
                    .body("[0].createdAt", equalTo(announcement.getCreatedAt().toString()));
        }

        @Test
        void 성공_여러_값_조회() {
            // given
            Organization organization = organizationJpaRepository.save(OrganizationFixture.create());

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
            Organization targetOrganization = organizationJpaRepository.save(OrganizationFixture.create());
            Organization anotherOrganization = organizationJpaRepository.save(OrganizationFixture.create());

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
