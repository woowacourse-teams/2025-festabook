package com.daedan.festabook.organization.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.daedan.festabook.global.exception.BusinessException;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class OrganizationTest {

    private final String DEFAULT_NAME = "학생회";
    private final Integer DEFAULT_ZOOM = 10;
    private final Coordinate DEFAULT_COORDINATE = new Coordinate(37.5665, 126.9780);
    private final List<Coordinate> DEFAULT_BOUNDARY = List.of(new Coordinate(37.1234, 127.1234));

    @Nested
    class validateName {

        @Test
        void 성공_경계값() {
            // given
            String name = "미".repeat(50);

            // when & then
            assertThatCode(() -> new Organization(name, DEFAULT_ZOOM, DEFAULT_COORDINATE, DEFAULT_BOUNDARY))
                    .doesNotThrowAnyException();
        }

        @Test
        void 예외_조직_이름_null() {
            // given
            String invalidName = null;

            // when & then
            assertThatThrownBy(() -> new Organization(invalidName, DEFAULT_ZOOM, DEFAULT_COORDINATE, DEFAULT_BOUNDARY))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("조직 이름은 비어 있을 수 없습니다.");
        }

        @Test
        void 예외_조직_이름_blank() {
            // given
            String invalidName = " ";

            // when & then
            assertThatThrownBy(() -> new Organization(invalidName, DEFAULT_ZOOM, DEFAULT_COORDINATE, DEFAULT_BOUNDARY))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("조직 이름은 비어 있을 수 없습니다.");
        }

        @Test
        void 예외_조직_이름_길이_초과() {
            // given
            String invalidName = "미".repeat(51);

            // when & then
            assertThatThrownBy(() -> new Organization(invalidName, DEFAULT_ZOOM, DEFAULT_COORDINATE, DEFAULT_BOUNDARY))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("조직 이름은 50자를 초과할 수 없습니다.");
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
                new Organization(DEFAULT_NAME, minZoom, DEFAULT_COORDINATE, DEFAULT_BOUNDARY);
                new Organization(DEFAULT_NAME, maxZoom, DEFAULT_COORDINATE, DEFAULT_BOUNDARY);
            })
                    .doesNotThrowAnyException();
        }

        @Test
        void 예외_줌_null() {
            // given
            Integer zoom = null;

            // when & then
            assertThatThrownBy(() -> new Organization(DEFAULT_NAME, zoom, DEFAULT_COORDINATE, DEFAULT_BOUNDARY))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("줌은 null일 수 없습니다.");
        }

        @Test
        void 예외_줌_최솟값_미만() {
            // given
            Integer zoom = -1;

            // when & then
            assertThatThrownBy(() -> new Organization(DEFAULT_NAME, zoom, DEFAULT_COORDINATE, DEFAULT_BOUNDARY))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("줌은 0 이상 30 이하이어야 합니다.");
        }

        @Test
        void 예외_줌_최댓값_초과() {
            // given
            Integer zoom = 31;

            // when & then
            assertThatThrownBy(() -> new Organization(DEFAULT_NAME, zoom, DEFAULT_COORDINATE, DEFAULT_BOUNDARY))
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
            assertThatCode(() -> new Organization(DEFAULT_NAME, DEFAULT_ZOOM, coordinate, DEFAULT_BOUNDARY))
                    .doesNotThrowAnyException();
        }

        @Test
        void 예외_중심_좌표_null() {
            // given
            Coordinate coordinate = null;

            // when & then
            assertThatThrownBy(() -> new Organization(DEFAULT_NAME, DEFAULT_ZOOM, coordinate, DEFAULT_BOUNDARY))
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
            assertThatCode(() -> new Organization(DEFAULT_NAME, DEFAULT_ZOOM, DEFAULT_COORDINATE, polygonHoleBoundary))
                    .doesNotThrowAnyException();
        }

        @Test
        void 예외_폴리곤_내부_구멍_좌표_null() {
            // given
            List<Coordinate> polygonHoleBoundary = null;

            // given & then
            assertThatThrownBy(() ->
                    new Organization(DEFAULT_NAME, DEFAULT_ZOOM, DEFAULT_COORDINATE, polygonHoleBoundary)
            )
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("폴리곤 내부 구멍 좌표 리스트는 비어있을 수 없습니다.");
        }

        @Test
        void 예외_폴리곤_내부_구멍_좌표_빈리스트() {
            // given
            List<Coordinate> polygonHoleBoundary = Collections.emptyList();

            // given & then
            assertThatThrownBy(() ->
                    new Organization(DEFAULT_NAME, DEFAULT_ZOOM, DEFAULT_COORDINATE, polygonHoleBoundary)
            )
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("폴리곤 내부 구멍 좌표 리스트는 비어있을 수 없습니다.");
        }
    }
}
