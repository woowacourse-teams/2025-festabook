package com.daedan.festabook.event.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class EventStatusTest {

    private static final LocalDateTime FIXED_CLOCK_DATETIME = LocalDateTime.of(2025, 5, 5, 16, 0, 0);

    @Nested
    class determine {

        @ParameterizedTest(name = "날짜: {0}, 시작 시간: {1}, 종료 시간: {2}")
        @CsvSource({
                // 과거 날짜인 경우
                "2025-05-04, 00:00:00, 23:59:59",
                "2025-04-30, 10:00:00, 20:00:00",

                // 당일 날짜이지만 종료 시간(endTime)이 현재 시간보다 이전인 경우
                "2025-05-05, 10:00:00, 12:00:00",
                "2025-05-05, 15:00:00, 15:59:59",
        })
        void 성공_지난_일정_경우_COMPLETED(LocalDate date, LocalTime startTime, LocalTime endTime) {
            // given
            Clock clock = createFixedClock();

            // when
            EventStatus result = EventStatus.determine(clock, date, startTime, endTime);

            // then
            assertThat(result).isEqualTo(EventStatus.COMPLETED);
        }

        @ParameterizedTest(name = "시작 시간: {0}, 종료 시간: {1}")
        @CsvSource({
                // 현재 시간이 시작-종료 시간 사이인 경우
                "15:00:00, 17:00:00",
                "00:00:00, 23:59:59",
                "16:00:00, 18:00:00", // 시작 시간이 현재와 같은 경우
                "14:00:00, 16:00:00", // 종료 시간이 현재와 같은 경우
        })
        void 성공_진행중인_일정_경우_ONGOING(LocalTime startTime, LocalTime endTime) {
            // given
            Clock clock = createFixedClock();

            // when
            EventStatus result = EventStatus.determine(clock, LocalDate.now(clock), startTime, endTime);

            // then
            assertThat(result).isEqualTo(EventStatus.ONGOING);
        }

        @ParameterizedTest(name = "날짜: {0}, 시작 시간: {1}, 종료 시간: {2}")
        @CsvSource({
                // 미래 날짜인 경우
                "2025-05-06, 00:00:00, 23:59:59",
                "2025-06-01, 10:00:00, 11:00:00",

                // 당일 날짜이며 이벤트 시작 시간(startTime)이 현재 시간보다 이후인 경우
                "2025-05-05, 17:00:00, 18:00:00",
                "2025-05-05, 16:00:01, 16:30:00",
        })
        void 성공_예정된_일정인_경우_UPCOMING(LocalDate date, LocalTime startTime, LocalTime endTime) {
            // given
            Clock clock = createFixedClock();

            // when
            EventStatus result = EventStatus.determine(clock, date, startTime, endTime);

            // then
            assertThat(result).isEqualTo(EventStatus.UPCOMING);
        }
    }

    private Clock createFixedClock() {
        ZoneId seoulZone = ZoneId.of("Asia/Seoul");
        return Clock.fixed(
                FIXED_CLOCK_DATETIME.atZone(seoulZone).toInstant(),
                seoulZone
        );
    }
}
