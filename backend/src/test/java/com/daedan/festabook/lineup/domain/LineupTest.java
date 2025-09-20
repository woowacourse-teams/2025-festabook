package com.daedan.festabook.lineup.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.global.exception.BusinessException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LineupTest {

    private static final int MAX_NAME_LENGTH = 50;

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

    @Nested
    class isFestivalIdEqualTo {

        @Test
        void 성공_같은_축제의_id이면_true() {
            // given
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            Lineup lineup = LineupFixture.create(festival);

            // when
            boolean result = lineup.isFestivalIdEqualTo(festivalId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        void 성공_다른_축제의_id이면_false() {
            // given
            Long festivalId = 1L;
            Long otherFestivalId = 999L;
            Festival festival = FestivalFixture.create(festivalId);
            Lineup lineup = LineupFixture.create(festival);

            // when
            boolean result = lineup.isFestivalIdEqualTo(otherFestivalId);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    class validateName {

        @Test
        void 성공_이름_유효성_검증() {
            // given
            String name = "비타";

            // when & then
            assertThatCode(() -> LineupFixture.create(name))
                    .doesNotThrowAnyException();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "  "})
        void 예외_이름_유효성_검증(String invalidName) {
            assertThatThrownBy(() -> LineupFixture.create(invalidName))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("이름은 비어 있을 수 없습니다.");
        }

        @ParameterizedTest
        @ValueSource(ints = {1, MAX_NAME_LENGTH})
        void 성공_이름_길이_검증(int len) {
            // given
            String name = "a".repeat(len);

            // when & then
            assertThatCode(() -> LineupFixture.create(name)).doesNotThrowAnyException();
        }

        @ParameterizedTest
        @ValueSource(ints = {MAX_NAME_LENGTH + 1, MAX_NAME_LENGTH + 10})
        void 예외_이름_길이_초과(int len) {
            // given
            String name = "가".repeat(len);

            // when & then
            assertThatThrownBy(() -> LineupFixture.create(name))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("이름은 %d자를 초과할 수 없습니다.", MAX_NAME_LENGTH);
        }
    }
}
