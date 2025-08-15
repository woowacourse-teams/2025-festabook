package com.daedan.festabook.place.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.daedan.festabook.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PlaceAnnouncementTest {

    private static final int MAX_TITLE_LENGTH = 20;
    private static final int MAX_CONTENT_LENGTH = 250;

    @Nested
    class validateAnnouncement {

        @Test
        void 성공() {
            // given
            String title = "플레이스 공지 제목";
            String content = "플레이스 공지 내용";

            // when & then
            assertThatCode(() -> PlaceAnnouncementFixture.create(title, content))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    class validateTitle {

        @ParameterizedTest
        @ValueSource(ints = {1, 5, 10, MAX_TITLE_LENGTH})
        void 성공_플레이스_공지_제목_경계값(int length) {
            // given
            String title = "m".repeat(length);

            // when & then
            assertThatCode(() -> PlaceAnnouncementFixture.createWithTitle(title))
                    .doesNotThrowAnyException();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = "    ")
        void 예외_플레이스_공지_제목_null_공백(String title) {
            // given & when & then
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
    }

    @Nested
    class validateContent {

        @ParameterizedTest
        @ValueSource(ints = {1, 100, 200, MAX_CONTENT_LENGTH})
        void 성공_플레이스_공지_내용_경계값(int length) {
            // given
            String content = "m".repeat(length);

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

    @Nested
    class updatePlaceAnnouncement {

        @Test
        void 성공() {
            // given
            PlaceAnnouncement placeAnnouncement = PlaceAnnouncementFixture.create();

            String title = "수정된 공지";
            String content = "수정된 내용";

            // when
            placeAnnouncement.updatePlaceAnnouncement(title, content);

            // then
            assertSoftly(s -> {
                s.assertThat(placeAnnouncement.getTitle()).isEqualTo(title);
                s.assertThat(placeAnnouncement.getContent()).isEqualTo(content);
            });
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 5, 10, MAX_TITLE_LENGTH})
        void 성공_플레이스_공지_제목_경계값(int length) {
            // given
            PlaceAnnouncement placeAnnouncement = PlaceAnnouncementFixture.create();

            String content = "수정된 내용";

            String title = "m".repeat(length);

            // when & then
            assertThatCode(() -> placeAnnouncement.updatePlaceAnnouncement(title, content))
                    .doesNotThrowAnyException();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = "    ")
        void 예외_플레이스_공지_제목_null_공백(String invalidTitle) {
            // given
            PlaceAnnouncement placeAnnouncement = PlaceAnnouncementFixture.create();

            String content = "수정된 내용";

            // when & then
            assertThatThrownBy(() -> placeAnnouncement.updatePlaceAnnouncement(invalidTitle, content))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("플레이스 공지의 제목은 비어있을 수 없습니다.");
        }
        
        @ParameterizedTest
        @ValueSource(ints = {1, 100, 200, MAX_CONTENT_LENGTH})
        void 성공_플레이스_공지_내용_경계값(int length) {
            // given
            PlaceAnnouncement placeAnnouncement = PlaceAnnouncementFixture.create();

            String title = "수정된 공지";

            String content = "m".repeat(length);

            // when & then
            assertThatCode(() -> placeAnnouncement.updatePlaceAnnouncement(title, content))
                    .doesNotThrowAnyException();
        }

        @Test
        void 예외_플레이스_공지_내용_최대_null() {
            // given
            PlaceAnnouncement placeAnnouncement = PlaceAnnouncementFixture.create();

            String title = "수정된 공지";

            String invalidContent = null;

            // when & then
            assertThatThrownBy(() -> placeAnnouncement.updatePlaceAnnouncement(title, invalidContent))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("플레이스 공지 내용은 null일 수 없습니다.", MAX_CONTENT_LENGTH);
        }

        @Test
        void 예외_플레이스_공지_내용_최대_길이_초과() {
            // given
            PlaceAnnouncement placeAnnouncement = PlaceAnnouncementFixture.create();

            String title = "수정된 공지";

            String invalidContent = "m".repeat(MAX_CONTENT_LENGTH + 1);

            // when & then
            assertThatThrownBy(() -> placeAnnouncement.updatePlaceAnnouncement(title, invalidContent))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("플레이스 공지 내용의 길이는 %d자를 초과할 수 없습니다.", MAX_CONTENT_LENGTH);
        }
    }
}
