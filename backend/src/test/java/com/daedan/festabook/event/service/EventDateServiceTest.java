package com.daedan.festabook.event.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.daedan.festabook.event.domain.EventDate;
import com.daedan.festabook.event.domain.EventDateFixture;
import com.daedan.festabook.event.dto.EventDateRequest;
import com.daedan.festabook.event.dto.EventDateRequestFixture;
import com.daedan.festabook.event.dto.EventDateResponse;
import com.daedan.festabook.event.dto.EventDateResponses;
import com.daedan.festabook.event.dto.EventDateUpdateRequest;
import com.daedan.festabook.event.dto.EventDateUpdateRequestFixture;
import com.daedan.festabook.event.dto.EventDateUpdateResponse;
import com.daedan.festabook.event.infrastructure.EventDateJpaRepository;
import com.daedan.festabook.event.infrastructure.EventJpaRepository;
import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.global.exception.BusinessException;
import java.time.LocalDate;
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
class EventDateServiceTest {

    @Mock
    private EventDateJpaRepository eventDateJpaRepository;

    @Mock
    private EventJpaRepository eventJpaRepository;

    @Mock
    private FestivalJpaRepository festivalJpaRepository;

    @InjectMocks
    private EventDateService eventDateService;

    @Nested
    class createEventDate {

        @Test
        void 성공() {
            // given
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            EventDateRequest request = EventDateRequestFixture.create();
            EventDate eventDate = EventDateFixture.create(request.date());

            given(eventDateJpaRepository.save(any()))
                    .willReturn(eventDate);
            given(festivalJpaRepository.findById(festivalId))
                    .willReturn(Optional.of(festival));

            // when
            EventDateResponse result = eventDateService.createEventDate(festivalId, request);

            // then
            assertSoftly(s -> {
                s.assertThat(result.eventDateId()).isEqualTo(eventDate.getId());
                s.assertThat(result.date()).isEqualTo(eventDate.getDate());
            });
        }

