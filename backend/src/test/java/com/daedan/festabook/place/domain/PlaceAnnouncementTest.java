package com.daedan.festabook.place.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.daedan.festabook.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PlaceAnnouncementTest {

    @Nested
    class validate {

        @Test
        void 성공() {
            // given & when & then
            assertThatCode(() ->
                    PlaceAnnouncementFixture.create("플레이스 공지 제목", "플레이스 공지 내용")
            )
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    class validateTitle {

        @Test
        void 예외_플레이스_공지_제목_경계값() {
            // given
            int maxLength = 20;
            String title = "m".repeat(maxLength);

            // when & then
            assertThatCode(() -> PlaceAnnouncementFixture.createWithTitle(title))
                    .doesNotThrowAnyException();
        }

        @Test
        void 예외_플레이스_공지_제목_null() {
            // given
            String title = null;

            // when & then
            assertThatThrownBy(() -> PlaceAnnouncementFixture.createWithTitle(title))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("플레이스 공지의 제목은 공백이거나 null이 될 수 없습니다.");
        }

        @Test
        void 예외_플레이스_공지_제목_최대_길이() {
            // given
            int maxLength = 20;
            String title = "m".repeat(maxLength + 1);

            // when & then
            assertThatThrownBy(() -> PlaceAnnouncementFixture.createWithTitle(title))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("플레이스 공지 제목의 길이는 %d자를 초과할 수 없습니다.", maxLength);
        }

        @Test
        void 예외_플레이스_공지_제목_공백() {
            // given
            String title = " ";

            // when & then
            assertThatThrownBy(() -> PlaceAnnouncementFixture.createWithTitle(title))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("플레이스 공지의 제목은 공백이거나 null이 될 수 없습니다.");
        }
    }

    @Nested
    class validateContent {

        @Test
        void 예외_플레이스_공지_내용_경계값() {
            // given
            int maxLength = 250;
            String content = "m".repeat(maxLength);

            // when & then
            assertThatCode(() -> PlaceAnnouncementFixture.createWithContent(content))
                    .doesNotThrowAnyException();
        }

        @Test
        void 예외_플레이스_공지_내용_최대_null() {
            // given
            String content = null;

            // when & then
            assertThatThrownBy(() -> PlaceAnnouncementFixture.createWithContent(content))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("플레이스 공지 내용은 null이 될 수 없습니다.");
        }

        @Test
        void 예외_플레이스_공지_내용_최대_길이() {
            // given
            int maxLength = 250;
            String content = "m".repeat(maxLength + 1);

            // when & then
            assertThatThrownBy(() -> PlaceAnnouncementFixture.createWithContent(content))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("플레이스 공지 내용의 길이는 %d자를 초과할 수 없습니다.", maxLength);
        }
    }
}
