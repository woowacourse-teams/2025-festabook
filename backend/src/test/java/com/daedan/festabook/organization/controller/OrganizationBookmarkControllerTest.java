package com.daedan.festabook.organization.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.daedan.festabook.device.domain.Device;
import com.daedan.festabook.device.domain.DeviceFixture;
import com.daedan.festabook.device.infrastructure.DeviceJpaRepository;
import com.daedan.festabook.notification.service.NotificationService;
import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationBookmark;
import com.daedan.festabook.organization.domain.OrganizationBookmarkFixture;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import com.daedan.festabook.organization.dto.OrganizationBookmarkRequest;
import com.daedan.festabook.organization.infrastructure.OrganizationBookmarkJpaRepository;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
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
class OrganizationBookmarkControllerTest {

    private static final String ORGANIZATION_HEADER_NAME = "organization";

    @Autowired
    private OrganizationJpaRepository organizationJpaRepository;

    @Autowired
    private DeviceJpaRepository deviceJpaRepository;

    @Autowired
    private OrganizationBookmarkJpaRepository organizationBookmarkJpaRepository;

    @MockitoBean
    private NotificationService notificationService;

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    class createOrganizationBookmark {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Device device = DeviceFixture.create();
            deviceJpaRepository.save(device);

            OrganizationBookmarkRequest request = new OrganizationBookmarkRequest(device.getId());

            int expectedFieldSize = 1;

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/organizations/bookmarks/" + organization.getId())
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("id", notNullValue());
        }

        @Test
        void 예외_존재하지_않는_디바이스() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Long invalidDeviceId = 0L;
            OrganizationBookmarkRequest request = new OrganizationBookmarkRequest(invalidDeviceId);

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/organizations/bookmarks/" + organization.getId())
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        void 예외_존재하지_않는_조직() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Device device = DeviceFixture.create();
            deviceJpaRepository.save(device);

            Long invalidOrgId = 0L;
            OrganizationBookmarkRequest request = new OrganizationBookmarkRequest(device.getId());

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/organizations/bookmarks/" + invalidOrgId)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    @Nested
    class deleteOrganizationBookmark {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Device device = DeviceFixture.create();
            deviceJpaRepository.save(device);

            OrganizationBookmark bookmark = OrganizationBookmarkFixture.create(organization, device);
            organizationBookmarkJpaRepository.save(bookmark);

            OrganizationBookmarkRequest request = new OrganizationBookmarkRequest(device.getId());

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .delete("/organizations/bookmarks/" + organization.getId())
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        }

        @Test
        void 예외_존재하지_않는_디바이스() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Long invalidDeviceId = 0L;
            OrganizationBookmarkRequest request = new OrganizationBookmarkRequest(invalidDeviceId);

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .delete("/organizations/bookmarks/" + organization.getId())
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }
}
