package com.daedan.festabook.schedule.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class EventTest {

    @ParameterizedTest(name = "날짜: {0}, 시작 시간: {1}, 종료 시간: {2}, 결과: {3}")
    @CsvSource({
            "2025-05-04, 10:00, 12:00, COMPLETED",   // 과거 날짜
            "2025-05-05, 10:00, 15:00, COMPLETED",   // 오늘, 종료 시각 이전
            "2025-05-05, 14:00, 17:00, ONGOING",     // 오늘, 진행 중
            "2025-05-05, 17:00, 18:00, UPCOMING",    // 오늘, 시작 전
            "2025-05-06, 10:00, 12:00, UPCOMING"     // 미래 날짜
    })
    void 이벤트_상태_판별_파라미터_테스트(LocalDate date, LocalTime startTime, LocalTime endTime, EventStatus expected) {
        // given
        Clock clock = Clock.fixed(
                LocalDateTime.of(2025, 5, 5, 16, 0).toInstant(ZoneOffset.ofHours(9)),
                ZoneId.of("Asia/Seoul")
        );

        Event event = EventFixture.create(
                startTime,
                endTime,
                EventDateFixture.create(date)
        );

        // when
        EventStatus result = event.getStatus(clock);

        // then
        assertThat(result).isEqualTo(expected);
    }
}
