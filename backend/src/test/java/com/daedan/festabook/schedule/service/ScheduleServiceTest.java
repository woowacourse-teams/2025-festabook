package com.daedan.festabook.schedule.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;

import com.daedan.festabook.schedule.domain.Event;
import com.daedan.festabook.schedule.domain.EventDate;
import com.daedan.festabook.schedule.domain.EventDateFixture;
import com.daedan.festabook.schedule.domain.EventFixture;
import com.daedan.festabook.schedule.domain.EventStatus;
import com.daedan.festabook.schedule.dto.EventDateResponse;
import com.daedan.festabook.schedule.dto.EventDateResponses;
import com.daedan.festabook.schedule.dto.EventResponses;
import com.daedan.festabook.schedule.infrastructure.EventDateJpaRepository;
import com.daedan.festabook.schedule.infrastructure.EventJpaRepository;
import java.time.LocalDate;
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

    private static final Long DEFAULT_ORGANIZATION_ID = 1L;

    @Mock
    private EventDateJpaRepository eventDateJpaRepository;

    @Mock
    private EventJpaRepository eventJpaRepository;

    @InjectMocks
    private ScheduleService scheduleService;

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
                s.assertThat(result.eventDate()).hasSize(2);
                s.assertThat(result.eventDate().getFirst().date()).isEqualTo(expected);
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
            assertThat(result.eventDate())
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
            Long eventDateId = 1L;

            Event event1 = EventFixture.create("무대 공연", EventStatus.COMPLETED);
            Event event2 = EventFixture.create("부스 운영", EventStatus.UPCOMING);

            given(eventJpaRepository.findAllByEventDateId(eventDateId))
                    .willReturn(List.of(event1, event2));

            // when
            EventResponses result = scheduleService.getAllEventByEventDateId(eventDateId);

            // then
            assertSoftly(s -> {
                s.assertThat(result.events()).hasSize(2);
                s.assertThat(result.events().get(0).title()).isEqualTo("무대 공연");
                s.assertThat(result.events().get(1).status()).isEqualTo(EventStatus.UPCOMING);
            });
        }
    }
}
