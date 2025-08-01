package com.daedan.festabook.organization.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.daedan.festabook.event.domain.EventDate;
import com.daedan.festabook.event.domain.EventDateFixture;
import com.daedan.festabook.event.infrastructure.EventDateJpaRepository;
import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.organization.domain.FestivalImage;
import com.daedan.festabook.organization.domain.FestivalImageFixture;
import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import com.daedan.festabook.organization.dto.OrganizationGeographyResponse;
import com.daedan.festabook.organization.dto.OrganizationResponse;
import com.daedan.festabook.organization.infrastructure.FestivalImageJpaRepository;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
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
class OrganizationServiceTest {

    @Mock
    private OrganizationJpaRepository organizationJpaRepository;

    @Mock
    private FestivalImageJpaRepository festivalImageJpaRepository;

    @Mock
    private EventDateJpaRepository eventDateJpaRepository;

    @InjectMocks
    private OrganizationService organizationService;

    @Nested
    class getOrganizationGeographyByOrganizationId {

        @Test
        void 성공() {
            // given
            Long organizationId = 1L;
            Organization organization = OrganizationFixture.create(organizationId);

            given(organizationJpaRepository.findById(organizationId))
                    .willReturn(Optional.of(organization));

            OrganizationGeographyResponse expected = OrganizationGeographyResponse.from(organization);

            // when
            OrganizationGeographyResponse result =
                    organizationService.getOrganizationGeographyByOrganizationId(organizationId);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        void 예외_존재하지_않는_조직_ID로_조회시_예외_발생() {
            // given
            Long invalidOrganizationId = 0L;

            given(organizationJpaRepository.findById(invalidOrganizationId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(
                    () -> organizationService.getOrganizationGeographyByOrganizationId(invalidOrganizationId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("조직이 존재하지 않습니다.");
        }
    }

    @Nested
    class getOrganizationByOrganizationId {

        @Test
        void 성공() {
            // given
            Long organizationId = 1L;
            Organization organization = OrganizationFixture.create(organizationId);
            List<FestivalImage> festivalImages = FestivalImageFixture.createList(2, organization);
            List<EventDate> eventDates = EventDateFixture.createList(3, organization);

            given(organizationJpaRepository.findById(organizationId))
                    .willReturn(Optional.of(organization));
            given(festivalImageJpaRepository.findAllByOrganizationIdOrderBySequenceAsc(organizationId))
                    .willReturn(festivalImages);
            given(eventDateJpaRepository.findAllByOrganizationIdOrderByDateAsc(organizationId))
                    .willReturn(eventDates);

            // when
            OrganizationResponse result = organizationService.getOrganizationByOrganizationId(organizationId);

            // then
            assertThat(result.universityName()).isEqualTo(organization.getUniversityName());
            assertThat(result.festivalImages().responses().get(0).imageUrl()).isEqualTo(
                    festivalImages.get(0).getImageUrl());
            assertThat(result.festivalImages().responses().get(1).imageUrl()).isEqualTo(
                    festivalImages.get(1).getImageUrl());
            assertThat(result.festivalName()).isEqualTo(organization.getFestivalName());
            assertThat(result.startDate()).isEqualTo(eventDates.getFirst().getDate());
            assertThat(result.endDate()).isEqualTo(eventDates.getLast().getDate());
        }

        @Test
        void 성공_축제_날짜_중_가장_빠른_날짜와_가장_늦은_날짜가_응답됨() {
            // given
            Long organizationId = 1L;
            Organization organization = OrganizationFixture.create(organizationId);

            EventDate firstEventDate = EventDateFixture.create(organization, LocalDate.of(2025, 8, 1));
            EventDate secondEventDate = EventDateFixture.create(organization, LocalDate.of(2025, 8, 4));
            EventDate thirdEventDate = EventDateFixture.create(organization, LocalDate.of(2025, 8, 5));
            EventDate fourthEventDate = EventDateFixture.create(organization, LocalDate.of(2025, 8, 6));
            List<EventDate> eventDates = List.of(firstEventDate, secondEventDate, thirdEventDate, fourthEventDate);

            given(organizationJpaRepository.findById(organizationId))
                    .willReturn(Optional.of(organization));
            given(eventDateJpaRepository.findAllByOrganizationIdOrderByDateAsc(organizationId))
                    .willReturn(eventDates);

            // when
            OrganizationResponse result = organizationService.getOrganizationByOrganizationId(organizationId);

            // then
            assertThat(result.startDate()).isEqualTo(firstEventDate.getDate());
            assertThat(result.endDate()).isEqualTo(fourthEventDate.getDate());
        }

        @Test
        void 성공_축제_날짜는_null_가능() {
            // given
            Long organizationId = 1L;
            Organization organization = OrganizationFixture.create(organizationId);

            given(organizationJpaRepository.findById(organizationId))
                    .willReturn(Optional.of(organization));

            // when
            OrganizationResponse result = organizationService.getOrganizationByOrganizationId(organizationId);

            // then
            assertThat(result.universityName()).isEqualTo(organization.getUniversityName());
            assertThat(result.festivalImages().responses()).isEqualTo(List.of());
            assertThat(result.festivalName()).isEqualTo(organization.getFestivalName());
            assertThat(result.startDate()).isNull();
            assertThat(result.endDate()).isNull();
        }

        @Test
        void 예외_존재하지_않는_조직_ID로_조회시_예외_발생() {
            // given
            Long invalidOrganizationId = 0L;

            given(organizationJpaRepository.findById(invalidOrganizationId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> organizationService.getOrganizationByOrganizationId(invalidOrganizationId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("조직이 존재하지 않습니다.");
        }
    }
}
