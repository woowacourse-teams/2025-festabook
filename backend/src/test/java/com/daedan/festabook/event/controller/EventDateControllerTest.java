package com.daedan.festabook.event.controller;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import com.daedan.festabook.event.domain.Event;
import com.daedan.festabook.event.domain.EventDate;
import com.daedan.festabook.event.domain.EventDateFixture;
import com.daedan.festabook.event.domain.EventFixture;
import com.daedan.festabook.event.dto.EventDateRequest;
import com.daedan.festabook.event.dto.EventDateRequestFixture;
import com.daedan.festabook.event.dto.EventDateUpdateRequest;
import com.daedan.festabook.event.dto.EventDateUpdateRequestFixture;
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
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class EventDateControllerTest {

    private static final String FESTIVAL_HEADER_NAME = "festival";

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
    class createEventDate {

        @ParameterizedTest
        @EnumSource(RoleType.class)
        void 성공(RoleType roleType) {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createAuthorizationHeaderWithRole(festival, roleType);

            EventDateRequest request = EventDateRequestFixture.create();

            int expectedFieldSize = 2;

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .header(authorizationHeader)
                    .body(request)
                    .when()
                    .post("/event-dates")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("eventDateId", notNullValue())
                    .body("date", equalTo(request.date().toString()));
        }

        @Test
        void 예외_이미_존재하는_일정_날짜() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createCouncilAuthorizationHeader(festival);

            EventDate existingEventDate = EventDateFixture.create(festival);
            eventDateJpaRepository.save(existingEventDate);

            EventDateRequest request = EventDateRequestFixture.create(existingEventDate.getDate());

            // when & then
            RestAssured
                    .given()
                    .header(authorizationHeader)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/event-dates")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", equalTo("이미 존재하는 일정 날짜입니다."));
        }
    }

    @Nested
    class getAllEventDateByFestivalId {

        @Test
        void 성공() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            EventDate eventDate = EventDateFixture.create(festival);
            eventDateJpaRepository.save(eventDate);

            int expectedSize = 1;
            int expectedFieldSize = 2;

            // when & then
            RestAssured
                    .given()
                    .header(FESTIVAL_HEADER_NAME, festival.getId())
                    .when()
                    .get("/event-dates")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize))
                    .body("[0].size()", equalTo(expectedFieldSize))
                    .body("[0].eventDateId", equalTo(eventDate.getId().intValue()))
                    .body("[0].date", equalTo(eventDate.getDate().toString()));
        }

        @Test
        void 성공_축제_ID_기반() {
            // given
            Festival festival = FestivalFixture.create("우리 학교");
            Festival otherFestival = FestivalFixture.create("다른 학교");
            festivalJpaRepository.saveAll(List.of(festival, otherFestival));

            int expectedSize = 4;
            List<EventDate> eventDates = EventDateFixture.createList(expectedSize, festival);
            int otherSize = 5;
            List<EventDate> otherEventDates = EventDateFixture.createList(otherSize, otherFestival);
            eventDateJpaRepository.saveAll(eventDates);
            eventDateJpaRepository.saveAll(otherEventDates);

            // when & then
            RestAssured
                    .given()
                    .header(FESTIVAL_HEADER_NAME, festival.getId())
                    .when()
                    .get("/event-dates")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize));
        }

        @Test
        void 성공_날짜_오름차순() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            List<LocalDate> dates = List.of(
                    LocalDate.of(2025, 7, 20),
                    LocalDate.of(2025, 7, 18),
                    LocalDate.of(2025, 7, 22),
                    LocalDate.of(2025, 7, 19)
            );

            List<EventDate> eventDates = EventDateFixture.createList(dates, festival);
            eventDateJpaRepository.saveAll(eventDates);

            List<String> expectedSortedDates = dates.stream()
                    .sorted()
                    .map(LocalDate::toString)
                    .toList();

            // when & then
            RestAssured
                    .given()
                    .header(FESTIVAL_HEADER_NAME, festival.getId())
                    .when()
                    .get("/event-dates")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("date", contains(expectedSortedDates.toArray()));
        }
    }

    @Nested
    class updateEventDate {

        @ParameterizedTest
        @EnumSource(RoleType.class)
        void 성공(RoleType roleType) {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createAuthorizationHeaderWithRole(festival, roleType);

            EventDate eventDate = EventDateFixture.create(festival);
            eventDateJpaRepository.save(eventDate);

            EventDateUpdateRequest request = EventDateUpdateRequestFixture.create(eventDate.getDate().plusDays(1));

            int expectedFieldSize = 2;

            // when & then
            RestAssured
                    .given()
                    .header(authorizationHeader)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .patch("/event-dates/{eventDateId}", eventDate.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("eventDateId", equalTo(eventDate.getId().intValue()))
                    .body("date", equalTo(request.date().toString()));
        }

        @Test
        void 성공_존재하지_않는_일정_날짜_ID_400_응답() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createCouncilAuthorizationHeader(festival);

            Long notExistingEventDateId = 0L;
            EventDateUpdateRequest request = EventDateUpdateRequestFixture.create();

            // when & then
            RestAssured
                    .given()
                    .header(authorizationHeader)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .patch("/event-dates/{eventDateId}", notExistingEventDateId)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", equalTo("존재하지 않는 일정 날짜입니다."));
        }

        @Test
        void 예외_이미_존재하는_일정_날짜() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createCouncilAuthorizationHeader(festival);

            List<EventDate> eventDates = EventDateFixture.createList(2, festival);
            eventDateJpaRepository.saveAll(eventDates);

            EventDateUpdateRequest request = EventDateUpdateRequestFixture.create(eventDates.get(1).getDate());

            // when & then
            RestAssured
                    .given()
                    .header(authorizationHeader)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .patch("/event-dates/{eventDateId}", eventDates.get(0).getId())
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", equalTo("이미 존재하는 일정 날짜입니다."));
        }
    }

    @Nested
    class deleteEventDateByEventDateId {

        @ParameterizedTest
        @EnumSource(RoleType.class)
        void 성공(RoleType roleType) {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createAuthorizationHeaderWithRole(festival, roleType);

            EventDate eventDate = EventDateFixture.create(festival);
            eventDateJpaRepository.save(eventDate);

            List<Event> events = EventFixture.createList(3, eventDate);
            eventJpaRepository.saveAll(events);

            // when & then
            RestAssured
                    .given()
                    .header(authorizationHeader)
                    .when()
                    .delete("/event-dates/{eventDateId}", eventDate.getId())
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
            assertSoftly(s -> {
                s.assertThat(eventDateJpaRepository.findById(eventDate.getId())).isEmpty();
                s.assertThat(eventJpaRepository.findAllByEventDateId(eventDate.getId())).isEmpty();
            });
        }
    }
}
