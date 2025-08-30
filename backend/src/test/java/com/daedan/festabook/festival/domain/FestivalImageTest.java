package com.daedan.festabook.festival.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class FestivalImageTest {

    @Nested
    class isFestivalIdEqualTo {

        @Test
        void 같은_축제의_id이면_true() {
            // given
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            FestivalImage festivalImage = FestivalImageFixture.create(festival);

            // when
            boolean result = festivalImage.isFestivalIdEqualTo(festivalId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        void 다른_축제의_id이면_false() {
            // given
            Long festivalId = 1L;
            Long otherFestivalId = 999L;
            Festival festival = FestivalFixture.create(festivalId);
            FestivalImage festivalImage = FestivalImageFixture.create(festival);

            // when
            boolean result = festivalImage.isFestivalIdEqualTo(otherFestivalId);

            // then
            assertThat(result).isFalse();
        }
    }
}