        @Test
        void 예외_이미_존재하는_일정_날짜() {
            // given
            Long festivalId = 1L;
            EventDateRequest request = EventDateRequestFixture.create();

            given(eventDateJpaRepository.existsByFestivalIdAndDate(festivalId, request.date()))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> eventDateService.createEventDate(festivalId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("이미 존재하는 일정 날짜입니다.");
        }

        @Test
        void 예외_존재하지_않는_축제() {
            // given
            Long festivalId = 1L;
            EventDateRequest request = EventDateRequestFixture.create();

            given(festivalJpaRepository.findById(festivalId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> eventDateService.createEventDate(festivalId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 축제입니다.");
        }
    }

    @Nested
    class getAllEventDateByFestivalId {

        @Test
        void 성공_응답_개수_확인() {
            // given
            Long festivalId = 1L;

            EventDate eventDate1 = EventDateFixture.create(LocalDate.of(2025, 10, 26));
            EventDate eventDate2 = EventDateFixture.create(LocalDate.of(2025, 10, 27));

            List<EventDate> eventDates = List.of(eventDate1, eventDate2);

            given(eventDateJpaRepository.findAllByFestivalId(festivalId))
                    .willReturn(eventDates);

            LocalDate expected = LocalDate.of(2025, 10, 26);

            // when
            EventDateResponses result = eventDateService.getAllEventDateByFestivalId(festivalId);

            // then
            assertSoftly(s -> {
                s.assertThat(result.responses()).hasSize(2);
                s.assertThat(result.responses().getFirst().date()).isEqualTo(expected);
            });
        }

        @Test
        void 성공_날짜_오름차순_정렬() {
            // given
            Long festivalId = 1L;

            EventDate eventDate1 = EventDateFixture.create(LocalDate.of(2025, 10, 27));
            EventDate eventDate2 = EventDateFixture.create(LocalDate.of(2025, 10, 26));
            EventDate eventDate3 = EventDateFixture.create(LocalDate.of(2025, 10, 25));

            List<EventDate> eventDates = List.of(eventDate1, eventDate2, eventDate3);

            given(eventDateJpaRepository.findAllByFestivalId(festivalId))
                    .willReturn(eventDates);

            // when
            EventDateResponses result = eventDateService.getAllEventDateByFestivalId(festivalId);

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
    class updateEventDate {

        @Test
        void 성공() {
            // given
            Long festivalId = 1L;
            Long eventDateId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            EventDate eventDate = EventDateFixture.create(eventDateId, festival);

            given(eventDateJpaRepository.findById(eventDateId))
                    .willReturn(Optional.of(eventDate));

            EventDateUpdateRequest request = EventDateUpdateRequestFixture.create(eventDate.getDate().plusDays(1));

            // when
            EventDateUpdateResponse result = eventDateService.updateEventDate(
                    festivalId,
                    eventDateId,
                    request
            );

            // then
            assertSoftly(s -> {
                s.assertThat(result.eventDateId()).isEqualTo(eventDate.getId());
                s.assertThat(result.date()).isEqualTo(request.date());
            });
        }

        @Test
        void 예외_이미_존재하는_일정_날짜() {
            // given
            Long festivalId = 1L;
            Long eventDateId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            EventDate eventDate = EventDateFixture.create(eventDateId, festival);
            EventDateUpdateRequest request = EventDateUpdateRequestFixture.create();

            given(eventDateJpaRepository.findById(eventDateId))
                    .willReturn(Optional.of(eventDate));
            given(eventDateJpaRepository.existsByFestivalIdAndDate(festivalId, request.date()))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> eventDateService.updateEventDate(festivalId, eventDateId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("이미 존재하는 일정 날짜입니다.");
        }

        @Test
        void 예외_다른_축제의_일정_날짜일_경우() {
            // given
            Long requestFestivalId = 1L;
            Long otherFestivalId = 999L;
            Long eventDateId = 1L;
            Festival requestFestival = FestivalFixture.create(requestFestivalId);
            Festival otherFestival = FestivalFixture.create(otherFestivalId);
            EventDate eventDate = EventDateFixture.create(eventDateId, requestFestival);

            given(eventDateJpaRepository.findById(eventDateId))
                    .willReturn(Optional.of(eventDate));

            EventDateUpdateRequest request = EventDateUpdateRequestFixture.create();

            // when & then
            assertThatThrownBy(() ->
                    eventDateService.updateEventDate(eventDate.getId(), otherFestival.getId(), request)
            )
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("해당 축제의 일정 날짜가 아닙니다.");
        }
    }

    @Nested
    class deleteEventDateByEventDateId {

        @Test
        void 성공() {
            // given
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            Long eventDateId = 1L;
            EventDate eventDate = EventDateFixture.create(eventDateId, festival);

            given(eventDateJpaRepository.findById(eventDateId))
                    .willReturn(Optional.of(eventDate));

            // when
            eventDateService.deleteEventDateByEventDateId(eventDateId, festivalId);

            // then
            then(eventDateJpaRepository).should()
                    .deleteById(eventDateId);
            then(eventJpaRepository).should()
                    .deleteAllByEventDateId(eventDateId);
        }

        @Test
        void 예외_다른_축제의_일정_날짜일_경우() {
            // given
            Long requestFestivalId = 1L;
            Long otherFestivalId = 999L;
            Festival requestFestival = FestivalFixture.create(requestFestivalId);
            Festival otherFestival = FestivalFixture.create(otherFestivalId);
            EventDate eventDate = EventDateFixture.create(requestFestival);

            given(eventDateJpaRepository.findById(eventDate.getId()))
                    .willReturn(Optional.of(eventDate));

            // when & then
            assertThatThrownBy(() ->
                    eventDateService.deleteEventDateByEventDateId(eventDate.getId(), otherFestival.getId())
            )
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("해당 축제의 일정 날짜가 아닙니다.");
        }
    }
}
