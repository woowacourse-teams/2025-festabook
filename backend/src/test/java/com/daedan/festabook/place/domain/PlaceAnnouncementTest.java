package com.daedan.festabook.place.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.daedan.festabook.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PlaceAnnouncementTest {

    @Nested
    class validate {

        @Test
        void 성공() {
            // given
            Place place = PlaceFixture.create();

            // when & then
            assertThatCode(() -> {
                new PlaceAnnouncement(
                        place,
                        "플레이스 공지 제목",
                        "플레이스 공지 내용"
                );
            }).doesNotThrowAnyException();
        }

        @Test
        void 성공_길이_경계값() {
            // given
            Place place = PlaceFixture.create();

            int maxTitleLength = 20;
            int maxContentLength = 250;
            String title = "m".repeat(maxTitleLength);
            String content = "m".repeat(maxContentLength);

            // when & then
            assertThatCode(() -> {
                new PlaceAnnouncement(
                        place,
                        title,
                        content
                );
            }).doesNotThrowAnyException();
        }

        @ParameterizedTest
        @CsvSource({
                ",공지내용",
                "공지제목,"
        })
        void 예외_null이_존재하면_예외가_발생(
                String title,
                String content
        ) {
            // given
            Place place = PlaceFixture.create();

            // when & then
            assertThatThrownBy(() -> {
                new PlaceAnnouncement(place, title, content);
            })
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("null");
        }

        @Test
        void 예외_플레이스_공지_제목_최대_길이() {
            // given
            Place place = PlaceFixture.create();

            int maxLength = 20;
            String title = "m".repeat(maxLength + 1);

            // when & then
            assertThatThrownBy(() -> {
                new PlaceAnnouncement(place, title, "공지내용");
            })
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("플레이스 공지 제목의 길이는 %d자를 초과할 수 없습니다.", maxLength);
        }

        @Test
        void 예외_플레이스_공지_내용_최대_길이() {
            // given
            Place place = PlaceFixture.create();

            int maxLength = 250;
            String content = "m".repeat(maxLength + 1);

            // when & then
            assertThatThrownBy(() -> {
                new PlaceAnnouncement(place, "공지제목", content);
            })
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("플레이스 공지 내용의 길이는 %d자를 초과할 수 없습니다.", maxLength);
        }
    }
}
