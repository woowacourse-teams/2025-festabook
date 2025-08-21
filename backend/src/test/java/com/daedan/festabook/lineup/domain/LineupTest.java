package com.daedan.festabook.lineup.domain;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LineupTest {

    @Nested
    class updateLineup {

        @Test
        void 성공() {
            // given
            Festival festival = FestivalFixture.create();
            Lineup lineup = LineupFixture.create(
                    festival,
                    "기존이름",
                    LocalDateTime.of(2025, 5, 1, 12, 0)
            );

            String updatedName = "수정된이름";
            String updatedImageUrl = "https://updated-image.example/image.jpg";
            LocalDateTime updatedPerformanceAt = LocalDateTime.of(2025, 5, 2, 15, 0);

            // when
            lineup.updateLineup(updatedName, updatedImageUrl, updatedPerformanceAt);

            // then
            assertSoftly(s -> {
                s.assertThat(lineup.getName()).isEqualTo(updatedName);
                s.assertThat(lineup.getImageUrl()).isEqualTo(updatedImageUrl);
                s.assertThat(lineup.getPerformanceAt()).isEqualTo(updatedPerformanceAt);
            });
        }
    }
}
