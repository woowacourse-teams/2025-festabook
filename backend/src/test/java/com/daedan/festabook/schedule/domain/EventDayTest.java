package com.daedan.festabook.schedule.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.ArrayList;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class EventDayTest {

    @Nested
    class compareTo {

        @ParameterizedTest(name = "기준일: {0}, 비교일: {1}, 기대값: {2}")
        @CsvSource({
                "2025-05-04, 2025-05-03, 1",
                "2025-05-04, 2025-05-04, 0",
                "2025-05-04, 2025-05-05, -1"
        })
        void 성공(LocalDate baseDate, LocalDate compareDate, int expected) {
            // given
            EventDay baseEvent = createTestEventDay(baseDate);
            EventDay compareEvent = createTestEventDay(compareDate);

            // when
            int result = baseEvent.compareTo(compareEvent);

            // then
            assertThat(result)
                    .isEqualTo(expected);
        }
    }

    private EventDay createTestEventDay(LocalDate date) {
        return new EventDay(
                date,
                new ArrayList<>()
        );
    }
}
