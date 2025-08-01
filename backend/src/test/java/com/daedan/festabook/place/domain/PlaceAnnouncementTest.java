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

    private static final int MAX_TITLE_LENGTH = 20;
    private static final int MAX_CONTENT_LENGTH = 250;

    @Nested
    class validateAnnouncement {

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
        void 성공_플레이스_공지_제목_경계값() {
            // given
            String title = "m".repeat(MAX_TITLE_LENGTH);

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
                    .hasMessage("플레이스 공지의 제목은 비어있을 수 없습니다.");
        }

        @Test
        void 예외_플레이스_공지_제목_최대_길이_초과() {
            // given
            String title = "m".repeat(MAX_TITLE_LENGTH + 1);

            // when & then
            assertThatThrownBy(() -> PlaceAnnouncementFixture.createWithTitle(title))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("플레이스 공지 제목의 길이는 %d자를 초과할 수 없습니다.", MAX_TITLE_LENGTH);
        }

        @Test
        void 예외_플레이스_공지_제목_공백() {
            // given
            String title = " ";

            // when & then
            assertThatThrownBy(() -> PlaceAnnouncementFixture.createWithTitle(title))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("플레이스 공지의 제목은 비어있을 수 없습니다.");
        }
    }

    @Nested
    class validateContent {

        @Test
        void 성공_플레이스_공지_내용_경계값() {
            // given
            String content = "m".repeat(MAX_CONTENT_LENGTH);

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
                    .hasMessage("플레이스 공지 내용은 null일 수 없습니다.");
        }

        @Test
        void 예외_플레이스_공지_내용_최대_길이_초과() {
            // given
            String content = "m".repeat(MAX_CONTENT_LENGTH + 1);

            // when & then
            assertThatThrownBy(() -> PlaceAnnouncementFixture.createWithContent(content))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("플레이스 공지 내용의 길이는 %d자를 초과할 수 없습니다.", MAX_CONTENT_LENGTH);
        }
    }
}
