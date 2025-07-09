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
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("공지 생성")
    class Create {

        @DisplayName("공지 정상 생성 테스트")
        @Test
        void create1() {
            // given
            final AnnouncementRequest request = new AnnouncementRequest(
                    "곧 재학생 스탬프 배부 시간입니다.",
                    "잠시후, ~~~에서 재학생존 스탬프 배부를 시작하겠습니다."
            );

            final LocalDate date = LocalDate.of(2025, 7, 9);
            final LocalTime time = LocalTime.of(16, 25);
            final Announcement notSaved = Announcement.builder()
                    .title(request.title())
                    .date(date)
                    .time(time)
                    .content(request.content())
                    .build();
            final Announcement saved = new Announcement(1L, request.title(), date, time, request.content());
            given(announcementJpaRepository.save(notSaved))
                    .willReturn(saved);
            given(dateTimeGenerator.generateDate())
                    .willReturn(date);
            given(dateTimeGenerator.generateTime())
                    .willReturn(time);

            final AnnouncementResponse expected = new AnnouncementResponse(
                    request.title(),
                    date,
                    time,
                    request.content()
            );

            // when
            final AnnouncementResponse actual = announcementService.createAnnouncement(request);

            // then
            assertThat(actual).isEqualTo(expected);
        }

    }

    @Nested
    @DisplayName("공지 전체 조회")
    class FindAll {

        @DisplayName("공지 전체 조회")
        @Test
        void findAll1() {
            // given
            final String title = "공지 제목";
            final LocalDate date = LocalDate.of(2025, 7, 9);
            final LocalTime time = LocalTime.of(16, 25);
            final String content = "공지 내용";

            final Announcement announcement1 = new Announcement(1L, title, date, time, content);
            final Announcement announcement2 = new Announcement(2L, title, date, time, content);
            final Announcement announcement3 = new Announcement(3L, title, date, time, content);
            given(announcementJpaRepository.findAll())
                    .willReturn(List.of(announcement1, announcement2, announcement3));

            // when
            final List<AnnouncementResponse> actual = announcementService.findAllAnnouncement();

            // then
            assertThat(actual)
                    .hasSize(3);
        }
    }
}
