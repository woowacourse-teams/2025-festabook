package com.daedan.festabook.timetag.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TimeTagTest {

    private static final int MAX_NAME_LENGTH = 40;

    @Nested
    class validateName {

        @Test
        void 성공_경계값() {
            // given
            String name = "미".repeat(MAX_NAME_LENGTH);

            // when & then
            assertThatCode(() -> TimeTagFixture.createWithName(name))
                    .doesNotThrowAnyException();
        }

        @ParameterizedTest
        @NullAndEmptySource
        void 예외_null(String name) {
            // when & then
            assertThatThrownBy(() -> TimeTagFixture.createWithName(name))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("시간 태그의 이름은 공백이거나 null일 수 없습니다.");
        }

        @Test
        void 예외_name_길이_초과() {
            // given
            String name = "미".repeat(MAX_NAME_LENGTH + 1);

            // when & then
            assertThatThrownBy(() -> TimeTagFixture.createWithName(name))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("시간 태그의 이름의 길이는 %d자를 초과할 수 없습니다.", MAX_NAME_LENGTH);
        }
    }

    @Nested
    class isFestivalIdEqualTo {

        @Test
        void 성공_같은_축제의_id이면_true() {
            // given
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            TimeTag timeTag = TimeTagFixture.createWithFestival(festival);

            // when
            boolean result = timeTag.isFestivalIdEqualTo(festivalId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        void 다른_축제의_id이면_false() {
            // given
            Long festivalId = 1L;
            Long otherFestivalId = 999L;
            Festival festival = FestivalFixture.create(festivalId);
            TimeTag timeTag = TimeTagFixture.createWithFestival(festival);

            // when
            boolean result = timeTag.isFestivalIdEqualTo(otherFestivalId);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    class updateTimeTag {

        @Test
        void 성공() {
            // given
            String originalName = "오전";
            TimeTag originalTimeTag = TimeTagFixture.createWithName(originalName);

            String newName = "오후";
            TimeTag newTimeTag = TimeTagFixture.createWithName(newName);

            // when
            originalTimeTag.updateTimeTag(newTimeTag);

            // then
            assertThat(originalTimeTag.getName()).isEqualTo(newName);
        }
    }
}
