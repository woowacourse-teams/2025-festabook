package com.daedan.festabook.organization.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.daedan.festabook.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CoordinateTest {

    private final Double DEFAULT_LATITUDE = 37.0;
    private final Double DEFAULT_LONGITUDE = 127.0;

    @Nested
    class validateLatitude {

        @Test
        void 성공_경계값() {
            // given
            Double min = -90.0;
            Double max = 90.0;

            // when & then
            assertThatCode(() -> {
                new Coordinate(min, DEFAULT_LONGITUDE);
                new Coordinate(max, DEFAULT_LONGITUDE);
            })
                    .doesNotThrowAnyException();
        }

        @Test
        void 예외_위도_null() {
            // given
            Double latitude = null;

            // when & then
            assertThatThrownBy(() -> new Coordinate(latitude, DEFAULT_LONGITUDE))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("위도는 null일 수 없습니다.");
        }

        @ParameterizedTest
        @ValueSource(doubles = {-90.1, 90.1})
        void 예외_위도_범위_초과(Double latitude) {
            // given

            // when & then
            assertThatThrownBy(() -> new Coordinate(latitude, DEFAULT_LONGITUDE))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("위도는 -90.0도 이상 90.0도 이하여야 합니다.");
        }
    }

    @Nested
    class validateLongitude {

        @Test
        void 성공_경계값() {
            // given
            Double min = -180.0;
            Double max = 180.0;

            // when & then
            assertThatCode(() -> {
                new Coordinate(DEFAULT_LATITUDE, min);
                new Coordinate(DEFAULT_LATITUDE, max);
            })
                    .doesNotThrowAnyException();
        }

        @Test
        void 예외_경도_null() {
            // given
            Double longitude = null;

            // when & then
            assertThatThrownBy(() -> new Coordinate(DEFAULT_LATITUDE, longitude))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("경도는 null일 수 없습니다.");
        }

        @ParameterizedTest
        @ValueSource(doubles = {-180.1, 180.1})
        void 예외_경도_범위_초과(Double longitude) {
            // given

            // when & then
            assertThatThrownBy(() -> new Coordinate(DEFAULT_LATITUDE, longitude))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("경도는 -180.0도 이상 180.0도 이하여야 합니다.");
        }
    }
}
