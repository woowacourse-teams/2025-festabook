package com.daedan.festabook.place.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.daedan.festabook.festival.domain.Coordinate;
import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PlaceTest {

    @Nested
    class hasDetail {

        @ParameterizedTest(name = "카테고리: {0}, 예상 결과: {1}")
        @CsvSource({
                "BOOTH, true",
                "BAR, true",
                "FOOD_TRUCK, true",
                "SMOKING, false",
                "TRASH_CAN, false"
        })
        void 성공_카테고리에_따라_상세_정보_유무_반환(PlaceCategory category, boolean expected) {
            // given
            Festival festival = FestivalFixture.create();
            Coordinate coordinate = new Coordinate(37.5, 127.0);
            Place place = new Place(festival, category, coordinate);

            // when
            boolean result = place.hasDetail();

            // then
            assertThat(result).isEqualTo(expected);
        }
    }
}
