package com.daedan.festabook.event.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.BDDMockito.given;

import com.daedan.festabook.event.domain.Event;
import com.daedan.festabook.event.domain.EventDate;
import com.daedan.festabook.event.domain.EventDateFixture;
import com.daedan.festabook.event.domain.EventFixture;
import com.daedan.festabook.event.domain.EventStatus;
import com.daedan.festabook.event.dto.EventRequest;
import com.daedan.festabook.event.dto.EventRequestFixture;
import com.daedan.festabook.event.dto.EventUpdateRequest;
import com.daedan.festabook.event.dto.EventUpdateRequestFixture;
import com.daedan.festabook.event.infrastructure.EventDateJpaRepository;
import com.daedan.festabook.event.infrastructure.EventJpaRepository;
import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.global.security.JwtTestHelper;
import com.daedan.festabook.global.security.role.RoleType;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
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
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class EventControllerTest {

    @Autowired
    private EventDateJpaRepository eventDateJpaRepository;

    @Autowired
    private EventJpaRepository eventJpaRepository;

    @Autowired
    private FestivalJpaRepository festivalJpaRepository;

    @Autowired
    private JwtTestHelper jwtTestHelper;

    @MockitoBean
    private Clock clock;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    class createEvent {

        @ParameterizedTest
        @EnumSource(RoleType.class)
        void 성공(RoleType roleType) {
            // given
            setFixedClock(LocalDateTime.now());

            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createAuthorizationHeaderWithRole(festival, roleType);

            EventDate eventDate = EventDateFixture.create(festival);
            eventDateJpaRepository.save(eventDate);

            EventRequest request = EventRequestFixture.create(eventDate.getId());

            int expectedSize = 6;

            // when & then
            RestAssured
                    .given()
                    .header(authorizationHeader)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/event-dates/events")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("size()", equalTo(expectedSize))
                    .body("eventId", notNullValue())
                    .body("status", notNullValue())
                    .body("startTime", equalTo(request.startTime().toString()))
                    .body("endTime", equalTo(request.endTime().toString()))
                    .body("title", equalTo(request.title()))
                    .body("location", equalTo(request.location()));
        }
    }

    @Nested
    class updateEvent {

        @ParameterizedTest
        @EnumSource(RoleType.class)
        void 성공(RoleType roleType) {
            // given
            setFixedClock(LocalDateTime.now());

            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createAuthorizationHeaderWithRole(festival, roleType);

            EventDate eventDate = EventDateFixture.create(festival);
            eventDateJpaRepository.save(eventDate);

            Event event = EventFixture.create(eventDate);
            eventJpaRepository.save(event);
            Long eventId = event.getId();

            EventUpdateRequest request = EventUpdateRequestFixture.create(
                    eventDate.getId(),
                    LocalTime.of(3, 0),
                    LocalTime.of(4, 0),
                    "updated title",
                    "updated location"
            );

            int expectedSize = 6;

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .header(authorizationHeader)
                    .body(request)
                    .when()
                    .patch("/event-dates/events/{eventId}", eventId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("size()", equalTo(expectedSize))
                    .body("eventId", equalTo(eventId.intValue()))
                    .body("status", notNullValue())
                    .body("startTime", equalTo(request.startTime().toString()))
                    .body("endTime", equalTo(request.endTime().toString()))
                    .body("title", equalTo(request.title()))
                    .body("location", equalTo(request.location()));
        }
    }

    @Nested
    class deleteEventByEventId {

        @ParameterizedTest
        @EnumSource(RoleType.class)
        void 성공(RoleType roleType) {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createAuthorizationHeaderWithRole(festival, roleType);

            EventDate eventDate = EventDateFixture.create(festival);
            eventDateJpaRepository.save(eventDate);

            Event event = EventFixture.create(eventDate);
            eventJpaRepository.save(event);

            // when & then
            RestAssured
                    .given()
                    .header(authorizationHeader)
                    .when()
                    .delete("/event-dates/events/{eventId}", event.getId())
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
            assertThat(eventJpaRepository.findById(event.getId())).isEmpty();
        }
    }

    @Nested
    class getAllEventByEventDateId {

        @Test
        void 성공() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            LocalDateTime dateTime = LocalDateTime.of(2025, 5, 20, 15, 0);
            setFixedClock(dateTime);

            EventDate eventDate = EventDateFixture.create(festival, dateTime.toLocalDate());
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
                    .get("/event-dates/{eventDateId}/events", eventDate.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize))
                    .body("[0].size()", equalTo(expectedFieldSize))
                    .body("[0].eventId", equalTo(event.getId().intValue()))
                    .body("[0].status", equalTo(EventStatus.ONGOING.name()))
                    .body("[0].startTime", equalTo(event.getStartTime().toString()))
                    .body("[0].endTime", equalTo(event.getEndTime().toString()))
                    .body("[0].title", equalTo(event.getTitle()))
                    .body("[0].location", equalTo(event.getLocation()));
        }

        @Test
        void 성공_특정_날짜_ID_기반() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            EventDate eventDate = EventDateFixture.create(festival, LocalDate.now());
            EventDate otherEventDate = EventDateFixture.create(festival, LocalDate.now().plusDays(1));
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
                    .get("/event-dates/{eventDateId}/events", eventDate.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize));
        }

        @Test
        void 성공_시작_시간_오름차순() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            EventDate eventDate = EventDateFixture.create(festival);
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
                    .get("/event-dates/{eventDateId}/events", eventDate.getId())
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
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            LocalDateTime dateTime = LocalDateTime.of(2025, 5, 5, 15, 0);
            setFixedClock(dateTime);

            EventDate eventDate = EventDateFixture.create(festival, date);
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
                    .get("/event-dates/{eventDateId}/events", eventDate.getId())
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
