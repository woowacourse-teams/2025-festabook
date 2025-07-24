package com.daedan.festabook.announcement.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

import com.daedan.festabook.announcement.domain.Announcement;
import com.daedan.festabook.announcement.domain.AnnouncementFixture;
import com.daedan.festabook.announcement.domain.AnnouncementRequestFixture;
import com.daedan.festabook.announcement.dto.AnnouncementGroupedResponses;
import com.daedan.festabook.announcement.dto.AnnouncementRequest;
import com.daedan.festabook.announcement.dto.AnnouncementResponse;
import com.daedan.festabook.announcement.infrastructure.AnnouncementJpaRepository;
import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import com.daedan.festabook.organization.domain.OrganizationNotificationManager;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
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
    private OrganizationNotificationManager organizationNotificationManager;

    @InjectMocks
    private AnnouncementService announcementService;

    @Nested
    class createAnnouncement {

        @Test
        void 성공() {
            // given
            AnnouncementRequest request = AnnouncementRequestFixture.create(
                    "폭우가 내립니다.",
                    "우산을 챙겨주세요.",
                    false
            );
            Long organizationId = 1L;
            Organization organization = OrganizationFixture.create(organizationId);

            given(organizationJpaRepository.findById(organizationId))
                    .willReturn(Optional.of(organization));

            // when
            AnnouncementResponse result = announcementService.createAnnouncement(organizationId, request);

            // then
            assertSoftly(s -> {
                s.assertThat(result.title()).isEqualTo(request.title());
                s.assertThat(result.content()).isEqualTo(request.content());
                s.assertThat(result.isPinned()).isEqualTo(request.isPinned());
            });
            then(organizationNotificationManager).should()
                    .sendToOrganizationTopic(any(), any());
        }

        @Test
        void 예외_존재하지_않는_조직_ID() {
            // given
            Long invalidDeviceId = 0L;
            AnnouncementRequest request = AnnouncementRequestFixture.create();

            given(organizationJpaRepository.findById(invalidDeviceId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> announcementService.createAnnouncement(invalidDeviceId, request))
                    .isInstanceOf(BusinessException.class)
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

            willThrow(new BusinessException("FCM 메시지 전송을 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR))
                    .given(organizationNotificationManager)
                    .sendToOrganizationTopic(any(), any());

            // when & then
            assertThatThrownBy(() -> announcementService.createAnnouncement(organizationId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("FCM 메시지 전송을 실패했습니다.");
        }
    }

    @Nested
    class getGroupedAnnouncementByOrganizationId {

        @Test
        void 성공_고정_분류() {
            // given
            int expectedPinedSize = 1;
            int expectedUnPinedSize = 2;

            List<Announcement> pinnedAnnouncements = AnnouncementFixture.createList(expectedPinedSize, true);
            List<Announcement> unPinnedAnnouncements = AnnouncementFixture.createList(expectedUnPinedSize, false);

            List<Announcement> allAnnouncements = new ArrayList<>();
            allAnnouncements.addAll(pinnedAnnouncements);
            allAnnouncements.addAll(unPinnedAnnouncements);

            given(announcementJpaRepository.findAllByOrganizationId(DEFAULT_ORGANIZATION_ID))
                    .willReturn(allAnnouncements);

            // when
            AnnouncementGroupedResponses result = announcementService.getGroupedAnnouncementByOrganizationId(
                    DEFAULT_ORGANIZATION_ID);

            // then
            assertSoftly(s -> {
                s.assertThat(result.pinned().responses()).hasSize(1);
                s.assertThat(result.unpinned().responses()).hasSize(2);
            });
        }

        @Test
        void 성공_생성_날짜_시간_역순으로_정렬() {
            // given
            // pinned 공지
            Announcement announcement1 = AnnouncementFixture.create(true, LocalDateTime.of(2025, 5, 2, 10, 0));
            Announcement announcement2 = AnnouncementFixture.create(true, LocalDateTime.of(2025, 5, 3, 10, 0));
            Announcement announcement3 = AnnouncementFixture.create(true, LocalDateTime.of(2025, 6, 3, 10, 0));

            // unpinned 공지
            Announcement announcement4 = AnnouncementFixture.create(false, LocalDateTime.of(2025, 5, 2, 10, 0));
            Announcement announcement5 = AnnouncementFixture.create(false, LocalDateTime.of(2025, 5, 3, 10, 0));
            Announcement announcement6 = AnnouncementFixture.create(false, LocalDateTime.of(2025, 6, 2, 10, 0));

            given(announcementJpaRepository.findAllByOrganizationId(DEFAULT_ORGANIZATION_ID))
                    .willReturn(List.of(
                            announcement1, announcement2, announcement3,
                            announcement4, announcement5, announcement6
                    ));

            // when
            AnnouncementGroupedResponses result = announcementService.getGroupedAnnouncementByOrganizationId(
                    DEFAULT_ORGANIZATION_ID);

            // then
            assertSoftly(s -> {
                List<LocalDateTime> pinnedDates = result.pinned().responses().stream()
                        .map(AnnouncementResponse::createdAt)
                        .toList();

                List<LocalDateTime> unpinnedDates = result.unpinned().responses().stream()
                        .map(AnnouncementResponse::createdAt)
                        .toList();

                s.assertThat(pinnedDates).isSortedAccordingTo(Comparator.reverseOrder());
                s.assertThat(unpinnedDates).isSortedAccordingTo(Comparator.reverseOrder());
            });
        }

        @Test
        void 성공_빈_컬렉션() {
            // given
            given(announcementJpaRepository.findAllByOrganizationId(DEFAULT_ORGANIZATION_ID))
                    .willReturn(List.of());

            // when
            AnnouncementGroupedResponses result = announcementService.getGroupedAnnouncementByOrganizationId(
                    DEFAULT_ORGANIZATION_ID);

            // then
            assertSoftly(s -> {
                s.assertThat(result.pinned().responses()).isEmpty();
                s.assertThat(result.unpinned().responses()).isEmpty();
            });
        }
    }
}
