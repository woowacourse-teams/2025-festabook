package com.daedan.festabook.festival.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.daedan.festabook.global.exception.BusinessException;
import java.time.LocalDate;
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

    private static final int MAX_NAME_LENGTH = 50;
    private static final int MIN_ZOOM = 0;
    private static final int MAX_ZOOM = 30;
    private static final int MAX_LOST_ITEM_GUIDE_LENGTH = 1000;

    @Nested
    class validateName {

        @Test
        void 성공_경계값() {
            // given
            String name = "m".repeat(MAX_NAME_LENGTH);

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
                    .hasMessage("이름은 비어 있을 수 없습니다.");
        }

        @Test
        void 예외_축제_이름_blank() {
            // given
            String invalidName = " ";

            // when & then
            assertThatThrownBy(() -> FestivalFixture.create(invalidName))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("이름은 비어 있을 수 없습니다.");
        }

        @Test
        void 예외_축제_이름_길이_초과() {
            // given
            String invalidName = "m".repeat(MAX_NAME_LENGTH + 1);

            // when & then
            assertThatThrownBy(() -> FestivalFixture.create(invalidName))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("이름은 %d자를 초과할 수 없습니다.", MAX_NAME_LENGTH);
        }
    }

    @Nested
    class validateDate {

        @Test
        void 성공() {
            // given
            LocalDate startDate = LocalDate.of(2025, 5, 1);
            LocalDate endDate = LocalDate.of(2025, 5, 2);

            // when & then
            assertThatCode(() -> FestivalFixture.create(startDate, endDate))
                    .doesNotThrowAnyException();
        }

        @Test
        void 성공_시작일과_종료일이_같은_경우() {
            // given
            LocalDate startDate = LocalDate.of(2025, 5, 1);
            LocalDate endDate = LocalDate.of(2025, 5, 1);

            // when & then
            assertThatCode(() -> FestivalFixture.create(startDate, endDate))
                    .doesNotThrowAnyException();
        }

        @Test
        void 예외_시작일_null() {
            // given
            LocalDate startDate = null;
            LocalDate endDate = LocalDate.of(2025, 5, 2);

            // when & then
            assertThatThrownBy(() -> FestivalFixture.create(startDate, endDate))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("시작일과 종료일은 null일 수 없습니다.");
        }

        @Test
        void 예외_종료일_null() {
            // given
            LocalDate startDate = LocalDate.of(2025, 5, 1);
            LocalDate endDate = null;

            // when & then
            assertThatThrownBy(() -> FestivalFixture.create(startDate, endDate))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("시작일과 종료일은 null일 수 없습니다.");
        }

        @Test
        void 예외_종료일이_시작일보다_이전() {
            // given
            LocalDate startDate = LocalDate.of(2025, 5, 2);
            LocalDate endDate = LocalDate.of(2025, 5, 1);

            // when & then
            assertThatThrownBy(() -> FestivalFixture.create(startDate, endDate))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("종료일은 시작일보다 이전일 수 없습니다.");
        }
    }

    @Nested
    class validateZoom {

        @Test
        void 성공_경계값() {
            // given & when & then
            assertThatCode(() -> {
                FestivalFixture.create(MIN_ZOOM);
                FestivalFixture.create(MAX_ZOOM);
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
        @ValueSource(ints = {MIN_ZOOM - 1, MAX_ZOOM + 1})
        void 예외_줌_범위_초과(Integer zoom) {
            // given

            // when & then
            assertThatThrownBy(() -> FestivalFixture.create(zoom))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("줌은 %d 이상 %d 이하이어야 합니다.", MIN_ZOOM, MAX_ZOOM);
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

    @Nested
    class validateLostItemGuide {

        @Test
        void 성공_경계값() {
            // given
            String lostItemGuide = "m".repeat(MAX_LOST_ITEM_GUIDE_LENGTH);

            // when & then
            assertThatCode(() -> FestivalFixture.createWithLostItemGuide(lostItemGuide))
                    .doesNotThrowAnyException();
        }

        @Test
        void 예외_축제_분실물_가이드_null() {
            // given
            String lostItemGuide = null;

            // when & then
            assertThatThrownBy(() -> FestivalFixture.createWithLostItemGuide(lostItemGuide))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("분실물 가이드는 비어 있을 수 없습니다.");
        }

        @Test
        void 예외_축제_분실물_가이드_blank() {
            // given
            String lostItemGuide = " ";

            // when & then
            assertThatThrownBy(() -> FestivalFixture.createWithLostItemGuide(lostItemGuide))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("분실물 가이드는 비어 있을 수 없습니다.");
        }

        @Test
        void 예외_축제_분실물_가이드_초과() {
            // given
            String lostItemGuide = "m".repeat(MAX_LOST_ITEM_GUIDE_LENGTH + 1);

            // when & then
            assertThatThrownBy(() -> FestivalFixture.createWithLostItemGuide(lostItemGuide))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("분실물 가이드는 %d자를 초과할 수 없습니다.", MAX_LOST_ITEM_GUIDE_LENGTH);
        }
    }
}
