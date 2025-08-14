package com.daedan.festabook.festival.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.festival.domain.FestivalImage;
import com.daedan.festabook.festival.domain.FestivalImageFixture;
import com.daedan.festabook.festival.dto.FestivalGeographyResponse;
import com.daedan.festabook.festival.dto.FestivalInformationResponse;
import com.daedan.festabook.festival.dto.FestivalInformationUpdateRequest;
import com.daedan.festabook.festival.dto.FestivalInformationUpdateRequestFixture;
import com.daedan.festabook.festival.dto.FestivalResponse;
import com.daedan.festabook.festival.dto.FestivalUniversityResponses;
import com.daedan.festabook.festival.infrastructure.FestivalImageJpaRepository;
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
class FestivalServiceTest {

    @Mock
    private FestivalJpaRepository festivalJpaRepository;

    @Mock
    private FestivalImageJpaRepository festivalImageJpaRepository;

    @InjectMocks
    private FestivalService festivalService;

    @Nested
    class getFestivalGeographyByFestivalId {

        @Test
        void 성공() {
            // given
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);

            given(festivalJpaRepository.findById(festivalId))
                    .willReturn(Optional.of(festival));

            FestivalGeographyResponse expected = FestivalGeographyResponse.from(festival);

            // when
            FestivalGeographyResponse result =
                    festivalService.getFestivalGeographyByFestivalId(festivalId);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        void 예외_존재하지_않는_축제_ID로_조회시_예외_발생() {
            // given
            Long invalidFestivalId = 0L;

            given(festivalJpaRepository.findById(invalidFestivalId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(
                    () -> festivalService.getFestivalGeographyByFestivalId(invalidFestivalId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 축제입니다.");
        }
    }

    @Nested
    class getFestivalByFestivalId {

        @Test
        void 성공() {
            // given
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            List<FestivalImage> festivalImages = FestivalImageFixture.createList(2, festival);

            given(festivalJpaRepository.findById(festivalId))
                    .willReturn(Optional.of(festival));
            given(festivalImageJpaRepository.findAllByFestivalIdOrderBySequenceAsc(festivalId))
                    .willReturn(festivalImages);

            // when
            FestivalResponse result = festivalService.getFestivalByFestivalId(festivalId);

            // then
            assertSoftly(s -> {
                s.assertThat(result.universityName()).isEqualTo(festival.getUniversityName());
                s.assertThat(result.festivalImages().responses().get(0).imageUrl())
                        .isEqualTo(festivalImages.get(0).getImageUrl());
                s.assertThat(result.festivalImages().responses().get(1).imageUrl())
                        .isEqualTo(festivalImages.get(1).getImageUrl());
                s.assertThat(result.festivalName()).isEqualTo(festival.getFestivalName());
                s.assertThat(result.startDate()).isEqualTo(festival.getStartDate());
                s.assertThat(result.endDate()).isEqualTo(festival.getEndDate());
            });
        }

        @Test
        void 예외_존재하지_않는_축제_ID로_조회시_예외_발생() {
            // given
            Long invalidFestivalId = 0L;

            given(festivalJpaRepository.findById(invalidFestivalId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> festivalService.getFestivalByFestivalId(invalidFestivalId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 축제입니다.");
        }
    }

    @Nested
    class getUniversitiesByUniversityName {

        @Test
        void 성공() {
            // given
            String universityName1 = "한양 대학교";
            String universityName2 = "한양 에리카 대학교";
            Festival festival1 = FestivalFixture.create(universityName1);
            Festival festival2 = FestivalFixture.create(universityName2);

            String universityNameToSearch = "한양";

            given(festivalJpaRepository.findByUniversityNameContaining(universityNameToSearch))
                    .willReturn(List.of(festival1, festival2));

            // when
            FestivalUniversityResponses results =
                    festivalService.getUniversitiesByUniversityName(universityNameToSearch);

            // then
            assertSoftly(s -> {
                s.assertThat(results.responses().get(0).universityName()).isEqualTo(universityName1);
                s.assertThat(results.responses().get(1).universityName()).isEqualTo(universityName2);
            });
        }
    }

    @Nested
    class updateFestivalInformation {

        @Test
        void 성공() {
            // given
            Festival festival = FestivalFixture.create();

            given(festivalJpaRepository.findById(festival.getId()))
                    .willReturn(Optional.of(festival));

            FestivalInformationUpdateRequest request = FestivalInformationUpdateRequestFixture.create(
                    "수정 후 제목",
                    LocalDate.of(2025, 10, 1),
                    LocalDate.of(2025, 10, 2)
            );

            // when
            FestivalInformationResponse result = festivalService.updateFestivalInformation(
                    festival.getId(),
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
        void 예외_존재하지_않는_축제() {
            // given
            Long invalidFestivalId = 0L;

            FestivalInformationUpdateRequest request = FestivalInformationUpdateRequestFixture.create();

            // when & then
            assertThatThrownBy(() -> festivalService.updateFestivalInformation(invalidFestivalId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 축제입니다.");
        }
    }
}
