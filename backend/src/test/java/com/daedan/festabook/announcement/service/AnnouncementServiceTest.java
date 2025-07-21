package com.daedan.festabook.announcement.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import com.daedan.festabook.announcement.domain.Announcement;
import com.daedan.festabook.announcement.domain.AnnouncementFixture;
import com.daedan.festabook.announcement.domain.AnnouncementRequestFixture;
import com.daedan.festabook.announcement.dto.AnnouncementRequest;
import com.daedan.festabook.announcement.dto.AnnouncementResponse;
import com.daedan.festabook.announcement.dto.AnnouncementResponses;
import com.daedan.festabook.announcement.infrastructure.AnnouncementJpaRepository;
import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import com.daedan.festabook.organization.domain.OrganizationNotificationManager;
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
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AnnouncementServiceTest {

    private static final Long DEFAULT_ORGANIZATION_ID = 1L;

    @Mock
    private AnnouncementJpaRepository announcementJpaRepository;

    @Mock
    private OrganizationJpaRepository organizationJpaRepository;

    @Mock
    private OrganizationNotificationManager notificationManager;

    @InjectMocks
    private AnnouncementService announcementService;

    @Nested
    class createAnnouncement {

        @Test
        void 성공() {
            // given
            AnnouncementRequest announcementRequest = AnnouncementRequestFixture.create(
                    "폭우가 내립니다.",
                    "우산을 챙겨주세요.",
                    false
            );
            Long organizationId = 1L;
            Organization organization = OrganizationFixture.create(organizationId);

            given(organizationJpaRepository.findById(organizationId))
                    .willReturn(Optional.of(organization));

            // when
            AnnouncementResponse result = announcementService.createAnnouncement(organizationId, announcementRequest);

            // then
            assertSoftly(s -> {
                s.assertThat(result.title()).isEqualTo(announcementRequest.title());
                s.assertThat(result.content()).isEqualTo(announcementRequest.content());
                s.assertThat(result.isPinned()).isEqualTo(announcementRequest.isPinned());
                verify(notificationManager).sendToOrganizationTopic(any(), any());
            });
        }

        @Test
        void 예외_존재하지_않는_조직_ID() {
            // given
            Long invalidDeviceId = 0L;
            AnnouncementRequest request = AnnouncementRequestFixture.create();

            given(organizationJpaRepository.findById(invalidDeviceId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() ->
                    announcementService.createAnnouncement(invalidDeviceId, request)
            ).isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 조직입니다.");
        }

        @Test
        void 예외_알림_전송_실패시_예외_전파() {
            // given
            Long organizationId = 1L;
            AnnouncementRequest request = AnnouncementRequestFixture.create();
            Organization organization = OrganizationFixture.create(organizationId);

            given(organizationJpaRepository.findById(organizationId))
                    .willReturn(Optional.of(organization));
            doThrow(new BusinessException("FCM 메시지 전송을 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR))
                    .when(notificationManager).sendToOrganizationTopic(any(), any());

            // when & then
            assertThatThrownBy(() ->
                    announcementService.createAnnouncement(organizationId, request)
            ).isInstanceOf(BusinessException.class)
                    .hasMessage("FCM 메시지 전송을 실패했습니다.");
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
