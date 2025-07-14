package com.daedan.festabook.announcement.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.daedan.festabook.announcement.domain.Announcement;
import com.daedan.festabook.announcement.domain.AnnouncementFixture;
import com.daedan.festabook.announcement.dto.AnnouncementResponses;
import com.daedan.festabook.announcement.infrastructure.AnnouncementJpaRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AnnouncementServiceTest {

    private static final Long DEFAULT_ORGANIZATION_ID = 1L;

    @Mock
    private AnnouncementJpaRepository announcementJpaRepository;

    @InjectMocks
    private AnnouncementService announcementService;

    @Nested
    class getAllAnnouncementByOrganizationId {

        @Test
        void 성공() {
            // given
            Announcement announcement1 = AnnouncementFixture.create();
            Announcement announcement2 = AnnouncementFixture.create();
            Announcement announcement3 = AnnouncementFixture.create();
            given(announcementJpaRepository.findAllByOrganizationId(DEFAULT_ORGANIZATION_ID))
                    .willReturn(List.of(announcement1, announcement2, announcement3));

            AnnouncementResponses expected = AnnouncementResponses.from(
                    List.of(announcement1, announcement2, announcement3));

            // when
            AnnouncementResponses result = announcementService.getAllAnnouncementByOrganizationId(
                    DEFAULT_ORGANIZATION_ID);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        void 성공_빈컬렉션() {
            // given
            given(announcementJpaRepository.findAllByOrganizationId(DEFAULT_ORGANIZATION_ID))
                    .willReturn(List.of());

            AnnouncementResponses expected = new AnnouncementResponses(List.of());

            // when
            AnnouncementResponses result = announcementService.getAllAnnouncementByOrganizationId(
                    DEFAULT_ORGANIZATION_ID);

            // then
            assertThat(result).isEqualTo(expected);
        }
    }
}
