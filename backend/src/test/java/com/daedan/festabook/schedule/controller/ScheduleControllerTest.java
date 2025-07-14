package com.daedan.festabook.schedule.controller;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

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

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ScheduleControllerTest {

    private static final String ORGANIZATION_HEADER_NAME = "organization";

    @LocalServerPort
    private int port;

    @Autowired
    private OrganizationJpaRepository organizationJpaRepository;

    @Autowired
    private EventDateJpaRepository eventDateJpaRepository;

    @Autowired
    private EventJpaRepository eventJpaRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    class getAllEventDateByOrganizationId {

        @Test
        void 성공_조직_ID_기반() {
            // given
            Organization organization = OrganizationFixture.create("우리 학교");
            Organization otherOrganization = OrganizationFixture.create("다른 학교");

            organizationJpaRepository.saveAll(List.of(organization, otherOrganization));

            eventDateJpaRepository.saveAll(EventDateFixture.createList(4, organization));
            eventDateJpaRepository.saveAll(EventDateFixture.createList(5, otherOrganization));

            // when & then
            RestAssured.given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .get("/schedules")
                    .then()
                    .statusCode(200)
                    .body("size()", is(4));
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

            List<EventDate> eventDates = dates.stream()
                    .map(date -> EventDateFixture.create(organization, date))
                    .toList();
            eventDateJpaRepository.saveAll(eventDates);

            List<String> expectedSortedDates = dates.stream()
                    .sorted()
                    .map(LocalDate::toString)
                    .toList();

            // when & then
            RestAssured.given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .get("/schedules")
                    .then()
                    .statusCode(200)
                    .body("size()", is(4))
                    .body("date", contains(
                            expectedSortedDates.toArray()
                    ));
        }
    }

    @Nested
    class getAllEventByEventDateId {

        @Test
        void 성공_특정_날짜_ID_기반() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            EventDate eventDate = EventDateFixture.create(organization, LocalDate.now());
            EventDate otherEventDate = EventDateFixture.create(organization, LocalDate.now().plusDays(1));
            eventDateJpaRepository.saveAll(List.of(eventDate, otherEventDate));

            eventJpaRepository.saveAll(EventFixture.createList(3, eventDate));
            eventJpaRepository.saveAll(EventFixture.createList(5, otherEventDate));

            // when & then
            RestAssured.given()
                    .when()
                    .get("/schedules/" + eventDate.getId())
                    .then()
                    .statusCode(200)
                    .body("size()", is(3));
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

            // when & then
            RestAssured.given()
                    .when()
                    .get("/schedules/" + eventDate.getId())
                    .then()
                    .statusCode(200)
                    .body("startTime", contains(
                            "12:00", "12:00", "15:00"
                    ))
                    .body("endTime", contains(
                            "15:00", "16:00", "18:00"
                    ));
        }
    }
}
