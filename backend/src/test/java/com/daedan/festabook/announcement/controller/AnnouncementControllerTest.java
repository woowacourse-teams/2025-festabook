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
import org.springframework.test.context.jdbc.Sql;

@Sql(value = "classpath:/reset_auto_increment.sql")
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
            Long organizationId = 2L;

            Organization seoul = new Organization("서울대학교");
            Organization woowa = new Organization("우아한대학교");

            Announcement announcement1 = new Announcement("서울대학교입니다.", "테스트", false, seoul);
            Announcement announcement2 = new Announcement("우아한대학교입니다.", "테스트", false, woowa);

            organizationJpaRepository.saveAll(List.of(seoul, woowa));
            announcementJpaRepository.saveAll(List.of(announcement1, announcement2));

            // when & then
            RestAssured.given()
                    .header(ORGANIZATION_HEADER_NAME, organizationId)
                    .when()
                    .get("/announcements")
                    .then()
                    .statusCode(200)
                    .body("size()", equalTo(1))
                    .body("[0].title", equalTo("우아한대학교입니다."));
        }
    }
}
