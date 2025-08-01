package com.daedan.festabook.place.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
public class PlaceDetailTest {

    private static final int MAX_TITLE_LENGTH = 20;
    private static final int MAX_DESCRIPTION_LENGTH = 100;
    private static final int MAX_LOCATION_LENGTH = 100;
    private static final int MAX_HOST_LENGTH = 100;

    @Nested
    class validatePlaceDetail {

        @Test
        void 성공() {
            // given
            String title = "플레이스 이름";
            String content = "플레이스 내용";
            String location = "플레이스 위치";
            String host = "플레이스 호스트";
            LocalTime startTime = LocalTime.of(12, 30);
            LocalTime endTime = LocalTime.of(13, 0);

            // when & then
            assertThatCode(() -> {
                PlaceDetailFixture.create(
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
    class validatePlace {

        @Test
        void 예외_플레이스는_null일_수_없음() {
            // given
            Place place = null;

            // when & then
            assertThatThrownBy(() -> PlaceDetailFixture.create(place))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("플레이스는 null일 수 없습니다.");
        }
    }

    @Nested
    class validateTitle {

        @Test
        void 성공_플레이스_이름_null() {
            // given
            String title = null;

            // when & then
            assertThatCode(() -> PlaceDetailFixture.createWithTitle(title))
                    .doesNotThrowAnyException();
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 5, 10, MAX_TITLE_LENGTH})
        void 성공_플레이스_이름_길이_경계값(int length) {
            // given
            String title = "m".repeat(length);

            // when & then
            assertThatCode(() -> PlaceDetailFixture.createWithTitle(title))
                    .doesNotThrowAnyException();
        }

        @Test
        void 예외_플레이스_이름_공백() {
            // given
            String title = " ";

            // when & then
            assertThatThrownBy(() -> PlaceDetailFixture.createWithTitle(title))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("플레이스의 이름은 공백일 수 없습니다.");
        }

        @Test
        void 예외_플레이스_이름_최대_길이_초과() {
            // given
            String title = "m".repeat(MAX_TITLE_LENGTH + 1);

            // when & then
            assertThatThrownBy(() -> PlaceDetailFixture.createWithTitle(title))
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
            assertThatCode(() -> PlaceDetailFixture.createWithDescription(description))
                    .doesNotThrowAnyException();
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 40, 80, MAX_DESCRIPTION_LENGTH})
        void 성공_플레이스_설명_길이_경계값(int length) {
            // given
            String description = "m".repeat(length);

            // when & then
            assertThatCode(() -> PlaceDetailFixture.createWithDescription(description))
                    .doesNotThrowAnyException();
        }

        @Test
        void 예외_플레이스_설명_공백() {
            // given
            String description = " ";

            // when & then
            assertThatThrownBy(() -> PlaceDetailFixture.createWithDescription(description))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("플레이스의 설명은 공백일 수 없습니다.");
        }

        @Test
        void 예외_플레이스_설명_최대_길이_초과() {
            // given
            String description = "m".repeat(MAX_DESCRIPTION_LENGTH + 1);

            // when & then
            assertThatThrownBy(() -> PlaceDetailFixture.createWithDescription(description))
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
            assertThatCode(() -> PlaceDetailFixture.createWithLocation(location))
                    .doesNotThrowAnyException();
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 40, 80, MAX_LOCATION_LENGTH})
        void 성공_플레이스_위치_길이_경계값(int length) {
            // given
            String location = "m".repeat(length);

            // when & then
            assertThatCode(() -> PlaceDetailFixture.createWithLocation(location))
                    .doesNotThrowAnyException();
        }

        @Test
        void 예외_플레이스_위치_공백() {
            // given
            String location = " ";

            // when & then
            assertThatThrownBy(() -> PlaceDetailFixture.createWithLocation(location))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("플레이스의 위치는 공백일 수 없습니다.");
        }

        @Test
        void 예외_플레이스_위치_최대_길이_초과() {
            // given
            String location = "m".repeat(MAX_LOCATION_LENGTH + 1);

            // when & then
            assertThatThrownBy(() -> PlaceDetailFixture.createWithLocation(location))
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
            assertThatCode(() -> PlaceDetailFixture.createWithHost(host))
                    .doesNotThrowAnyException();
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 40, 80, MAX_HOST_LENGTH})
        void 성공_플레이스_호스트_길이_경계값(int length) {
            // given
            String host = "m".repeat(length);

            // when & then
            assertThatCode(() -> PlaceDetailFixture.createWithHost(host))
                    .doesNotThrowAnyException();
        }

        @Test
        void 예외_플레이스_호스트_공백() {
            // given
            String host = " ";

            // when & then
            assertThatThrownBy(() -> PlaceDetailFixture.createWithHost(host))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("플레이스의 호스트는 공백일 수 없습니다.");
        }

        @Test
        void 예외_플레이스_호스트_최대_길이_초과() {
            // given
            String host = "m".repeat(MAX_HOST_LENGTH + 1);

            // when & then
            assertThatThrownBy(() -> PlaceDetailFixture.createWithHost(host))
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
            assertThatCode(() -> PlaceDetailFixture.createWithTime(startTime, endTime))
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
            assertThatThrownBy(() -> PlaceDetailFixture.createWithTime(startTime, endTime))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("플레이스의 시작, 종료 시간 둘 중 하나만 비어있을 수 없습니다. 둘다 비어있거나 둘다 정해져 있어야 합니다.");
        }
    }
}
