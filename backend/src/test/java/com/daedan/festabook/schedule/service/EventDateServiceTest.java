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
import com.daedan.festabook.schedule.domain.EventDate;
import com.daedan.festabook.schedule.domain.EventDateFixture;
import com.daedan.festabook.schedule.dto.EventDateRequest;
import com.daedan.festabook.schedule.dto.EventDateResponse;
import com.daedan.festabook.schedule.dto.EventDateResponses;
import com.daedan.festabook.schedule.infrastructure.EventDateJpaRepository;
import com.daedan.festabook.schedule.infrastructure.EventJpaRepository;
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

    private static final Long DEFAULT_ORGANIZATION_ID = 1L;

    @Mock
    private EventDateJpaRepository eventDateJpaRepository;

    @Mock
    private EventJpaRepository eventJpaRepository;

    @Mock
    private OrganizationJpaRepository organizationJpaRepository;

    @InjectMocks
    private EventDateService eventDateService;

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
            EventDateResponse result = eventDateService.createEventDate(DEFAULT_ORGANIZATION_ID, request);

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
            assertThatThrownBy(() -> eventDateService.createEventDate(DEFAULT_ORGANIZATION_ID, request))
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
            assertThatThrownBy(() -> eventDateService.createEventDate(DEFAULT_ORGANIZATION_ID, request))
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
            eventDateService.deleteEventDate(eventDateId);

            // then
            then(eventDateJpaRepository).should()
                    .deleteById(eventDateId);
            then(eventJpaRepository).should()
                    .deleteAllByEventDateId(eventDateId);
        }
    }

    @Nested
    class getAllEventDateByOrganizationId {

        @Test
        void 성공_응답_개수_확인() {
            // given
            EventDate eventDate1 = EventDateFixture.create(LocalDate.of(2025, 10, 26));
            EventDate eventDate2 = EventDateFixture.create(LocalDate.of(2025, 10, 27));

            List<EventDate> eventDates = List.of(eventDate1, eventDate2);

            given(eventDateJpaRepository.findAllByOrganizationId(DEFAULT_ORGANIZATION_ID))
                    .willReturn(eventDates);

            LocalDate expected = LocalDate.of(2025, 10, 26);

            // when
            EventDateResponses result = eventDateService.getAllEventDateByOrganizationId(DEFAULT_ORGANIZATION_ID);

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
            EventDateResponses result = eventDateService.getAllEventDateByOrganizationId(DEFAULT_ORGANIZATION_ID);

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
}
