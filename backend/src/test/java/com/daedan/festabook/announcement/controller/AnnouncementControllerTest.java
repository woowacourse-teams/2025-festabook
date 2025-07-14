package com.daedan.festabook.announcement.controller;

import static org.hamcrest.Matchers.equalTo;

import com.daedan.festabook.announcement.domain.Announcement;
import com.daedan.festabook.announcement.infrastructure.AnnouncementJpaRepository;
import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import io.restassured.RestAssured;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
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
        organizationJpaRepository.deleteAll();
        announcementJpaRepository.deleteAll();
    }

    @Nested
    class getAllAnnouncementByOrganizationId {

        @Test
        void 성공() {
            // given
            Organization seoul = new Organization("서울대학교");
            Organization woowa = new Organization("우아한대학교");

            Announcement seoulAnnouncement = new Announcement("서울대학교입니다.", "서울테스트", true, seoul);
            Announcement woowaAnnouncement = new Announcement("우아한대학교입니다.", "우아한테스트", false, woowa);

            organizationJpaRepository.saveAll(List.of(seoul, woowa));
            announcementJpaRepository.saveAll(List.of(seoulAnnouncement, woowaAnnouncement));

            // when & then
            RestAssured.given()
                    .header(ORGANIZATION_HEADER_NAME, woowa.getId())
                    .when()
                    .get("/announcements")
                    .then()
                    .statusCode(200)
                    .body("size()", equalTo(1))
                    .body("[0].id", equalTo(woowaAnnouncement.getId().intValue()))
                    .body("[0].title", equalTo(woowaAnnouncement.getTitle()))
                    .body("[0].content", equalTo(woowaAnnouncement.getContent()))
                    .body("[0].isPinned", equalTo(woowaAnnouncement.isPinned()))
                    .body("[0].createdAt", equalTo(woowaAnnouncement.getCreatedAt().toString()));
        }
    }
}
