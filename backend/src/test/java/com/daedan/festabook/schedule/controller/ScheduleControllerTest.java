package com.daedan.festabook.schedule.controller;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import com.daedan.festabook.schedule.domain.Event;
import com.daedan.festabook.schedule.domain.EventDate;
import com.daedan.festabook.schedule.domain.EventDateFixture;
import com.daedan.festabook.schedule.domain.EventFixture;
import com.daedan.festabook.schedule.infrastructure.EventDateJpaRepository;
import com.daedan.festabook.schedule.infrastructure.EventJpaRepository;
import io.restassured.RestAssured;
import java.time.LocalDate;
import java.time.LocalTime;
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
class ScheduleControllerTest {

    private static final String ORGANIZATION_HEADER_NAME = "organization";

    @Autowired
    private OrganizationJpaRepository organizationJpaRepository;

    @Autowired
    private EventDateJpaRepository eventDateJpaRepository;

    @Autowired
    private EventJpaRepository eventJpaRepository;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
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
                    .get("/schedules")
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
                    .get("/schedules")
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
                    .get("/schedules")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("date", contains(
                            expectedSortedDates.toArray()
                    ));
        }
    }

    @Nested
    class getAllEventByEventDateId {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            EventDate eventDate = EventDateFixture.create(organization);
            eventDateJpaRepository.save(eventDate);

            Event event = EventFixture.create(eventDate);
            eventJpaRepository.save(event);

            int expectedSize = 1;
            int expectedFieldSize = 6;

            // when & then
            RestAssured
                    .given()
                    .when()
                    .get("/schedules/{eventDateId}", eventDate.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize))
                    .body("[0].size()", equalTo(expectedFieldSize))
                    .body("[0].id", equalTo(event.getId().intValue()))
                    .body("[0].status", equalTo(event.getStatus().name()))
                    .body("[0].startTime", equalTo(event.getStartTime().toString()))
                    .body("[0].endTime", equalTo(event.getEndTime().toString()))
                    .body("[0].title", equalTo(event.getTitle()))
                    .body("[0].location", equalTo(event.getLocation()));
        }

        @Test
        void 성공_특정_날짜_ID_기반() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            EventDate eventDate = EventDateFixture.create(organization, LocalDate.now());
            EventDate otherEventDate = EventDateFixture.create(organization, LocalDate.now().plusDays(1));
            eventDateJpaRepository.saveAll(List.of(eventDate, otherEventDate));

            int expectedSize = 3;
            List<Event> events = EventFixture.createList(expectedSize, eventDate);
            List<Event> otherEvents = EventFixture.createList(5, otherEventDate);
            eventJpaRepository.saveAll(events);
            eventJpaRepository.saveAll(otherEvents);

            // when & then
            RestAssured
                    .given()
                    .when()
                    .get("/schedules/{eventDateId}", eventDate.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize));
        }

        @Test
        void 성공_시작_시간_오름차순() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            EventDate eventDate = EventDateFixture.create(organization);
            eventDateJpaRepository.save(eventDate);

            List<Event> events = List.of(
                    EventFixture.create(LocalTime.of(15, 0), LocalTime.of(18, 0), eventDate),
                    EventFixture.create(LocalTime.of(12, 0), LocalTime.of(15, 0), eventDate),
                    EventFixture.create(LocalTime.of(12, 0), LocalTime.of(16, 0), eventDate)
            );
            eventJpaRepository.saveAll(events);

            List<String> expectedStartTime = List.of("12:00", "12:00", "15:00");
            List<String> expectedEndTime = List.of("15:00", "16:00", "18:00");

            // when & then
            RestAssured
                    .given()
                    .when()
                    .get("/schedules/{eventDateId}", eventDate.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("startTime", equalTo(expectedStartTime))
                    .body("endTime", equalTo(expectedEndTime));
        }
    }
}
