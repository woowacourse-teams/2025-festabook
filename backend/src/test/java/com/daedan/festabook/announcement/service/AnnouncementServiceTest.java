package com.daedan.festabook.announcement.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.daedan.festabook.announcement.domain.Announcement;
import com.daedan.festabook.announcement.dto.AnnouncementRequest;
import com.daedan.festabook.announcement.dto.AnnouncementResponse;
import com.daedan.festabook.announcement.infrastructure.AnnouncementJpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AnnouncementServiceTest {

    private final AnnouncementJpaRepository announcementJpaRepository;
    private final AnnouncementService announcementService;

    public AnnouncementServiceTest() {
        this.announcementJpaRepository = mock(AnnouncementJpaRepository.class);
        this.announcementService = new AnnouncementService(announcementJpaRepository);
    }

    @Nested
    class 공지_생성 {

        @Test
        void 공지를_정상적으로_생성한다() {
            // given
            AnnouncementRequest request = new AnnouncementRequest(
                    "곧 재학생 스탬프 배부 시간입니다.",
                    "잠시후, ~~~에서 재학생존 스탬프 배부를 시작하겠습니다."
            );

            LocalDateTime createAt = LocalDateTime.of(2025, 7, 9, 16, 25, 0);
            Announcement notSaved = Announcement.builder()
                    .title(request.title())
                    .createAt(createAt)
                    .content(request.content())
                    .build();
            Announcement saved = new Announcement(1L, request.title(), createAt, request.content());
            given(announcementJpaRepository.save(notSaved))
                    .willReturn(saved);

            AnnouncementResponse expected = new AnnouncementResponse(
                    1L,
                    request.title(),
                    createAt,
                    request.content()
            );

            // when
            AnnouncementResponse actual = announcementService.createAnnouncement(request);

            // then
            assertThat(actual).isEqualTo(expected);
        }
    }

    @Nested
    class 공지_전체_조회 {

        @Test
        void 공지를_정상적으로_전체_조회한다() {
            // given
            String title = "공지 제목";
            LocalDateTime createAt = LocalDateTime.of(2025, 7, 9, 16, 25, 0);
            String content = "공지 내용";

            Announcement announcement1 = new Announcement(1L, title, createAt, content);
            Announcement announcement2 = new Announcement(2L, title, createAt, content);
            Announcement announcement3 = new Announcement(3L, title, createAt, content);
            given(announcementJpaRepository.findAll())
                    .willReturn(List.of(announcement1, announcement2, announcement3));

            // when
            List<AnnouncementResponse> actual = announcementService.findAllAnnouncement();

            // then
            assertSoftly(s -> {
                s.assertThat(actual).hasSize(3);
                s.assertThat(actual.get(0).id()).isEqualTo(1L);
                s.assertThat(actual.get(1).id()).isEqualTo(2L);
                s.assertThat(actual.get(2).id()).isEqualTo(3L);
            });
        }
    }
}
