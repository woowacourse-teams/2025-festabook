package com.daedan.festabook.event.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.global.exception.BusinessException;
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
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class EventTest {

    private static final int MAX_TITLE_LENGTH = 30;
    private static final int MAX_LOCATION_LENGTH = 100;

    @Nested
    class validateEventDate {

        @Test
        void 성공_존재하는_일정_날짜() {
            // given
            EventDate eventDate = EventDateFixture.create(LocalDate.of(2025, 5, 5));

            // when & then
            assertThatCode(() -> EventFixture.create(eventDate))
                    .doesNotThrowAnyException();
        }

        @Test
        void 예외_일정_날짜_null() {
            // given
            EventDate eventDate = null;

            // when & then
            assertThatThrownBy(() -> EventFixture.create(eventDate))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("일정 날짜는 null일 수 없습니다.");
        }
    }

    @Nested
    class validateTimes {

        @Test
        void 성공_존재하는_시작_시간_종료_시간() {
            // given
            LocalTime startTime = LocalTime.of(10, 0);
            LocalTime endTime = LocalTime.of(12, 0);
            EventDate eventDate = EventDateFixture.create(LocalDate.of(2025, 5, 5));

            // when & then
            assertThatCode(() -> EventFixture.create(startTime, endTime, eventDate))
                    .doesNotThrowAnyException();
        }

        @Test
        void 예외_시작_시간_null() {
            // given
            LocalTime startTime = null;
            LocalTime endTime = LocalTime.of(12, 0);
            EventDate eventDate = EventDateFixture.create(LocalDate.of(2025, 5, 5));

            // when & then
            assertThatThrownBy(() -> EventFixture.create(startTime, endTime, eventDate))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("시작 시간과 종료 시간은 null일 수 없습니다.");
        }

        @Test
        void 예외_종료_시간_null() {
            // given
            LocalTime startTime = LocalTime.of(10, 0);
            LocalTime endTime = null;
            EventDate eventDate = EventDateFixture.create(LocalDate.of(2025, 5, 5));

            // when & then
            assertThatThrownBy(() -> EventFixture.create(startTime, endTime, eventDate))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("시작 시간과 종료 시간은 null일 수 없습니다.");
        }
    }

    @Nested
    class validateTitle {

        @ParameterizedTest
        @ValueSource(ints = {1, 5, 10, MAX_TITLE_LENGTH})
        void 성공_일정_이름_길이_경계값(int length) {
            // given
            String title = "m".repeat(length);

            // when & then
            assertThatCode(() -> EventFixture.createWithTitle(title))
                    .doesNotThrowAnyException();
        }

        @Test
        void 예외_일정_이름_null() {
            // given
            String title = null;

            // when & then
            assertThatThrownBy(() -> EventFixture.createWithTitle(title))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("일정 제목은 공백이거나 null일 수 없습니다.");
        }

        @Test
        void 예외_일정_이름_공백() {
            // given
            String title = " ";

            // when & then
            assertThatThrownBy(() -> EventFixture.createWithTitle(title))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("일정 제목은 공백이거나 null일 수 없습니다.");
        }

        @Test
        void 예외_일정_이름_최대_길이_초과() {
            // given
            String title = "m".repeat(MAX_TITLE_LENGTH + 1);

            // when & then
            assertThatThrownBy(() -> EventFixture.createWithTitle(title))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("일정 제목의 길이는 %d자를 초과할 수 없습니다.", MAX_TITLE_LENGTH);
        }
    }

    @Nested
    class validateLocation {

        @ParameterizedTest
        @ValueSource(ints = {1, 5, 10, MAX_LOCATION_LENGTH})
        void 성공_일정_위치_길이_경계값(int length) {
            // given
            String location = "m".repeat(length);

            // when & then
            assertThatCode(() -> EventFixture.createWithLocation(location))
                    .doesNotThrowAnyException();
        }

        @Test
        void 예외_일정_위치_null() {
            // given
            String location = null;

            // when & then
            assertThatThrownBy(() -> EventFixture.createWithLocation(location))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("일정 위치는 공백이거나 null일 수 없습니다.");
        }

        @Test
        void 예외_일정_위치_공백() {
            // given
            String location = " ";

            // when & then
            assertThatThrownBy(() -> EventFixture.createWithLocation(location))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("일정 위치는 공백이거나 null일 수 없습니다.");
        }

        @Test
        void 예외_일정_위치_최대_길이_초과() {
            // given
            String location = "m".repeat(MAX_LOCATION_LENGTH + 1);

            // when & then
            assertThatThrownBy(() -> EventFixture.createWithLocation(location))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("일정 위치의 길이는 %d자를 초과할 수 없습니다.", MAX_LOCATION_LENGTH);
        }
    }

    @Nested
    class updateEvent {

        @Test
        void 성공_이벤트_업데이트() {
            // given
            EventDate eventDate = EventDateFixture.create(LocalDate.of(2025, 5, 5));
            Event originalEvent = EventFixture.create(
                    eventDate,
                    LocalTime.of(1, 0),
                    LocalTime.of(2, 0),
                    "Original Title",
                    "Original Location"
            );

            Event newEvent = EventFixture.create(
                    eventDate,
                    LocalTime.of(3, 0),
                    LocalTime.of(4, 0),
                    "Updated Title",
                    "Updated Location"
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

    @Nested
    class isFestivalIdEqualTo {

        @Test
        void 같은_축제의_id이면_true() {
            // given
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            EventDate eventDate = EventDateFixture.create(festival);
            Event event = EventFixture.create(eventDate);

            // when
            boolean result = event.isFestivalIdEqualTo(festivalId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        void 다른_축제의_id이면_false() {
            // given
            Long festivalId = 1L;
            Long otherFestivalId = 999L;
            Festival festival = FestivalFixture.create(festivalId);
            EventDate eventDate = EventDateFixture.create(festival);
            Event event = EventFixture.create(eventDate);

            // when
            boolean result = event.isFestivalIdEqualTo(otherFestivalId);

            // then
            assertThat(result).isFalse();
        }
    }
}
