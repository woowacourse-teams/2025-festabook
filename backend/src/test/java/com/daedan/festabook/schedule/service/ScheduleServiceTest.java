package com.daedan.festabook.schedule.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import com.daedan.festabook.schedule.domain.Event;
import com.daedan.festabook.schedule.domain.EventDate;
import com.daedan.festabook.schedule.domain.EventDateFixture;
import com.daedan.festabook.schedule.domain.EventFixture;
import com.daedan.festabook.schedule.domain.EventStatus;
import com.daedan.festabook.schedule.dto.EventDateRequest;
import com.daedan.festabook.schedule.dto.EventDateResponse;
import com.daedan.festabook.schedule.dto.EventDateResponses;
import com.daedan.festabook.schedule.dto.EventRequest;
import com.daedan.festabook.schedule.dto.EventResponse;
import com.daedan.festabook.schedule.dto.EventResponses;
import com.daedan.festabook.schedule.infrastructure.EventDateJpaRepository;
import com.daedan.festabook.schedule.infrastructure.EventJpaRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ScheduleServiceTest {

    private static final LocalDateTime FIXED_CLOCK_DATETIME = LocalDateTime.of(2025, 5, 5, 16, 0, 0);
    private static final Long DEFAULT_ORGANIZATION_ID = 1L;

    @Mock
    private EventDateJpaRepository eventDateJpaRepository;

    @Mock
    private EventJpaRepository eventJpaRepository;

    @Mock
    private OrganizationJpaRepository organizationJpaRepository;

    @Mock
    private Clock clock;

    @InjectMocks
    private ScheduleService scheduleService;

    @Nested
    class createEventDate {

        @Test
        void 성공() {
            // given
            EventDateRequest request = new EventDateRequest(LocalDate.of(2025, 7, 18));
            EventDate eventDate = EventDateFixture.create(request.date());
            given(eventDateJpaRepository.save(any()))
                    .willReturn(eventDate);

            Organization organization = OrganizationFixture.create(DEFAULT_ORGANIZATION_ID);
            given(organizationJpaRepository.findById(DEFAULT_ORGANIZATION_ID))
                    .willReturn(Optional.of(organization));

            // when
            EventDateResponse result = scheduleService.createEventDate(DEFAULT_ORGANIZATION_ID, request);

            // then
            assertSoftly(s -> {
                s.assertThat(result.id()).isEqualTo(eventDate.getId());
                s.assertThat(result.date()).isEqualTo(eventDate.getDate());
            });
        }

        @Test
        void 예외_이미_존재하는_일정_날짜() {
            // given
            LocalDate date = LocalDate.of(2025, 7, 18);
            EventDateRequest request = new EventDateRequest(date);
            given(eventDateJpaRepository.existsByOrganizationIdAndDate(DEFAULT_ORGANIZATION_ID, date))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> scheduleService.createEventDate(DEFAULT_ORGANIZATION_ID, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("이미 존재하는 일정 날짜입니다.");
        }

        @Test
        void 예외_존재하지_않는_조직() {
            // given
            LocalDate date = LocalDate.of(2025, 7, 18);
            EventDateRequest request = new EventDateRequest(date);
            given(organizationJpaRepository.findById(DEFAULT_ORGANIZATION_ID))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> scheduleService.createEventDate(DEFAULT_ORGANIZATION_ID, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 조직입니다.");
        }
    }

    @Nested
    class deleteEventDate {

        @Test
        void 성공() {
            // given
            Long eventDateId = 1L;

            // when
            scheduleService.deleteEventDate(eventDateId);

            // then
            then(eventDateJpaRepository).should()
                    .deleteById(eventDateId);
            then(eventJpaRepository).should()
                    .deleteAllByEventDateId(eventDateId);
        }
    }

    @Nested
    class createEvent {

        @Test
        void 성공() {
            // given
            setFixedClock();

            EventDate eventDate = EventDateFixture.create(LocalDate.of(2025, 5, 5));
            EventRequest request = new EventRequest(
                    LocalTime.of(1, 0),
                    LocalTime.of(2, 0),
                    "title",
                    "location",
                    eventDate.getId()
            );
            given(eventDateJpaRepository.findById(request.eventDateId()))
                    .willReturn(Optional.of(eventDate));

            Long eventId = 1L;
            Event event = EventFixture.create(
                    eventId,
                    request.startTime(),
                    request.endTime(),
                    request.title(),
                    request.location(),
                    eventDate
            );
            given(eventJpaRepository.save(any()))
                    .willReturn(event);

            // when
            EventResponse result = scheduleService.createEvent(request);

            // then
            assertSoftly(s -> {
                s.assertThat(result.id()).isEqualTo(eventId);
                s.assertThat(result.startTime()).isEqualTo(event.getStartTime());
                s.assertThat(result.endTime()).isEqualTo(event.getEndTime());
                s.assertThat(result.title()).isEqualTo(event.getTitle());
                s.assertThat(result.location()).isEqualTo(event.getLocation());
            });
        }

        @Test
        void 예외_존재하지_않는_일정_날짜() {
            // given
            EventRequest request = new EventRequest(
                    LocalTime.of(1, 0),
                    LocalTime.of(2, 0),
                    "title",
                    "location",
                    1L
            );
            given(eventDateJpaRepository.findById(request.eventDateId()))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> scheduleService.createEvent(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 일정 날짜입니다.");
        }
    }

    @Nested
    class updateEvent {

        @Test
        void 성공() {
            // given
            setFixedClock();

            Long eventId = 1L;
            Long eventDateId = 1L;
            EventDate eventDate = EventDateFixture.create(eventDateId, LocalDate.of(2025, 5, 5));
            Event event = EventFixture.create(
                    LocalTime.of(1, 0),
                    LocalTime.of(2, 0),
                    eventDate
            );
            given(eventJpaRepository.findById(eventId))
                    .willReturn(Optional.of(event));

            Event updatedEvent = EventFixture.create(
                    LocalTime.of(3, 0),
                    LocalTime.of(4, 0),
                    eventDate
            );

            EventRequest eventRequest = new EventRequest(
                    updatedEvent.getStartTime(),
                    updatedEvent.getEndTime(),
                    updatedEvent.getTitle(),
                    updatedEvent.getLocation(),
                    eventDateId
            );

            // when
            EventResponse result = scheduleService.updateEvent(eventId, eventRequest);

            // then
            assertSoftly(s -> {
                s.assertThat(result.startTime()).isEqualTo(updatedEvent.getStartTime());
                s.assertThat(result.endTime()).isEqualTo(updatedEvent.getEndTime());
                s.assertThat(result.title()).isEqualTo(updatedEvent.getTitle());
                s.assertThat(result.location()).isEqualTo(updatedEvent.getLocation());
            });
        }

        @Test
        void 예외_존재하지_않는_이벤트() {
            // given
            Long eventId = 1L;
            EventRequest request = new EventRequest(
                    LocalTime.of(1, 0),
                    LocalTime.of(2, 0),
                    "title",
                    "location",
                    1L
            );

            given(eventJpaRepository.findById(eventId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> scheduleService.updateEvent(eventId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 일정입니다.");
        }
    }

    @Nested
    class deleteEvent {

        @Test
        void 성공() {
            // given
            Long eventId = 1L;

            // when
            scheduleService.deleteEvent(eventId);

            // then
            then(eventJpaRepository).should()
                    .deleteById(eventId);
        }
    }

    @Nested
    class getAllEventDateByOrganizationId {

        @Test
        void 성공() {
            // given
            EventDate eventDate1 = EventDateFixture.create(LocalDate.of(2025, 10, 26));
            EventDate eventDate2 = EventDateFixture.create(LocalDate.of(2025, 10, 27));

            List<EventDate> eventDates = List.of(eventDate1, eventDate2);

            given(eventDateJpaRepository.findAllByOrganizationId(DEFAULT_ORGANIZATION_ID))
                    .willReturn(eventDates);

            LocalDate expected = LocalDate.of(2025, 10, 26);

            // when
            EventDateResponses result = scheduleService.getAllEventDateByOrganizationId(DEFAULT_ORGANIZATION_ID);

            // then
            assertSoftly(s -> {
                s.assertThat(result.responses()).hasSize(2);
                s.assertThat(result.responses().getFirst().date()).isEqualTo(expected);
            });
        }

        @Test
        void 성공_날짜_오름차순_정렬() {
            // given
            EventDate eventDate1 = EventDateFixture.create(LocalDate.of(2025, 10, 27));
            EventDate eventDate2 = EventDateFixture.create(LocalDate.of(2025, 10, 26));
            EventDate eventDate3 = EventDateFixture.create(LocalDate.of(2025, 10, 25));

            List<EventDate> eventDates = List.of(eventDate1, eventDate2, eventDate3);

            given(eventDateJpaRepository.findAllByOrganizationId(DEFAULT_ORGANIZATION_ID))
                    .willReturn(eventDates);

            // when
            EventDateResponses result = scheduleService.getAllEventDateByOrganizationId(DEFAULT_ORGANIZATION_ID);

            // then
            assertThat(result.responses())
                    .extracting(EventDateResponse::date)
                    .containsExactly(
                            eventDate3.getDate(),
                            eventDate2.getDate(),
                            eventDate1.getDate()
                    );
        }
    }

    @Nested
    class getAllEventByEventDateId {

        @Test
        void 성공() {
            // given
            setFixedClock();

            Event event = EventFixture.create(
                    LocalTime.of(16, 0, 0),
                    LocalTime.of(16, 0, 0),
                    "title",
                    "location",
                    EventDateFixture.create(LocalDate.of(2025, 5, 5))
            );

            Long eventDateId = 1L;
            given(eventJpaRepository.findAllByEventDateId(eventDateId))
                    .willReturn(List.of(event));

            // when
            EventResponses result = scheduleService.getAllEventByEventDateId(eventDateId);

            // then
            assertSoftly(s -> {
                s.assertThat(result.responses().getFirst().status()).isEqualTo(EventStatus.ONGOING);
                s.assertThat(result.responses().getFirst().startTime()).isEqualTo(event.getStartTime());
                s.assertThat(result.responses().getFirst().endTime()).isEqualTo(event.getEndTime());
                s.assertThat(result.responses().getFirst().title()).isEqualTo(event.getTitle());
                s.assertThat(result.responses().getFirst().location()).isEqualTo(event.getLocation());
            });
        }

        @ParameterizedTest(name = "날짜: {0}, 시작 시간: {1}, 종료 시간: {2}, 결과: {3}")
        @CsvSource({
                "2025-05-04, 10:00, 12:00, COMPLETED",   // 과거 날짜
                "2025-05-05, 10:00, 15:00, COMPLETED",   // 오늘, 종료 시각 이전
                "2025-05-05, 14:00, 17:00, ONGOING",     // 오늘, 진행 중
                "2025-05-05, 17:00, 18:00, UPCOMING",    // 오늘, 시작 전
                "2025-05-06, 10:00, 12:00, UPCOMING"     // 미래 날짜
        })
        void 성공_이벤트_상태_판별(LocalDate date, LocalTime startTime, LocalTime endTime, EventStatus expected) {
            // given
            setFixedClock();

            Event event = EventFixture.create(startTime, endTime, EventDateFixture.create(date));

            Long eventDateId = 1L;
            given(eventJpaRepository.findAllByEventDateId(eventDateId))
                    .willReturn(List.of(event));

            // when
            EventResponses result = scheduleService.getAllEventByEventDateId(eventDateId);

            // then
            assertThat(result.responses().getFirst().status()).isEqualTo(expected);
        }
    }

    private void setFixedClock() {
        ZoneId korea = ZoneId.of("Asia/Seoul");
        Clock fixedClock = Clock.fixed(
                FIXED_CLOCK_DATETIME.atZone(korea).toInstant(),
                korea
        );

        given(clock.instant())
                .willReturn(fixedClock.instant());
        given(clock.getZone())
                .willReturn(fixedClock.getZone());
    }
}
