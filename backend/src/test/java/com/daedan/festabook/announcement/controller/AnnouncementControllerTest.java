package com.daedan.festabook.announcement.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

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

            // when & then
            RestAssured.given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .get("/announcements")
                    .then()
                    .statusCode(200)
                    .body("size()", equalTo(1))
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

            Announcement announcement1 = AnnouncementFixture.create(organization);
            Announcement announcement2 = AnnouncementFixture.create(organization);
            Announcement announcement3 = AnnouncementFixture.create(organization);
            announcementJpaRepository.saveAll(List.of(announcement1, announcement2, announcement3));

            // when & then
            RestAssured.given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .get("/announcements")
                    .then()
                    .statusCode(200)
                    .body("size()", equalTo(3))
                    .body("id", hasItem(announcement1.getId().intValue()))
                    .body("id", hasItem(announcement2.getId().intValue()))
                    .body("id", hasItem(announcement3.getId().intValue()));
        }

        @Test
        void 성공_서로_다른_조직() {
            // given
            Organization targetOrganization = organizationJpaRepository.save(OrganizationFixture.create());
            Organization anotherOrganization = organizationJpaRepository.save(OrganizationFixture.create());

            Announcement targetAnnouncement = AnnouncementFixture.create(targetOrganization);
            Announcement anotherAnnouncement = AnnouncementFixture.create(anotherOrganization);
            announcementJpaRepository.saveAll(List.of(anotherAnnouncement, targetAnnouncement));

            // when & then
            RestAssured.given()
                    .header(ORGANIZATION_HEADER_NAME, targetOrganization.getId())
                    .when()
                    .get("/announcements")
                    .then()
                    .statusCode(200)
                    .body("size()", equalTo(1))
                    .body("[0].id", equalTo(targetAnnouncement.getId().intValue()));
        }
    }
}
