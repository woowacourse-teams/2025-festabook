package com.daedan.festabook.event.controller;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import com.daedan.festabook.event.domain.Event;
import com.daedan.festabook.event.domain.EventDate;
import com.daedan.festabook.event.domain.EventDateFixture;
import com.daedan.festabook.event.domain.EventFixture;
import com.daedan.festabook.event.dto.EventDateRequest;
import com.daedan.festabook.event.dto.EventDateRequestFixture;
import com.daedan.festabook.event.infrastructure.EventDateJpaRepository;
import com.daedan.festabook.event.infrastructure.EventJpaRepository;
import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.Clock;
import java.time.LocalDate;
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
class EventDateControllerTest {

    private static final String ORGANIZATION_HEADER_NAME = "organization";

    @Autowired
    private EventDateJpaRepository eventDateJpaRepository;

    @Autowired
    private EventJpaRepository eventJpaRepository;

    @Autowired
    private OrganizationJpaRepository organizationJpaRepository;

    @MockitoBean
    private Clock clock;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    class createEventDate {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            EventDateRequest request = EventDateRequestFixture.create();

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .body(request)
                    .when()
                    .post("/event-dates")
                    .then()
                    .statusCode(HttpStatus.CREATED.value());
        }

        @Test
        void 예외_이미_존재하는_일정_날짜() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            EventDateRequest request = EventDateRequestFixture.create();
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .body(request)
                    .when()
                    .post("/event-dates")
                    .then()
                    .statusCode(HttpStatus.CREATED.value());

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .body(request)
                    .when()
                    .post("/event-dates")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", equalTo("이미 존재하는 일정 날짜입니다."));
        }
    }

    @Nested
    class updateEventDate {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            EventDate eventDate = EventDateFixture.create(organization);
            eventDateJpaRepository.save(eventDate);

            EventDateRequest request = EventDateRequestFixture.create(eventDate.getDate().plusDays(1));

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .body(request)
                    .when()
                    .patch("/event-dates/{eventDateId}", eventDate.getId())
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            assertSoftly(s -> {
                EventDate updatedEventDate = eventDateJpaRepository.findById(eventDate.getId()).orElseThrow();
                s.assertThat(updatedEventDate.getDate()).isEqualTo(request.date());
            });
        }

        @Test
        void 성공_존재하지_않는_일정_날짜_ID_204_응답() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Long notExistingEventDateId = 0L;
            EventDateRequest request = EventDateRequestFixture.create();

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .body(request)
                    .when()
                    .patch("/event-dates/{eventDateId}", notExistingEventDateId)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", equalTo("존재하지 않는 일정 날짜입니다."));
        }
    }

    @Nested
    class deleteEventDateByEventDateId {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            EventDate eventDate = EventDateFixture.create(organization);
            eventDateJpaRepository.save(eventDate);

            List<Event> events = EventFixture.createList(3, eventDate);
            eventJpaRepository.saveAll(events);

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .delete("/event-dates/{eventDateId}", eventDate.getId())
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
            assertSoftly(s -> {
                s.assertThat(eventDateJpaRepository.findById(eventDate.getId())).isEmpty();
                s.assertThat(eventJpaRepository.findAllByEventDateId(eventDate.getId())).isEmpty();
            });
        }

        @Test
        void 성공_존재하지_않는_일정_날짜_ID() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Long invalidEventDateId = 0L;

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .delete("/event-dates/{eventDateId}", invalidEventDateId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
            assertSoftly(s -> {
                s.assertThat(eventDateJpaRepository.findById(invalidEventDateId)).isEmpty();
                s.assertThat(eventJpaRepository.findAllByEventDateId(invalidEventDateId)).isEmpty();
            });
        }
    }

    @Nested
    class getAllEventDateByOrganizationId {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            EventDate eventDate = EventDateFixture.create(organization);
            eventDateJpaRepository.save(eventDate);

            int expectedSize = 1;
            int expectedFieldSize = 2;

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .get("/event-dates")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize))
                    .body("[0].size()", equalTo(expectedFieldSize))
                    .body("[0].id", equalTo(eventDate.getId().intValue()))
                    .body("[0].date", equalTo(eventDate.getDate().toString()));
        }

        @Test
        void 성공_조직_ID_기반() {
            // given
            Organization organization = OrganizationFixture.create("우리 학교");
            Organization otherOrganization = OrganizationFixture.create("다른 학교");
            organizationJpaRepository.saveAll(List.of(organization, otherOrganization));

            int expectedSize = 4;
            List<EventDate> eventDates = EventDateFixture.createList(expectedSize, organization);
            List<EventDate> otherEventDates = EventDateFixture.createList(5, otherOrganization);
            eventDateJpaRepository.saveAll(eventDates);
            eventDateJpaRepository.saveAll(otherEventDates);

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .get("/event-dates")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize));
        }

        @Test
        void 성공_날짜_오름차순() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            List<LocalDate> dates = List.of(
                    LocalDate.of(2025, 7, 20),
                    LocalDate.of(2025, 7, 18),
                    LocalDate.of(2025, 7, 22),
                    LocalDate.of(2025, 7, 19)
            );

            List<EventDate> eventDates = EventDateFixture.createList(dates, organization);
            eventDateJpaRepository.saveAll(eventDates);

            List<String> expectedSortedDates = dates.stream()
                    .sorted()
                    .map(LocalDate::toString)
                    .toList();

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .get("/event-dates")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("date", contains(expectedSortedDates.toArray()));
        }
    }
}
