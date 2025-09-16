package com.daedan.festabook.lineup.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.lineup.domain.Lineup;
import com.daedan.festabook.lineup.domain.LineupFixture;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LineupJpaRepositoryTest {

    @Autowired
    private FestivalJpaRepository festivalJpaRepository;

    @Autowired
    private LineupJpaRepository lineupJpaRepository;

    @Nested
    class existsByFestivalIdAndPerformanceAt {

        @Test
        void 페스티벌ID와_공연시간_일치_데이터_존재_여부() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            LocalDateTime performanceAt = LocalDateTime.of(2025, 10, 1, 18, 0);
            Lineup lineup = LineupFixture.create(festival, performanceAt);
            lineupJpaRepository.save(lineup);

            // when
            boolean exists = lineupJpaRepository.existsByFestivalIdAndPerformanceAt(festival.getId(), performanceAt);

            // then
            assertThat(exists).isTrue();
        }

        @Test
        void 동일_페스티벌ID_다른_공연시간_데이터_존재_여부() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            LocalDateTime savedPerformanceAt = LocalDateTime.of(2025, 10, 1, 18, 0);
            Lineup savedLineup = LineupFixture.create(festival, savedPerformanceAt);
            lineupJpaRepository.save(savedLineup);

            LocalDateTime searchPerformanceAt = LocalDateTime.of(2025, 10, 1, 19, 0);

            // when
            boolean exists = lineupJpaRepository.existsByFestivalIdAndPerformanceAt(festival.getId(),
                    searchPerformanceAt);

            // then
            assertThat(exists).isFalse();
        }

        @Test
        void 동일_공연시간_다른_페스티벌ID_데이터_존재_여부() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            LocalDateTime performanceAt = LocalDateTime.of(2025, 10, 1, 18, 0);
            Lineup lineup = LineupFixture.create(festival, performanceAt);
            lineupJpaRepository.save(lineup);

            Festival otherFestival = FestivalFixture.create();
            festivalJpaRepository.save(otherFestival);

            // when
            boolean exists = lineupJpaRepository.existsByFestivalIdAndPerformanceAt(otherFestival.getId(),
                    performanceAt);

            // then
            assertThat(exists).isFalse();
        }

        @Test
        void 존재하지_않는_데이터_조회_결과() {
            // given
            Long nonExistentFestivalId = 0L;
            LocalDateTime nonExistentPerformanceAt = LocalDateTime.of(2025, 12, 31, 23, 59);

            // when
            boolean exists = lineupJpaRepository.existsByFestivalIdAndPerformanceAt(
                    nonExistentFestivalId,
                    nonExistentPerformanceAt
            );

            // then
            assertThat(exists).isFalse();
        }
    }
}
