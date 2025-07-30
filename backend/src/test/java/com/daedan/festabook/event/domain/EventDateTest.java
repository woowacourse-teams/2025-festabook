package com.daedan.festabook.event.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class EventDateTest {

    @Nested
    class updateDate {

        @Test
        void 성공() {
            // given
            LocalDate initialDate = LocalDate.of(2025, 5, 1);
            EventDate eventDate = EventDateFixture.create(initialDate);
            LocalDate newDate = LocalDate.of(2025, 6, 1);

            // when
            eventDate.updateDate(newDate);

            // then
            assertThat(eventDate.getDate()).isEqualTo(newDate);
        }
    }

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
            EventDate baseEvent = EventDateFixture.create(baseDate);
            EventDate compareEvent = EventDateFixture.create(compareDate);

            // when
            int result = baseEvent.compareTo(compareEvent);

            // then
            assertThat(result).isEqualTo(expected);
        }
    }
}
