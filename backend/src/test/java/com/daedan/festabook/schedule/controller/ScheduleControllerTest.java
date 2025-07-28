package com.daedan.festabook.schedule.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.BDDMockito.given;

import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import com.daedan.festabook.schedule.domain.Event;
import com.daedan.festabook.schedule.domain.EventDate;
import com.daedan.festabook.schedule.domain.EventDateFixture;
import com.daedan.festabook.schedule.domain.EventFixture;
import com.daedan.festabook.schedule.domain.EventStatus;
import com.daedan.festabook.schedule.dto.EventDateRequest;
import com.daedan.festabook.schedule.dto.EventRequest;
import com.daedan.festabook.schedule.infrastructure.EventDateJpaRepository;
import com.daedan.festabook.schedule.infrastructure.EventJpaRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

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

            EventDateRequest request = new EventDateRequest(LocalDate.of(2025, 5, 5));
            int expectedFieldSize = 2;

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .body(request)
                    .when()
                    .post("/schedules")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("id", notNullValue())
                    .body("date", equalTo(request.date().toString()));
        }

        @Test
        void 예외_이미_존재하는_일정_날짜() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            LocalDate date = LocalDate.of(2025, 5, 5);
            eventDateJpaRepository.save(EventDateFixture.create(organization, date));
            EventDateRequest request = new EventDateRequest(date);

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .body(request)
                    .when()
                    .post("/schedules")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", equalTo("이미 존재하는 일정 날짜입니다."));
        }
    }

    @Nested
    class deleteEventDate {

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
                    .delete("/schedules/{eventDateId}", eventDate.getId())
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
                    .delete("/schedules/{eventDateId}", invalidEventDateId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
            assertSoftly(s -> {
                s.assertThat(eventDateJpaRepository.findById(invalidEventDateId)).isEmpty();
                s.assertThat(eventJpaRepository.findAllByEventDateId(invalidEventDateId)).isEmpty();
            });
        }
    }

    @Nested
    class createEvent {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            LocalDateTime dateTime = LocalDateTime.MAX;
            setFixedClock(dateTime);

            EventDate eventDate = EventDateFixture.create(organization);
            eventDateJpaRepository.save(eventDate);

            EventRequest request = new EventRequest(
                    LocalTime.of(1, 0),
                    LocalTime.of(2, 0),
                    "title",
                    "location",
                    eventDate.getId()
            );

            int expectedFieldSize = 6;

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .body(request)
                    .when()
                    .post("/schedules/events")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("id", notNullValue())
                    .body("status", notNullValue())
                    .body("startTime", equalTo(request.startTime().toString()))
                    .body("endTime", equalTo(request.endTime().toString()))
                    .body("title", equalTo(request.title()))
                    .body("location", equalTo(request.location()));
        }
    }

    @Nested
    class updateEvent {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            LocalDateTime dateTime = LocalDateTime.MAX;
            setFixedClock(dateTime);

            EventDate eventDate = EventDateFixture.create(organization);
            eventDateJpaRepository.save(eventDate);

            Event event = EventFixture.create(eventDate);
            eventJpaRepository.save(event);

            EventRequest request = new EventRequest(
                    LocalTime.of(3, 0),
                    LocalTime.of(4, 0),
                    "updated title",
                    "updated location",
                    eventDate.getId()
            );

            int expectedFieldSize = 6;

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .body(request)
                    .when()
                    .patch("/schedules/events/{eventId}", event.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("id", notNullValue())
                    .body("status", notNullValue())
                    .body("startTime", equalTo(request.startTime().toString()))
                    .body("endTime", equalTo(request.endTime().toString()))
                    .body("title", equalTo(request.title()))
                    .body("location", equalTo(request.location()));
        }
    }

    @Nested
    class deleteEvent {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            EventDate eventDate = EventDateFixture.create(organization);
            eventDateJpaRepository.save(eventDate);

            Event event = EventFixture.create(eventDate);
            eventJpaRepository.save(event);

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .delete("/schedules/events/{eventId}", event.getId())
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
            assertThat(eventJpaRepository.findById(event.getId())).isEmpty();
        }

        @Test
        void 성공_존재하지_않는_일정_ID() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Long invalidEventId = 0L;

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .delete("/schedules/events/{eventId}", invalidEventId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
            assertThat(eventJpaRepository.findById(invalidEventId)).isEmpty();
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
                    .body("date", contains(expectedSortedDates.toArray()));
        }
    }

    @Nested
    class getAllEventByEventDateId {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            LocalDateTime dateTime = LocalDateTime.of(2025, 5, 20, 15, 0);
            setFixedClock(dateTime);

            EventDate eventDate = EventDateFixture.create(organization, dateTime.toLocalDate());
            eventDateJpaRepository.save(eventDate);

            Event event = EventFixture.create(
                    dateTime.toLocalTime().minusHours(2), // 시작 시간, 13:00
                    dateTime.toLocalTime().plusHours(2), // 종료 시간, 17:00
                    eventDate
            );
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
                    .body("[0].status", equalTo(EventStatus.ONGOING.name()))
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

            setFixedClock(LocalDateTime.now());

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

            setFixedClock(LocalDateTime.now());

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

        @ParameterizedTest(name = "날짜: {0}, 시작 시간: {1}, 종료 시간: {2}, 결과: {3}")
        @CsvSource({
                "2025-05-04, 10:00, 12:00, COMPLETED",   // 종료
                "2025-05-05, 14:00, 17:00, ONGOING",     // 진행중
                "2025-05-06, 10:00, 12:00, UPCOMING"     // 예정
        })
        void 성공_이벤트_상태_기반(LocalDate date, LocalTime startTime, LocalTime endTime, EventStatus status) {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            LocalDateTime dateTime = LocalDateTime.of(2025, 5, 5, 15, 0);
            setFixedClock(dateTime);

            EventDate eventDate = EventDateFixture.create(organization, date);
            eventDateJpaRepository.save(eventDate);

            Event event = EventFixture.create(
                    startTime,
                    endTime,
                    eventDate
            );
            eventJpaRepository.save(event);

            // when & then
            RestAssured
                    .given()
                    .when()
                    .get("/schedules/{eventDateId}", eventDate.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("[0].status", equalTo(status.name()));
        }
    }

    private void setFixedClock(LocalDateTime dateTime) {
        ZoneId korea = ZoneId.of("Asia/Seoul");
        Clock fixedClock = Clock.fixed(
                dateTime.atZone(korea).toInstant(),
                korea
        );

        given(clock.instant())
                .willReturn(fixedClock.instant());
        given(clock.getZone())
                .willReturn(fixedClock.getZone());
    }
}
