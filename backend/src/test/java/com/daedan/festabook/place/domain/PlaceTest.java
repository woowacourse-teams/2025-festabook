package com.daedan.festabook.place.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.daedan.festabook.festival.domain.Coordinate;
import com.daedan.festabook.festival.domain.CoordinateFixture;
import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.global.exception.BusinessException;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PlaceTest {

    private static final int MAX_TITLE_LENGTH = 20;
    private static final int MAX_DESCRIPTION_LENGTH = 100;
    private static final int MAX_LOCATION_LENGTH = 100;
    private static final int MAX_HOST_LENGTH = 100;

    @Nested
    class validatePlace {

        @Test
        void 성공() {
            // given

            Festival festival = FestivalFixture.create();
            PlaceCategory placeCategory = PlaceCategory.BOOTH;
            Coordinate coordinate = CoordinateFixture.create();
            String title = "플레이스 이름";
            String content = "플레이스 내용";
            String location = "플레이스 위치";
            String host = "플레이스 호스트";
            LocalTime startTime = LocalTime.of(12, 30);
            LocalTime endTime = LocalTime.of(13, 0);

            // when & then
            assertThatCode(() -> {
                PlaceFixture.create(
                        festival,
                        placeCategory,
                        coordinate,
                        title,
                        content,
                        location,
                        host,
                        startTime,
                        endTime
                );
            })
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    class validateTitle {

        @Test
        void 성공_플레이스_이름_null() {
            // given
            String title = null;

            // when & then
            assertThatCode(() -> PlaceFixture.createWithTitle(title))
                    .doesNotThrowAnyException();
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 5, 10, MAX_TITLE_LENGTH})
        void 성공_플레이스_이름_길이_경계값(int length) {
            // given
            String title = "m".repeat(length);

            // when & then
            assertThatCode(() -> PlaceFixture.createWithTitle(title))
                    .doesNotThrowAnyException();
        }

        @Test
        void 예외_플레이스_이름_공백() {
            // given
            String title = " ";

            // when & then
            assertThatThrownBy(() -> PlaceFixture.createWithTitle(title))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("플레이스의 이름은 공백일 수 없습니다.");
        }

        @Test
        void 예외_플레이스_이름_최대_길이_초과() {
            // given
            String title = "m".repeat(MAX_TITLE_LENGTH + 1);

            // when & then
            assertThatThrownBy(() -> PlaceFixture.createWithTitle(title))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("플레이스의 이름의 길이는 %d자를 초과할 수 없습니다.", MAX_TITLE_LENGTH);
        }
    }

    @Nested
    class validateDescription {

        @Test
        void 성공_플레이스_설명_null() {
            // given
            String description = null;

            // when & then
            assertThatCode(() -> PlaceFixture.createWithDescription(description))
                    .doesNotThrowAnyException();
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 40, 80, MAX_DESCRIPTION_LENGTH})
        void 성공_플레이스_설명_길이_경계값(int length) {
            // given
            String description = "m".repeat(length);

            // when & then
            assertThatCode(() -> PlaceFixture.createWithDescription(description))
                    .doesNotThrowAnyException();
        }

        @Test
        void 예외_플레이스_설명_공백() {
            // given
            String description = " ";

            // when & then
            assertThatThrownBy(() -> PlaceFixture.createWithDescription(description))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("플레이스의 설명은 공백일 수 없습니다.");
        }

        @Test
        void 예외_플레이스_설명_최대_길이_초과() {
            // given
            String description = "m".repeat(MAX_DESCRIPTION_LENGTH + 1);

            // when & then
            assertThatThrownBy(() -> PlaceFixture.createWithDescription(description))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("플레이스 설명의 길이는 %d자를 초과할 수 없습니다.", MAX_DESCRIPTION_LENGTH);
        }
    }

    @Nested
    class validateLocation {

        @Test
        void 성공_플레이스_위치_null() {
            // given
            String location = null;

            // when & then
            assertThatCode(() -> PlaceFixture.createWithLocation(location))
                    .doesNotThrowAnyException();
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 40, 80, MAX_LOCATION_LENGTH})
        void 성공_플레이스_위치_길이_경계값(int length) {
            // given
            String location = "m".repeat(length);

            // when & then
            assertThatCode(() -> PlaceFixture.createWithLocation(location))
                    .doesNotThrowAnyException();
        }

        @Test
        void 예외_플레이스_위치_공백() {
            // given
            String location = " ";

            // when & then
            assertThatThrownBy(() -> PlaceFixture.createWithLocation(location))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("플레이스의 위치는 공백일 수 없습니다.");
        }

        @Test
        void 예외_플레이스_위치_최대_길이_초과() {
            // given
            String location = "m".repeat(MAX_LOCATION_LENGTH + 1);

            // when & then
            assertThatThrownBy(() -> PlaceFixture.createWithLocation(location))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("플레이스 위치의 길이는 %d자를 초과할 수 없습니다.", MAX_LOCATION_LENGTH);
        }
    }

    @Nested
    class validateHost {

        @Test
        void 성공_null() {
            // given
            String host = null;

            // when & then
            assertThatCode(() -> PlaceFixture.createWithHost(host))
                    .doesNotThrowAnyException();
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 40, 80, MAX_HOST_LENGTH})
        void 성공_플레이스_호스트_길이_경계값(int length) {
            // given
            String host = "m".repeat(length);

            // when & then
            assertThatCode(() -> PlaceFixture.createWithHost(host))
                    .doesNotThrowAnyException();
        }

        @Test
        void 예외_플레이스_호스트_공백() {
            // given
            String host = " ";

            // when & then
            assertThatThrownBy(() -> PlaceFixture.createWithHost(host))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("플레이스의 호스트는 공백일 수 없습니다.");
        }

        @Test
        void 예외_플레이스_호스트_최대_길이_초과() {
            // given
            String host = "m".repeat(MAX_HOST_LENGTH + 1);

            // when & then
            assertThatThrownBy(() -> PlaceFixture.createWithHost(host))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("플레이스 호스트의 길이는 %d자를 초과할 수 없습니다.", MAX_HOST_LENGTH);
        }
    }

    @Nested
    class validateTime {

        @Test
        void 성공_동시에_null() {
            // given
            LocalTime startTime = null;
            LocalTime endTime = null;

            // when & then
            assertThatCode(() -> PlaceFixture.createWithTime(startTime, endTime))
                    .doesNotThrowAnyException();
        }

        @ParameterizedTest
        @CsvSource({
                ",13:00",
                "12:30,"
        })
        void 예외_시작시간_종료시간_둘_중_하나만_null일_수_없음(
                LocalTime startTime,
                LocalTime endTime
        ) {
            // given & when & then
            assertThatThrownBy(() -> PlaceFixture.createWithTime(startTime, endTime))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("플레이스의 시작 날짜, 종료 날짜는 모두 비어 있거나 모두 입력되어야 합니다.");
        }
    }

    @Nested
    class isMainPlace {

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
            Place place = PlaceFixture.create(category);

            // when
            boolean result = place.isMainPlace();

            // then
            assertThat(result).isEqualTo(expected);
        }
    }

    @Nested
    class updatePlace {

        @Test
        void 성공() {
            // given
            PlaceCategory placeCategory = PlaceCategory.FOOD_TRUCK;
            String title = "수정된 이름";
            String description = "수정된 설명";
            String location = "수정된 위치";
            String host = "수정된 호스트";
            LocalTime startTime = LocalTime.of(9, 10);
            LocalTime endTime = LocalTime.of(23, 14);

            Place place = PlaceFixture.create();

            // when
            place.updatePlace(
                    placeCategory,
                    title,
                    description,
                    location,
                    host,
                    startTime,
                    endTime
            );

            // then
            assertSoftly(s -> {
                s.assertThat(place.getCategory()).isEqualTo(placeCategory);
                s.assertThat(place.getTitle()).isEqualTo(title);
                s.assertThat(place.getDescription()).isEqualTo(description);
                s.assertThat(place.getLocation()).isEqualTo(location);
                s.assertThat(place.getHost()).isEqualTo(host);
                s.assertThat(place.getStartTime()).isEqualTo(startTime);
                s.assertThat(place.getEndTime()).isEqualTo(endTime);
            });
        }

        @Test
        void 예외_제목() {
            // given
            PlaceCategory placeCategory = PlaceCategory.FOOD_TRUCK;
            String description = "수정된 설명";
            String location = "수정된 위치";
            String host = "수정된 호스트";
            LocalTime startTime = LocalTime.of(9, 10);
            LocalTime endTime = LocalTime.of(23, 14);

            String invalidTitle = "m".repeat(MAX_TITLE_LENGTH + 1);

            Place place = PlaceFixture.create();

            // when & then
            assertThatThrownBy(() -> {
                place.updatePlace(
                        placeCategory,
                        invalidTitle,
                        description,
                        location,
                        host,
                        startTime,
                        endTime
                );
            })
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("초과할 수 없습니다.");
        }

        @Test
        void 예외_설명() {
            // given
            PlaceCategory placeCategory = PlaceCategory.FOOD_TRUCK;
            String title = "수정된 이름";
            String location = "수정된 위치";
            String host = "수정된 호스트";
            LocalTime startTime = LocalTime.of(9, 10);
            LocalTime endTime = LocalTime.of(23, 14);

            Place place = PlaceFixture.create();

            String invalidDescription = "m".repeat(MAX_DESCRIPTION_LENGTH + 1);

            // when & then
            assertThatThrownBy(() -> {
                place.updatePlace(
                        placeCategory,
                        title,
                        invalidDescription,
                        location,
                        host,
                        startTime,
                        endTime
                );
            })
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("초과할 수 없습니다.");
        }

        @Test
        void 예외_위치() {
            // given
            PlaceCategory placeCategory = PlaceCategory.FOOD_TRUCK;
            String title = "수정된 이름";
            String description = "수정된 설명";
            String host = "수정된 호스트";
            LocalTime startTime = LocalTime.of(9, 10);
            LocalTime endTime = LocalTime.of(23, 14);

            Place place = PlaceFixture.create();

            String invalidLocation = "m".repeat(MAX_LOCATION_LENGTH + 1);

            // when & then
            assertThatThrownBy(() -> {
                place.updatePlace(
                        placeCategory,
                        title,
                        description,
                        invalidLocation,
                        host,
                        startTime,
                        endTime
                );
            })
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("초과할 수 없습니다.");
        }

        @Test
        void 예외_호스트() {
            // given
            PlaceCategory placeCategory = PlaceCategory.FOOD_TRUCK;
            String title = "수정된 이름";
            String description = "수정된 설명";
            String location = "수정된 위치";
            LocalTime startTime = LocalTime.of(9, 10);
            LocalTime endTime = LocalTime.of(23, 14);

            Place place = PlaceFixture.create();

            String invalidHost = "m".repeat(MAX_HOST_LENGTH + 1);

            // when & then
            assertThatThrownBy(() -> {
                place.updatePlace(
                        placeCategory,
                        title,
                        description,
                        location,
                        invalidHost,
                        startTime,
                        endTime
                );
            })
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("초과할 수 없습니다.");
        }

        @Test
        void 예외_시간() {
            // given
            PlaceCategory placeCategory = PlaceCategory.FOOD_TRUCK;
            String title = "수정된 이름";
            String description = "수정된 설명";
            String location = "수정된 위치";
            String host = "수정된 호스트";
            LocalTime startTime = LocalTime.of(9, 10);

            Place place = PlaceFixture.create();

            LocalTime invalidTime = null;

            // when & then
            assertThatThrownBy(() -> {
                place.updatePlace(
                        placeCategory,
                        title,
                        description,
                        location,
                        host,
                        startTime,
                        invalidTime
                );
            })
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("플레이스의 시작 날짜, 종료 날짜는 모두 비어 있거나 모두 입력되어야 합니다.");
        }
    }
}
