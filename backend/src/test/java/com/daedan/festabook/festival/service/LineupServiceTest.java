package com.daedan.festabook.festival.service;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.daedan.festabook.festival.controller.LineupRequest;
import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.festival.domain.Lineup;
import com.daedan.festabook.festival.domain.LineupFixture;
import com.daedan.festabook.festival.domain.LineupRequestFixture;
import com.daedan.festabook.festival.dto.LineupResponse;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.festival.infrastructure.LineupJpaRepository;
import java.time.LocalDateTime;
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
    }
}
