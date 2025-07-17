package com.daedan.festabook.organization.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import com.daedan.festabook.organization.dto.OrganizationGeographicResponse;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import io.restassured.RestAssured;
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
class OrganizationControllerTest {

    private static final String ORGANIZATION_HEADER_NAME = "organization";

    @Autowired
    private OrganizationJpaRepository organizationJpaRepository;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    class getOrganizationGeographicById {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            int expectedFieldSize = 3;

            OrganizationGeographicResponse expected = OrganizationGeographicResponse.from(organization);

            // when & then
            OrganizationGeographicResponse result = RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .get("/organizations/geography")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .extract()
                    .as(OrganizationGeographicResponse.class);

            assertThat(result).isEqualTo(expected);
        }

        @Test
        void 실패_존재하지_않는_조직() {
            // given
            Long notExistId = 0L;

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, notExistId)
                    .when()
                    .get("/organizations/geography")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }
    }
}
