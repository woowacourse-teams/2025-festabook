package com.daedan.festabook.announcement.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.daedan.festabook.announcement.domain.Announcement;
import com.daedan.festabook.announcement.domain.DateTimeGenerator;
import com.daedan.festabook.announcement.dto.AnnouncementRequest;
import com.daedan.festabook.announcement.dto.AnnouncementResponse;
import com.daedan.festabook.announcement.infrastructure.AnnouncementJpaRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AnnouncementServiceTest {

    private final AnnouncementJpaRepository announcementJpaRepository;
    private final DateTimeGenerator dateTimeGenerator;
    private final AnnouncementService announcementService;

    public AnnouncementServiceTest() {
        this.announcementJpaRepository = mock(AnnouncementJpaRepository.class);
        this.dateTimeGenerator = mock(DateTimeGenerator.class);
        this.announcementService = new AnnouncementService(announcementJpaRepository, dateTimeGenerator);
    }

    @Nested
    class 공지_생성 {

        @Test
        void 공지_정상_생성() {
            // given
            AnnouncementRequest request = new AnnouncementRequest(
                    "곧 재학생 스탬프 배부 시간입니다.",
                    "잠시후, ~~~에서 재학생존 스탬프 배부를 시작하겠습니다."
            );

            LocalDate date = LocalDate.of(2025, 7, 9);
            LocalTime time = LocalTime.of(16, 25);
            Announcement notSaved = Announcement.builder()
                    .title(request.title())
                    .date(date)
                    .time(time)
                    .content(request.content())
                    .build();
            Announcement saved = new Announcement(1L, request.title(), date, time, request.content());
            given(announcementJpaRepository.save(notSaved))
                    .willReturn(saved);
            given(dateTimeGenerator.generateDate())
                    .willReturn(date);
            given(dateTimeGenerator.generateTime())
                    .willReturn(time);

            AnnouncementResponse expected = new AnnouncementResponse(
                    request.title(),
                    date,
                    time,
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
        void 공지_정상_전체_조회() {
            // given
            String title = "공지 제목";
            LocalDate date = LocalDate.of(2025, 7, 9);
            LocalTime time = LocalTime.of(16, 25);
            String content = "공지 내용";

            Announcement announcement1 = new Announcement(1L, title, date, time, content);
            Announcement announcement2 = new Announcement(2L, title, date, time, content);
            Announcement announcement3 = new Announcement(3L, title, date, time, content);
            given(announcementJpaRepository.findAll())
                    .willReturn(List.of(announcement1, announcement2, announcement3));

            // when
            List<AnnouncementResponse> actual = announcementService.findAllAnnouncement();

            // then
            assertThat(actual).hasSize(3);
        }
    }
}
