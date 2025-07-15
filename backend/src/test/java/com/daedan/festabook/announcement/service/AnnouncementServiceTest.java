package com.daedan.festabook.announcement.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.daedan.festabook.announcement.domain.Announcement;
import com.daedan.festabook.announcement.domain.AnnouncementFixture;
import com.daedan.festabook.announcement.dto.AnnouncementRequest;
import com.daedan.festabook.announcement.dto.AnnouncementResponse;
import com.daedan.festabook.announcement.dto.AnnouncementResponses;
import com.daedan.festabook.announcement.infrastructure.AnnouncementJpaRepository;
import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import java.util.List;
import java.util.Optional;
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

    @Mock
    private OrganizationJpaRepository organizationJpaRepository;

    @InjectMocks
    private AnnouncementService announcementService;

    @Nested
    class createAnnouncement {

        @Test
        void 성공() {
            // given
            AnnouncementRequest request = new AnnouncementRequest(
                    "폭우가 내립니다.",
                    "우산을 챙겨주세요.",
                    true
            );
            Long organizationId = 1L;
            Organization organization = OrganizationFixture.create();

            given(organizationJpaRepository.findById(organizationId))
                    .willReturn(Optional.of(organization));

            // when
            AnnouncementResponse result = announcementService.createAnnouncement(1L, request);

            // then
            assertThat(result.title()).isEqualTo(request.title());
            assertThat(result.content()).isEqualTo(request.content());
            assertThat(result.isPinned()).isEqualTo(request.isPinned());
        }
    }

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
