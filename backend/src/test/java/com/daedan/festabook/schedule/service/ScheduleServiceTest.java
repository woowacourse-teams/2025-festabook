package com.daedan.festabook.schedule.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.daedan.festabook.schedule.domain.Event;
import com.daedan.festabook.schedule.domain.EventDay;
import com.daedan.festabook.schedule.domain.EventStatus;
import com.daedan.festabook.schedule.dto.EventDayResponse;
import com.daedan.festabook.schedule.dto.EventDayResponses;
import com.daedan.festabook.schedule.dto.EventResponses;
import com.daedan.festabook.schedule.repository.EventDayJpaRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ScheduleServiceTest {

    @Mock
    private EventDayJpaRepository eventDayJpaRepository;

    @InjectMocks
    private ScheduleService scheduleService;

    @Nested
    class getEventDays {

        @Test
        void 성공() {
            // given
            EventDay eventDay1 = createTestEventDay(26);
            EventDay eventDay2 = createTestEventDay(27);

            List<EventDay> eventDays = Arrays.asList(eventDay1, eventDay2);

            given(eventDayJpaRepository.findAll())
                    .willReturn(eventDays);

            // when
            EventDayResponses responses = scheduleService.getEventDays();

            // then
            assertThat(responses.eventDays())
                    .hasSize(2);
            assertThat(responses.eventDays().get(0).date())
                    .isEqualTo(LocalDate.of(2025, 10, 26));
        }

        @Test
        void 성공_날짜_오름차순_정렬() {
            // given
            EventDay eventDay1 = createTestEventDay(27);
            EventDay eventDay2 = createTestEventDay(26);
            EventDay eventDay3 = createTestEventDay(25);

            List<EventDay> eventDays = Arrays.asList(eventDay1, eventDay2, eventDay3);

            given(eventDayJpaRepository.findAll())
                    .willReturn(eventDays);

            // when
            EventDayResponses result = scheduleService.getEventDays();

            // then
            assertThat(result.eventDays())
                    .extracting(EventDayResponse::date)
                    .containsExactly(
                            eventDay3.getDate(),
                            eventDay2.getDate(),
                            eventDay1.getDate()
                    );
        }
    }

    @Nested
    class getEventsByEventDayId {

        @Test
        void 성공() {
            // given
            Long eventDayId = 1L;
            EventDay eventDay = createTestEventDay(26);

            Event event1 = createTestEvent("무대 공연", EventStatus.COMPLETED);
            Event event2 = createTestEvent("부스 운영", EventStatus.UPCOMING);

            eventDay.getEvents().addAll(Arrays.asList(event1, event2));

            given(eventDayJpaRepository.findById(eventDayId))
                    .willReturn(Optional.of(eventDay));

            // when
            EventResponses result = scheduleService.getEventsByEventDayId(eventDayId);

            // then
            assertThat(result.events())
                    .hasSize(2);
            assertThat(result.events().get(0).title())
                    .isEqualTo("무대 공연");
            assertThat(result.events().get(1).status())
                    .isEqualTo(EventStatus.UPCOMING);
        }
    }

    private Event createTestEvent(String title, EventStatus status) {
        return new Event(
                status,
                LocalTime.of(12, 0, 0),
                LocalTime.of(13, 0, 0),
                title,
                "장소"
        );
    }

    private EventDay createTestEventDay(int dayOfMonth) {
        return new EventDay(
                LocalDate.of(2025, 10, dayOfMonth),
                new ArrayList<>()
        );
    }
}
