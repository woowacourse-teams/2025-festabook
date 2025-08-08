package com.daedan.festabook.festival.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.daedan.festabook.global.exception.BusinessException;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class FestivalTest {

    @Nested
    class validateName {

        @Test
        void 성공_경계값() {
            // given
            int maxNameLength = 50;
            String name = "미".repeat(maxNameLength);

            // when & then
            assertThatCode(() -> FestivalFixture.create(name))
                    .doesNotThrowAnyException();
        }

        @Test
        void 예외_축제_이름_null() {
            // given
            String invalidName = null;

            // when & then
            assertThatThrownBy(() -> FestivalFixture.create(invalidName))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("축제 이름은 비어 있을 수 없습니다.");
        }

        @Test
        void 예외_축제_이름_blank() {
            // given
            String invalidName = " ";

            // when & then
            assertThatThrownBy(() -> FestivalFixture.create(invalidName))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("축제 이름은 비어 있을 수 없습니다.");
        }

        @Test
        void 예외_축제_이름_길이_초과() {
            // given
            int maxNameLength = 50;
            String invalidName = "미".repeat(maxNameLength + 1);

            // when & then
            assertThatThrownBy(() -> FestivalFixture.create(invalidName))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("축제 이름은 50자를 초과할 수 없습니다.");
        }
    }

    @Nested
    class validateZoom {

        @Test
        void 성공_경계값() {
            // given
            Integer minZoom = 0;
            Integer maxZoom = 30;

            // when & then
            assertThatCode(() -> {
                FestivalFixture.create(minZoom);
                FestivalFixture.create(maxZoom);
            })
                    .doesNotThrowAnyException();
        }

        @Test
        void 예외_줌_null() {
            // given
            Integer zoom = null;

            // when & then
            assertThatThrownBy(() -> FestivalFixture.create(zoom))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("줌은 null일 수 없습니다.");
        }

        @ParameterizedTest
        @ValueSource(ints = {-1, 31})
        void 예외_줌_범위_초과(Integer zoom) {
            // given

            // when & then
            assertThatThrownBy(() -> FestivalFixture.create(zoom))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("줌은 0 이상 30 이하이어야 합니다.");
        }
    }

    @Nested
    class validateCenterCoordinate {

        @Test
        void 성공() {
            // given
            Coordinate coordinate = new Coordinate(37.1234, 127.1234);

            // when & then
            assertThatCode(() -> FestivalFixture.create(coordinate))
                    .doesNotThrowAnyException();
        }

        @Test
        void 예외_중심_좌표_null() {
            // given
            Coordinate coordinate = null;

            // when & then
            assertThatThrownBy(() -> FestivalFixture.create(coordinate))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("중심 좌표는 null일 수 없습니다.");
        }
    }

    @Nested
    class validatePolygonHoleBoundary {

        @Test
        void 성공() {
            // given
            List<Coordinate> polygonHoleBoundary = List.of(new Coordinate(37.1234, 127.1234));

            // when & then
            assertThatCode(() -> FestivalFixture.create(polygonHoleBoundary))
                    .doesNotThrowAnyException();
        }

        @Test
        void 예외_폴리곤_내부_구멍_좌표_null() {
            // given
            List<Coordinate> polygonHoleBoundary = null;

            // given & then
            assertThatThrownBy(() -> FestivalFixture.create(polygonHoleBoundary))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("폴리곤 내부 구멍 좌표 리스트는 비어있을 수 없습니다.");
        }

        @Test
        void 예외_폴리곤_내부_구멍_좌표_빈리스트() {
            // given
            List<Coordinate> polygonHoleBoundary = Collections.emptyList();

            // given & then
            assertThatThrownBy(() -> FestivalFixture.create(polygonHoleBoundary))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("폴리곤 내부 구멍 좌표 리스트는 비어있을 수 없습니다.");
        }
    }
}
