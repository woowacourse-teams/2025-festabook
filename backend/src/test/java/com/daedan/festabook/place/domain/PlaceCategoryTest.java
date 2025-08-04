package com.daedan.festabook.place.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PlaceCategoryTest {

    @Nested
    class isServiceLocation {
        @ParameterizedTest(name = "카테고리: {0}, 예상 결과: {1}")
        @CsvSource({
                "BOOTH, true",
                "BAR, true",
                "FOOD_TRUCK, true",
                "SMOKING, false",
                "TRASH_CAN, false"
        })
        void 성공_서비스형_카테고리_여부_반환(PlaceCategory category, boolean expected) {
            // when
            boolean result = category.isServiceLocation();

            // then
            assertThat(result).isEqualTo(expected);
        }
    }
}
