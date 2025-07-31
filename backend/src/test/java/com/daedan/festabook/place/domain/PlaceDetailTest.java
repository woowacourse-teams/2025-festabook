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
            // given
            Place place = PlaceFixture.create();

            // when & then
            assertThatCode(() -> {
                new PlaceDetail(
                        place,
                        "플레이스 이름",
                        "플레이스 설명",
                        "플레이스 위치",
                        "플레이스 호스트",
                        LocalTime.of(12, 30),
                        LocalTime.of(13, 0)
                );
            }).doesNotThrowAnyException();
        }

        @Test
        void 성공_길이_경계값() {
            // given
            Place place = PlaceFixture.create();

            int maxTitleLength = 20;
            int maxDescriptionLength = 100;
            int maxLocationLength = 100;
            int maxHostLength = 100;

            String title = "m".repeat(maxTitleLength);
            String description = "m".repeat(maxDescriptionLength);
            String location = "m".repeat(maxLocationLength);
            String host = "m".repeat(maxHostLength);

            // when & then
            assertThatCode(() -> {
                new PlaceDetail(
                        place,
                        title,
                        description,
                        location,
                        host,
                        LocalTime.of(12, 30),
                        LocalTime.of(13, 0)
                );
            }).doesNotThrowAnyException();
        }

        @Test
        void 성공_null은_허용() {
            // given
            Place place = PlaceFixture.create();

            // when & then
            assertThatCode(() -> {
                new PlaceDetail(
                        place,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );
            }).doesNotThrowAnyException();
        }

        @Test
        void 플레이스_시작시간_종료시간이_모두_null은_가능() {
            // given
            Place place = PlaceFixture.create();

            LocalTime startTime = null;
            LocalTime endTime = null;

            // when & then
            assertThatCode(() -> {
                new PlaceDetail(
                        place,
                        "플레이스 이름",
                        "플레이스 설명",
                        "플레이스 위치",
                        "플레이스 호스트",
                        startTime,
                        endTime
                );
            }).doesNotThrowAnyException();
        }

        @ParameterizedTest
        @CsvSource({
                "' ',설명,위치,호스트",
                "이름,' ',위치,호스트",
                "이름,설명,' ',호스트",
                "이름,설명,위치,' '"
        })
        void 예외_공백이_존재하면_예외가_발생(
                String title,
                String description,
                String location,
                String host
        ) {
            // given
            Place place = PlaceFixture.create();

            // when & then
            assertThatThrownBy(() -> {
                new PlaceDetail(
                        place,
                        title,
                        description,
                        location,
                        host,
                        LocalTime.of(12, 30),
                        LocalTime.of(13, 0)
                );
            }).isInstanceOf(BusinessException.class);
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
            // given
            Place place = PlaceFixture.create();

            // when & then
            assertThatThrownBy(() -> {
                new PlaceDetail(
                        place,
                        "플레이스 이름",
                        "플레이스 설명",
                        "플레이스 위치",
                        "플레이스 호스트",
                        startTime,
                        endTime
                );
            })
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("플레이스의 시작, 종료 날짜를 모두 정해야합니다.");
        }

        @Test
        void 예외_플레이스_이름_최대_길이() {
            // given
            Place place = PlaceFixture.create();

            int maxLength = 20;
            String title = "m".repeat(maxLength + 1);

            // when & then
            assertThatThrownBy(() -> {
                new PlaceDetail(
                        place,
                        title,
                        "플레이스 설명",
                        "플레이스 위치",
                        "플레이스 호스트",
                        LocalTime.of(12, 30),
                        LocalTime.of(13, 0)
                );
            })
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("플레이스의 이름의 길이는 %d자를 초과할 수 없습니다.", maxLength);
        }

        @Test
        void 예외_플레이스_설명_최대_길이() {
            // given
            Place place = PlaceFixture.create();

            int maxLength = 100;
            String description = "m".repeat(maxLength + 1);

            // when & then
            assertThatThrownBy(() -> {
                new PlaceDetail(
                        place,
                        "플레이스 이름",
                        description,
                        "플레이스 위치",
                        "플레이스 호스트",
                        LocalTime.of(12, 30),
                        LocalTime.of(13, 0)
                );
            })
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("플레이스 설명의 길이는 %d자를 초과할 수 없습니다.", maxLength);
        }

        @Test
        void 예외_플레이스_위치_최대_길이() {
            // given
            Place place = PlaceFixture.create();

            int maxLength = 100;
            String location = "m".repeat(maxLength + 1);

            // when & then
            assertThatThrownBy(() -> {
                new PlaceDetail(
                        place,
                        "플레이스 이름",
                        "플레이스 설명",
                        location,
                        "플레이스 호스트",
                        LocalTime.of(12, 30),
                        LocalTime.of(13, 0)
                );
            })
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("플레이스 위치의 길이는 %d자를 초과할 수 없습니다.", maxLength);
        }

        @Test
        void 예외_플레이스_호스트_최대_길이() {
            // given
            Place place = PlaceFixture.create();

            int maxLength = 100;
            String host = "m".repeat(maxLength + 1);

            // when & then
            assertThatThrownBy(() -> {
                new PlaceDetail(
                        place,
                        "플레이스 이름",
                        "플레이스 설명",
                        "플레이스 위치",
                        host,
                        LocalTime.of(12, 30),
                        LocalTime.of(13, 0)
                );
            })
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("플레이스 호스트의 길이는 %d자를 초과할 수 없습니다.", maxLength);
        }
    }
}
