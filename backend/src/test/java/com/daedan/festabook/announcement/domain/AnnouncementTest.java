package com.daedan.festabook.announcement.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AnnouncementTest {

    private static final int MAX_TITLE_LENGTH = 50;
    private static final int MAX_CONTENT_LENGTH = 3000;

    @Nested
    class validateAnnouncement {

        @Test
        void 성공() {
            // given
            String title = "플레이스 공지 제목";
            String content = "플레이스 공지 내용";
            boolean isPinned = false;
            Festival festival = FestivalFixture.create();

            // when & then
            assertThatCode(() -> AnnouncementFixture.create(title, content, isPinned, festival))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    class validateTitle {

        @Test
        void 성공() {
            // given
            String title = "공지사항 제목";

            // when & then
            assertThatCode(() -> AnnouncementFixture.createWithTitle(title))
                    .doesNotThrowAnyException();
        }

        @ParameterizedTest(name = "제목: {0}")
        @NullAndEmptySource
        @ValueSource(strings = "    ")
        void 예외_제목_null_혹은_빈문자열(String invalidTitle) {
            assertThatThrownBy(() -> AnnouncementFixture.createWithTitle(invalidTitle))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("공지사항 제목은 비어 있을 수 없습니다.");
        }

        @ParameterizedTest(name = "제목 길이: {0}")
        @ValueSource(ints = {25, MAX_TITLE_LENGTH})
        void 성공_제목_길이_이하(int titleLength) {
            // given
            String title = "a".repeat(titleLength);

            // when & then
            assertThatCode(() -> AnnouncementFixture.createWithTitle(title))
                    .doesNotThrowAnyException();
        }

        @ParameterizedTest(name = "제목 길이: {0}")
        @ValueSource(ints = {MAX_TITLE_LENGTH + 1, 100})
        void 예외_제목_길이_초과(int invalidTitleLength) {
            // given
            String title = "a".repeat(invalidTitleLength);

            // when & then
            assertThatThrownBy(() -> AnnouncementFixture.createWithTitle(title))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(String.format("공지사항 제목은 %s자를 초과할 수 없습니다.", MAX_TITLE_LENGTH));
        }
    }

    @Nested
    class validateContent {

        @Test
        void 성공() {
            // given
            String content = "공지사항 내용";

            // when & then
            assertThatCode(() -> AnnouncementFixture.createWithContent(content))
                    .doesNotThrowAnyException();
        }

        @ParameterizedTest(name = "내용: {0}")
        @NullAndEmptySource
        @ValueSource(strings = "    ")
        void 예외_내용_null_혹은_빈문자열(String invalidContent) {
            assertThatThrownBy(() -> AnnouncementFixture.createWithContent(invalidContent))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("공지사항 본문은 비어 있을 수 없습니다.");
        }

        @ParameterizedTest(name = "내용 길이: {0}")
        @ValueSource(ints = {500, MAX_CONTENT_LENGTH})
        void 성공_내용_길이_이하(int contentLength) {
            // given
            String content = "a".repeat(contentLength);

            // when & then
            assertThatCode(() -> AnnouncementFixture.createWithContent(content))
                    .doesNotThrowAnyException();
        }

        @ParameterizedTest(name = "내용 길이: {0}")
        @ValueSource(ints = {MAX_CONTENT_LENGTH + 1, MAX_CONTENT_LENGTH + 1000})
        void 예외_내용_길이_초과(int invalidContentLength) {
            // given
            String content = "a".repeat(invalidContentLength);

            // when & then
            assertThatThrownBy(() -> AnnouncementFixture.createWithContent(content))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(String.format("공지사항 본문은 %s자를 초과할 수 없습니다.", MAX_CONTENT_LENGTH));
        }
    }

    @Nested
    class validateFestival {

        @Test
        void 성공() {
            // given
            Festival festival = FestivalFixture.create();

            // when & then
            assertThatCode(() -> AnnouncementFixture.create(festival))
                    .doesNotThrowAnyException();
        }

        @Test
        void 예외_축제_null() {
            // given
            Festival festival = null;

            // when & then
            assertThatThrownBy(() -> AnnouncementFixture.create(festival))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("축제는 null일 수 없습니다.");
        }
    }

    @Nested
    class isFestivalIdEqualTo {

        @Test
        void 같은_축제의_id이면_true() {
            // given
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            Announcement announcement = AnnouncementFixture.create(festival);

            // when
            boolean result = announcement.isFestivalIdEqualTo(festivalId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        void 다른_축제의_id이면_false() {
            // given
            Long festivalId = 1L;
            Long otherFestivalId = 999L;
            Festival festival = FestivalFixture.create(festivalId);
            Announcement announcement = AnnouncementFixture.create(festival);

            // when
            boolean result = announcement.isFestivalIdEqualTo(otherFestivalId);

            // then
            assertThat(result).isFalse();
        }
    }
}
