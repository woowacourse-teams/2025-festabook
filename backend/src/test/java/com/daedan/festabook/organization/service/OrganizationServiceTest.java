package com.daedan.festabook.organization.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.organization.domain.FestivalImage;
import com.daedan.festabook.organization.domain.FestivalImageFixture;
import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import com.daedan.festabook.organization.dto.OrganizationGeographyResponse;
import com.daedan.festabook.organization.dto.OrganizationInformationResponse;
import com.daedan.festabook.organization.dto.OrganizationInformationUpdateRequest;
import com.daedan.festabook.organization.dto.OrganizationInformationUpdateRequestFixture;
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
                    .hasMessage("존재하지 않는 조직입니다.");
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

            given(organizationJpaRepository.findById(organizationId))
                    .willReturn(Optional.of(organization));
            given(festivalImageJpaRepository.findAllByOrganizationIdOrderBySequenceAsc(organizationId))
                    .willReturn(festivalImages);

            // when
            OrganizationResponse result = organizationService.getOrganizationByOrganizationId(organizationId);

            // then
            assertSoftly(s -> {
                s.assertThat(result.universityName()).isEqualTo(organization.getUniversityName());
                s.assertThat(result.festivalImages().responses().get(0).imageUrl())
                        .isEqualTo(festivalImages.get(0).getImageUrl());
                s.assertThat(result.festivalImages().responses().get(1).imageUrl())
                        .isEqualTo(festivalImages.get(1).getImageUrl());
                s.assertThat(result.festivalName()).isEqualTo(organization.getFestivalName());
                s.assertThat(result.startDate()).isEqualTo(organization.getStartDate());
                s.assertThat(result.endDate()).isEqualTo(organization.getEndDate());
            });
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
                    .hasMessage("존재하지 않는 조직입니다.");
        }
    }

    @Nested
    class updateOrganizationInformation {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();

            given(organizationJpaRepository.findById(organization.getId()))
                    .willReturn(Optional.of(organization));

            OrganizationInformationUpdateRequest request = OrganizationInformationUpdateRequestFixture.create(
                    "수정 후 제목",
                    LocalDate.of(2025, 10, 1),
                    LocalDate.of(2025, 10, 2)
            );

            // when
            OrganizationInformationResponse result = organizationService.updateOrganizationInformation(
                    organization.getId(),
                    request
            );

            // then
            assertSoftly(s -> {
                s.assertThat(result.festivalName()).isEqualTo(request.festivalName());
                s.assertThat(result.startDate()).isEqualTo(request.startDate());
                s.assertThat(result.endDate()).isEqualTo(request.endDate());
            });
        }

        @Test
        void 예외_존재하지_않는_조직() {
            // given
            Long invalidOrganizationId = 0L;

            OrganizationInformationUpdateRequest request = OrganizationInformationUpdateRequestFixture.create();

            // when & then
            assertThatThrownBy(() -> organizationService.updateOrganizationInformation(invalidOrganizationId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 조직입니다.");
        }
    }
}
