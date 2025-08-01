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

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class PlaceDetailTest {

    @Nested
    class validate {

        @Test
        void 성공() {
            // given & when & then
            assertThatCode(() -> {
                PlaceDetailFixture.create(
                        "플레이스 이름",
                        "플레이스 설명",
                        "플레이스 위치",
                        "플레이스 호스트",
                        LocalTime.of(12, 30),
                        LocalTime.of(13, 0)
                );
            }).doesNotThrowAnyException();
        }
    }

    @Nested
    class validatePlace {

        @Test
        void 예외_null일_수_없음() {
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
        void 성공_null_허용() {
            // given
            String title = null;

            // when & then
            assertThatCode(() -> PlaceDetailFixture.createWithTitle(title))
                    .doesNotThrowAnyException();
        }

        @Test
        void 성공_길이_경계값() {
            // given
            int maxTitleLength = 20;
            String title = "m".repeat(maxTitleLength);

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
                    .hasMessage("플레이스의 이름은 공백이 될 수 없습니다.");
        }

        @Test
        void 예외_플레이스_이름_최대_길이() {
            // given
            int maxLength = 20;
            String title = "m".repeat(maxLength + 1);

            // when & then
            assertThatThrownBy(() -> PlaceDetailFixture.createWithTitle(title))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("플레이스의 이름의 길이는 %d자를 초과할 수 없습니다.", maxLength);
        }
    }

    @Nested
    class validateDescription {

        @Test
        void 성공_null_허용() {
            // given
            String description = null;

            // when & then
            assertThatCode(() -> PlaceDetailFixture.createWithDescription(description))
                    .doesNotThrowAnyException();
        }

        @Test
        void 성공_길이_경계값() {
            // given
            int maxDescriptionLength = 100;
            String description = "m".repeat(maxDescriptionLength);

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
                    .hasMessage("플레이스의 설명은 공백이 될 수 없습니다.");
        }

        @Test
        void 예외_플레이스_설명_최대_길이() {
            // given
            int maxLength = 100;
            String description = "m".repeat(maxLength + 1);

            // when & then
            assertThatThrownBy(() -> PlaceDetailFixture.createWithDescription(description))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("플레이스 설명의 길이는 %d자를 초과할 수 없습니다.", maxLength);
        }
    }

    @Nested
    class validateLocation {

        @Test
        void 성공_null_허용() {
            // given
            String location = null;

            // when & then
            assertThatCode(() -> PlaceDetailFixture.createWithLocation(location))
                    .doesNotThrowAnyException();
        }

        @Test
        void 성공_길이_경계값() {
            // given
            int maxLocationLength = 100;
            String location = "m".repeat(maxLocationLength);

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
                    .hasMessage("플레이스의 위치는 공백이 될 수 없습니다.");
        }

        @Test
        void 예외_플레이스_위치_최대_길이() {
            // given
            int maxLength = 100;
            String location = "m".repeat(maxLength + 1);

            // when & then
            assertThatThrownBy(() -> PlaceDetailFixture.createWithLocation(location))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("플레이스 위치의 길이는 %d자를 초과할 수 없습니다.", maxLength);
        }
    }

    @Nested
    class validateHost {

        @Test
        void 성공_null_허용() {
            // given
            String host = null;

            // when & then
            assertThatCode(() -> PlaceDetailFixture.createWithHost(host))
                    .doesNotThrowAnyException();
        }

        @Test
        void 성공_길이_경계값() {
            // given
            int maxHostLength = 100;
            String host = "m".repeat(maxHostLength);

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
                    .hasMessage("플레이스의 호스트는 공백이 될 수 없습니다.");
        }

        @Test
        void 예외_플레이스_호스트_최대_길이() {
            // given
            int maxLength = 100;
            String host = "m".repeat(maxLength + 1);

            // when & then
            assertThatThrownBy(() -> PlaceDetailFixture.createWithHost(host))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("플레이스 호스트의 길이는 %d자를 초과할 수 없습니다.", maxLength);
        }
    }

    @Nested
    class validateTime {

        @Test
        void 성공_동시에_null_허용() {
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
                    .hasMessage("플레이스의 시작, 종료 날짜 둘 중 하나만 비어있을 수 없습니다. 둘다 비어있거나 둘다 정해져 있어야 합니다.");
        }
    }
}
