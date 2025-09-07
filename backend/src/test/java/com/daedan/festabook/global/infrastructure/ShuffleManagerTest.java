package com.daedan.festabook.global.infrastructure;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ShuffleManagerTest {

    @Nested
    class getShuffledList {

        @Test
        void 성공_null_전달시_빈_리스트_반환() {
            // given
            ShuffleManager shuffleManager = new ShuffleManager();

            // when
            List<Object> result = shuffleManager.getShuffledList(null);

            // then
            assertSoftly(s -> {
                s.assertThat(result).isNotNull();
                s.assertThat(result).isEmpty();
            });
        }

        @Test
        void 성공_빈_리스트_전달시_빈_리스트_반환() {
            // given
            ShuffleManager shuffleManager = new ShuffleManager();

            // when
            List<Object> result = shuffleManager.getShuffledList(List.of());

            // then
            assertSoftly(s -> {
                s.assertThat(result).isNotNull();
                s.assertThat(result).isEmpty();
            });
        }
    }
}
