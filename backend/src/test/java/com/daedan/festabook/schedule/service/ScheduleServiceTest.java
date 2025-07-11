package com.daedan.festabook.schedule.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;

import com.daedan.festabook.schedule.domain.Event;
import com.daedan.festabook.schedule.domain.EventDay;
import com.daedan.festabook.schedule.domain.EventDayFixture;
import com.daedan.festabook.schedule.domain.EventFixture;
import com.daedan.festabook.schedule.domain.EventStatus;
import com.daedan.festabook.schedule.dto.EventDayResponse;
import com.daedan.festabook.schedule.dto.EventDayResponses;
import com.daedan.festabook.schedule.dto.EventResponses;
import com.daedan.festabook.schedule.repository.EventDayJpaRepository;
import com.daedan.festabook.schedule.repository.EventJpaRepository;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
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

    @Mock
    private EventJpaRepository eventJpaRepository;

    @InjectMocks
    private ScheduleService scheduleService;

    @Nested
    class getAllEventDay {

        @Test
        void 성공() {
            // given
            EventDay eventDay1 = EventDayFixture.create(LocalDate.of(2025, 10, 26));
            EventDay eventDay2 = EventDayFixture.create(LocalDate.of(2025, 10, 27));

            List<EventDay> eventDays = Arrays.asList(eventDay1, eventDay2);

            given(eventDayJpaRepository.findAll())
                    .willReturn(eventDays);

            LocalDate expected = LocalDate.of(2025, 10, 26);

            // when
            EventDayResponses result = scheduleService.getAllEventDay();

            // then
            assertSoftly(s -> {
                s.assertThat(result.eventDays()).hasSize(2);
                s.assertThat(result.eventDays().getFirst().date()).isEqualTo(expected);
            });
        }

        @Test
        void 성공_날짜_오름차순_정렬() {
            // given
            EventDay eventDay1 = EventDayFixture.create(LocalDate.of(2025, 10, 27));
            EventDay eventDay2 = EventDayFixture.create(LocalDate.of(2025, 10, 26));
            EventDay eventDay3 = EventDayFixture.create(LocalDate.of(2025, 10, 25));

            List<EventDay> eventDays = Arrays.asList(eventDay1, eventDay2, eventDay3);

            given(eventDayJpaRepository.findAll())
                    .willReturn(eventDays);

            // when
            EventDayResponses result = scheduleService.getAllEventDay();

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
    class getAllEventByEventDayId {

        @Test
        void 성공() {
            // given
            Long eventDayId = 1L;

            Event event1 = EventFixture.create("무대 공연", EventStatus.COMPLETED);
            Event event2 = EventFixture.create("부스 운영", EventStatus.UPCOMING);

            given(eventJpaRepository.findAllByEventDayId(eventDayId))
                    .willReturn(List.of(event1, event2));

            // when
            EventResponses result = scheduleService.getAllEventByEventDayId(eventDayId);

            // then
            assertSoftly(s -> {
                s.assertThat(result.events()).hasSize(2);
                s.assertThat(result.events().get(0).title()).isEqualTo("무대 공연");
                s.assertThat(result.events().get(1).status()).isEqualTo(EventStatus.UPCOMING);
            });
        }
    }
}
