package com.daedan.festabook.event.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class EventTest {

    @Nested
    class updateEvent {

        @Test
        void 성공_이벤트_업데이트() {
            // given
            EventDate eventDate = EventDateFixture.create(LocalDate.of(2025, 5, 5));
            Event originalEvent = EventFixture.create(
                    LocalTime.of(1, 0),
                    LocalTime.of(2, 0),
                    "Original Title",
                    "Original Location",
                    eventDate
            );

            Event newEvent = EventFixture.create(
                    LocalTime.of(3, 0),
                    LocalTime.of(4, 0),
                    "Updated Title",
                    "Updated Location",
                    eventDate
            );

            // when
            originalEvent.updateEvent(newEvent);

            // then
            assertSoftly(s -> {
                s.assertThat(originalEvent.getStartTime()).isEqualTo(newEvent.getStartTime());
                s.assertThat(originalEvent.getEndTime()).isEqualTo(newEvent.getEndTime());
                s.assertThat(originalEvent.getTitle()).isEqualTo(newEvent.getTitle());
                s.assertThat(originalEvent.getLocation()).isEqualTo(newEvent.getLocation());
            });
        }
    }

    @Nested
    class determineStatus {

        @ParameterizedTest(name = "날짜: {0}, 시작 시간: {1}, 종료 시간: {2}, 결과: {3}")
        @CsvSource({
                "2025-05-04, 10:00, 12:00, COMPLETED",   // 종료
                "2025-05-05, 14:00, 17:00, ONGOING",     // 진행중
                "2025-05-05, 17:00, 18:00, UPCOMING",    // 예정
        })
        void 이벤트_상태_판별_파라미터_테스트(LocalDate date, LocalTime startTime, LocalTime endTime, EventStatus expected) {
            // given
            ZoneId korea = ZoneId.of("Asia/Seoul");
            Clock clock = Clock.fixed(
                    LocalDateTime.of(2025, 5, 5, 16, 0).atZone(korea).toInstant(),
                    korea
            );

            Event event = EventFixture.create(
                    startTime,
                    endTime,
                    EventDateFixture.create(date)
            );

            // when
            EventStatus result = event.determineStatus(clock);

            // then
            assertThat(result).isEqualTo(expected);
        }
    }
}
