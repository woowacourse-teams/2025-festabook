package com.daedan.festabook.event.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.daedan.festabook.event.domain.Event;
import com.daedan.festabook.event.domain.EventDate;
import com.daedan.festabook.event.domain.EventDateFixture;
import com.daedan.festabook.event.domain.EventFixture;
import com.daedan.festabook.event.domain.EventStatus;
import com.daedan.festabook.event.dto.EventRequest;
import com.daedan.festabook.event.dto.EventRequestFixture;
import com.daedan.festabook.event.dto.EventResponse;
import com.daedan.festabook.event.dto.EventResponses;
import com.daedan.festabook.event.dto.EventUpdateRequest;
import com.daedan.festabook.event.dto.EventUpdateRequestFixture;
import com.daedan.festabook.event.dto.EventUpdateResponse;
import com.daedan.festabook.event.infrastructure.EventDateJpaRepository;
import com.daedan.festabook.event.infrastructure.EventJpaRepository;
import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.global.exception.BusinessException;
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
class EventServiceTest {

    private static final LocalDateTime FIXED_CLOCK_DATETIME = LocalDateTime.of(2025, 5, 5, 16, 0, 0);

    @Mock
    private Clock clock;

    @Mock
    private EventDateJpaRepository eventDateJpaRepository;

    @Mock
    private EventJpaRepository eventJpaRepository;

    @InjectMocks
    private EventService eventService;

    @Nested
    class createEvent {

        @Test
        void 성공() {
            // given
            setFixedClock();

            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);

            Long eventDateId = 1L;
            EventDate eventDate = EventDateFixture.create(eventDateId, festival);
            EventRequest request = EventRequestFixture.create(eventDate.getId());
            given(eventDateJpaRepository.findById(request.eventDateId()))
                    .willReturn(Optional.of(eventDate));

            Long eventId = 1L;
            Event event = EventFixture.create(
                    eventId,
                    eventDate,
                    request.startTime(),
                    request.endTime(),
                    request.title(),
                    request.location()
            );
            given(eventJpaRepository.save(any()))
                    .willReturn(event);

            // when
            EventResponse result = eventService.createEvent(festival.getId(), request);

            // then
            assertSoftly(s -> {
                s.assertThat(result.eventId()).isEqualTo(eventId);
                s.assertThat(result.startTime()).isEqualTo(event.getStartTime());
                s.assertThat(result.endTime()).isEqualTo(event.getEndTime());
                s.assertThat(result.title()).isEqualTo(event.getTitle());
                s.assertThat(result.location()).isEqualTo(event.getLocation());
            });
        }

        @Test
        void 예외_존재하지_않는_일정_날짜() {
            // given
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            Long notExistingEventDateId = 0L;
            EventRequest request = EventRequestFixture.create(notExistingEventDateId);

            given(eventDateJpaRepository.findById(request.eventDateId()))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> eventService.createEvent(festival.getId(), request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 일정 날짜입니다.");
        }

        @Test
        void 예외_다른_축제의_일정_날짜일_경우() {
            // given
            Long requestFestivalId = 1L;
            Long otherFestivalId = 999L;
            Festival requestFestival = FestivalFixture.create(requestFestivalId);
            EventDate eventDate = EventDateFixture.create(1L, requestFestival, LocalDate.of(2025, 5, 5));

            given(eventDateJpaRepository.findById(eventDate.getId()))
                    .willReturn(Optional.of(eventDate));

            EventRequest request = EventRequestFixture.create(eventDate.getId());

            // when & then
            assertThatThrownBy(() -> eventService.createEvent(otherFestivalId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("해당 축제의 일정 날짜가 아닙니다.");
        }
    }

    @Nested
    class getAllEventByEventDateId {

        @Test
        void 성공() {
            // given
            setFixedClock();

            Event event = EventFixture.create(
                    EventDateFixture.create(LocalDate.of(2025, 5, 5)),
                    LocalTime.of(16, 0, 0),
                    LocalTime.of(16, 0, 0),
                    "title",
                    "location"
            );

            Long eventDateId = 1L;
            given(eventJpaRepository.findAllByEventDateId(eventDateId))
                    .willReturn(List.of(event));

            // when
            EventResponses result = eventService.getAllEventByEventDateId(eventDateId);

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
            EventResponses result = eventService.getAllEventByEventDateId(eventDateId);

            // then
            assertThat(result.responses().getFirst().status()).isEqualTo(expected);
        }
    }

    @Nested
    class updateEvent {

        @Test
        void 성공() {
            // given
            setFixedClock();

            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);

            Long eventDateId = 1L;
            EventDate eventDate = EventDateFixture.create(
                    eventDateId,
                    festival,
                    LocalDate.of(2025, 5, 5)
            );

            Long eventId = 1L;
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

            EventUpdateRequest eventUpdateRequest = EventUpdateRequestFixture.create(
                    eventDateId,
                    updatedEvent.getStartTime(),
                    updatedEvent.getEndTime(),
                    updatedEvent.getTitle(),
                    updatedEvent.getLocation()
            );

            // when
            EventUpdateResponse result = eventService.updateEvent(eventId, festival.getId(), eventUpdateRequest);

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
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            EventUpdateRequest request = EventUpdateRequestFixture.create();

            given(eventJpaRepository.findById(eventId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> eventService.updateEvent(eventId, festival.getId(), request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 일정입니다.");
        }

        @Test
        void 예외_다른_축제의_일정일_경우() {
            // given
            Long requestFestivalId = 1L;
            Long otherFestivalId = 999L;
            Festival requestFestival = FestivalFixture.create(requestFestivalId);
            Festival otherFestival = FestivalFixture.create(otherFestivalId);
            EventDate eventDate = EventDateFixture.create(requestFestival);
            Event event = EventFixture.create(eventDate);

            given(eventJpaRepository.findById(event.getId()))
                    .willReturn(Optional.of(event));

            EventUpdateRequest request = EventUpdateRequestFixture.create();

            // when & then
            assertThatThrownBy(() -> eventService.updateEvent(event.getId(), otherFestival.getId(), request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("해당 축제의 일정이 아닙니다.");
        }
    }

    @Nested
    class deleteEventByEventId {

        @Test
        void 성공() {
            // given
            Long festivalId = 12L;
            Long eventDateId = 77L;
            Long eventId = 1L;

            Festival festival = FestivalFixture.create(festivalId);
            EventDate eventDate = EventDateFixture.create(eventDateId, festival);
            Event event = EventFixture.create(eventId, eventDate);

            given(eventJpaRepository.findById(eventId))
                    .willReturn(Optional.of(event));

            // when
            eventService.deleteEventByEventId(eventId, festival.getId());

            // then
            then(eventJpaRepository).should()
                    .deleteById(eventId);
        }

        @Test
        void 예외_다른_축제의_일정일_경우() {
            // given
            Long requestFestivalId = 1L;
            Long otherFestivalId = 999L;
            Festival requestFestival = FestivalFixture.create(requestFestivalId);
            Festival otherFestival = FestivalFixture.create(otherFestivalId);
            EventDate eventDate = EventDateFixture.create(requestFestival);
            Event event = EventFixture.create(eventDate);

            given(eventJpaRepository.findById(event.getId()))
                    .willReturn(Optional.of(event));

            // when & then
            assertThatThrownBy(() -> eventService.deleteEventByEventId(event.getId(), otherFestival.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("해당 축제의 일정이 아닙니다.");
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
