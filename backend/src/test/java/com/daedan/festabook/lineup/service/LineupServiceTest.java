package com.daedan.festabook.lineup.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.lineup.domain.Lineup;
import com.daedan.festabook.lineup.domain.LineupFixture;
import com.daedan.festabook.lineup.dto.LineupRequest;
import com.daedan.festabook.lineup.dto.LineupRequestFixture;
import com.daedan.festabook.lineup.dto.LineupResponse;
import com.daedan.festabook.lineup.dto.LineupResponses;
import com.daedan.festabook.lineup.dto.LineupUpdateRequest;
import com.daedan.festabook.lineup.dto.LineupUpdateRequestFixture;
import com.daedan.festabook.lineup.infrastructure.LineupJpaRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
class LineupServiceTest {

    @Mock
    private LineupJpaRepository lineupJpaRepository;

    @Mock
    private FestivalJpaRepository festivalJpaRepository;

    @InjectMocks
    private LineupService lineupService;

    @Nested
    class addLineup {

        @Test
        void 성공() {
            // given
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);

            Long lineupId = 1L;
            String name = "이미소";
            String imageUrl = "https://example.com/image.png";
            LocalDateTime performanceAt = LocalDateTime.of(2025, 10, 15, 12, 0, 0);
            Lineup lineup = LineupFixture.create(lineupId, festival, name, imageUrl, performanceAt);

            given(festivalJpaRepository.findById(festivalId))
                    .willReturn(Optional.of(festival));
            given(lineupJpaRepository.save(any(Lineup.class)))
                    .willReturn(lineup);

            LineupRequest request = LineupRequestFixture.create();

            // when
            LineupResponse result = lineupService.addLineup(festivalId, request);

            // then
            assertSoftly(s -> {
                s.assertThat(result.lineupId()).isEqualTo(lineup.getId());
                s.assertThat(result.name()).isEqualTo(lineup.getName());
            });
        }

        @Test
        void 예외_존재하지_않는_축제() {
            // given
            Long invalidFestivalId = 0L;
            LineupRequest request = LineupRequestFixture.create();

            given(festivalJpaRepository.findById(invalidFestivalId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> lineupService.addLineup(invalidFestivalId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 축제입니다.");
        }
    }

    @Nested
    class getAllLineupByFestivalId {

        @Test
        void 성공_날짜_오름차순_정렬_반환() {
            // given
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);

            Lineup lineup1 = LineupFixture.create(festival, "이미소", LocalDateTime.of(2025, 5, 3, 20, 0));
            Lineup lineup2 = LineupFixture.create(festival, "부기", LocalDateTime.of(2025, 5, 2, 20, 0));
            Lineup lineup3 = LineupFixture.create(festival, "후유", LocalDateTime.of(2025, 5, 2, 19, 0));
            Lineup lineup4 = LineupFixture.create(festival, "타마", LocalDateTime.of(2025, 5, 1, 20, 0));

            given(lineupJpaRepository.findAllByFestivalId(festivalId))
                    .willReturn(new ArrayList<>(List.of(lineup1, lineup2, lineup3, lineup4)));

            // when
            LineupResponses result = lineupService.getAllLineupByFestivalId(festivalId);

            // then
            List<String> names = result.responses().stream()
                    .map(LineupResponse::name)
                    .toList();

            assertThat(names).containsExactly("타마", "후유", "부기", "이미소");
        }
    }

    @Nested
    class updateLineup {

        @Test
        void 성공() {
            // given
            Long lineupId = 1L;
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            Lineup lineup = LineupFixture.create(lineupId, festival);
            LineupUpdateRequest request = LineupUpdateRequestFixture.create("후유", "https://example.com/image.png",
                    LocalDateTime.of(2025, 10, 15, 12, 0, 0));

            given(lineupJpaRepository.findById(lineupId))
                    .willReturn(Optional.of(lineup));

            // when
            LineupResponse result = lineupService.updateLineup(lineupId, festivalId, request);

            // then
            assertSoftly(s -> {
                s.assertThat(result.lineupId()).isEqualTo(lineupId);
                s.assertThat(result.name()).isEqualTo(request.name());
                s.assertThat(result.imageUrl()).isEqualTo(request.imageUrl());
                s.assertThat(result.performanceAt()).isEqualTo(request.performanceAt());
            });
        }

        @Test
        void 예외_존재하지_않는_라인업() {
            // given
            Long invalidLineupId = 0L;
            Long festivalId = 1L;
            LineupUpdateRequest request = LineupUpdateRequestFixture.create();

            given(lineupJpaRepository.findById(invalidLineupId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> lineupService.updateLineup(invalidLineupId, festivalId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 라인업입니다.");
        }

        @Test
        void 예외_다른_축제의_라인업일_경우() {
            // given
            Long requestFestivalId = 1L;
            Long otherFestivalId = 999L;
            Festival requestFestival = FestivalFixture.create(requestFestivalId);
            Festival otherFestival = FestivalFixture.create(otherFestivalId);
            Lineup lineup = LineupFixture.create(requestFestival);

            given(lineupJpaRepository.findById(lineup.getId()))
                    .willReturn(Optional.of(lineup));

            LineupUpdateRequest request = LineupUpdateRequestFixture.create();

            // when & then
            assertThatThrownBy(() -> lineupService.updateLineup(lineup.getId(), otherFestival.getId(), request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("해당 축제의 라인업이 아닙니다.");
        }
    }

    @Nested
    class deleteLineupByLineupId {

        @Test
        void 성공() {
            // given
            Long lineupId = 1L;
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            Lineup lineup = LineupFixture.create(lineupId, festival);

            given(lineupJpaRepository.findById(lineupId))
                    .willReturn(Optional.of(lineup));

            // when
            lineupService.deleteLineupByLineupId(lineupId, festival.getId());

            // then
            then(lineupJpaRepository).should()
                    .deleteById(lineupId);
        }

        @Test
        void 예외_다른_축제의_라인업일_경우() {
            // given
            Long requestFestivalId = 1L;
            Long otherFestivalId = 999L;
            Festival requestFestival = FestivalFixture.create(requestFestivalId);
            Festival otherFestival = FestivalFixture.create(otherFestivalId);
            Lineup lineup = LineupFixture.create(requestFestival);

            given(lineupJpaRepository.findById(lineup.getId()))
                    .willReturn(Optional.of(lineup));

            // when & then
            assertThatThrownBy(() -> lineupService.deleteLineupByLineupId(lineup.getId(), otherFestival.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("해당 축제의 라인업이 아닙니다.");
        }
    }
}
