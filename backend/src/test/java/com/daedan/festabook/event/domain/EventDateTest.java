package com.daedan.festabook.event.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import java.time.LocalDate;
import org.assertj.core.api.AssertionsForClassTypes;
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

    @Nested
    class isFestivalIdEqualTo {

        @Test
        void 같은_축제의_id이면_true() {
            // given
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            EventDate eventDate = EventDateFixture.create(festival);

            // when
            boolean result = eventDate.isFestivalIdEqualTo(festivalId);

            // then
            AssertionsForClassTypes.assertThat(result).isTrue();
        }

        @Test
        void 다른_축제의_id이면_false() {
            // given
            Long festivalId = 1L;
            Long otherFestivalId = 999L;
            Festival festival = FestivalFixture.create(festivalId);
            EventDate eventDate = EventDateFixture.create(festival);

            // when
            boolean result = eventDate.isFestivalIdEqualTo(otherFestivalId);

            // then
            AssertionsForClassTypes.assertThat(result).isFalse();
        }
    }
}
