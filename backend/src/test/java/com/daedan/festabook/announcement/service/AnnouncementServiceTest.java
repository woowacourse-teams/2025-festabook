package com.daedan.festabook.announcement.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;

import com.daedan.festabook.announcement.domain.Announcement;
import com.daedan.festabook.announcement.domain.AnnouncementFixture;
import com.daedan.festabook.announcement.dto.AnnouncementGroupedResponses;
import com.daedan.festabook.announcement.dto.AnnouncementPinUpdateRequest;
import com.daedan.festabook.announcement.dto.AnnouncementPinUpdateRequestFixture;
import com.daedan.festabook.announcement.dto.AnnouncementPinUpdateResponse;
import com.daedan.festabook.announcement.dto.AnnouncementRequest;
import com.daedan.festabook.announcement.dto.AnnouncementRequestFixture;
import com.daedan.festabook.announcement.dto.AnnouncementResponse;
import com.daedan.festabook.announcement.dto.AnnouncementUpdateRequest;
import com.daedan.festabook.announcement.dto.AnnouncementUpdateRequestFixture;
import com.daedan.festabook.announcement.dto.AnnouncementUpdateResponse;
import com.daedan.festabook.announcement.infrastructure.AnnouncementJpaRepository;
import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.festival.domain.FestivalNotificationManager;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.global.exception.BusinessException;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AnnouncementServiceTest {

    @Mock
    private AnnouncementJpaRepository announcementJpaRepository;

    @Mock
    private FestivalJpaRepository festivalJpaRepository;

    @Mock
    private FestivalNotificationManager festivalNotificationManager;

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
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);

            given(festivalJpaRepository.findById(festivalId))
                    .willReturn(Optional.of(festival));

            // when
            AnnouncementResponse result = announcementService.createAnnouncement(festivalId, request);

            // then
            assertSoftly(s -> {
                s.assertThat(result.title()).isEqualTo(request.title());
                s.assertThat(result.content()).isEqualTo(request.content());
                s.assertThat(result.isPinned()).isEqualTo(request.isPinned());
            });

            then(festivalNotificationManager).should()
                    .sendToFestivalTopic(any(), any());
        }

        @ParameterizedTest(name = "고정 공지 개수: {0}")
        @ValueSource(longs = {0L, 1L, 2L})
        void 성공_고정_공지_개수_제한_미만(Long pinnedCount) {
            // given
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            AnnouncementRequest request = AnnouncementRequestFixture.create(true);

            given(announcementJpaRepository.countByFestivalIdAndIsPinnedTrue(festivalId))
                    .willReturn(pinnedCount);
            given(festivalJpaRepository.findById(festivalId))
                    .willReturn(Optional.of(festival));

            // when
            announcementService.createAnnouncement(festivalId, request);

            // then
            then(announcementJpaRepository).should()
                    .save(any(Announcement.class));

            then(festivalNotificationManager).should()
                    .sendToFestivalTopic(any(), any());
        }

        @Test
        void 예외_존재하지_않는_축제_ID() {
            // given
            Long invalidDeviceId = 0L;
            AnnouncementRequest request = AnnouncementRequestFixture.create();

            given(festivalJpaRepository.findById(invalidDeviceId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> announcementService.createAnnouncement(invalidDeviceId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 축제입니다.");
        }

        @Test
        void 예외_알림_전송_실패시_예외_전파() {
            // given
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);

            AnnouncementRequest request = AnnouncementRequestFixture.create();

            given(festivalJpaRepository.findById(festivalId))
                    .willReturn(Optional.of(festival));

            willThrow(new BusinessException("FCM 메시지 전송을 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR))
                    .given(festivalNotificationManager)
                    .sendToFestivalTopic(any(), any());

            // when & then
            assertThatThrownBy(() -> announcementService.createAnnouncement(festivalId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("FCM 메시지 전송을 실패했습니다.");
        }

        @ParameterizedTest(name = "고정 공지 개수: {0}")
        @ValueSource(longs = {3L, 4L, 10L})
        void 예외_고정_공지_개수_제한_초과(Long maxPinnedCount) {
            // given
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            AnnouncementRequest request = AnnouncementRequestFixture.create(true);

            given(festivalJpaRepository.findById(festival.getId()))
                    .willReturn(Optional.of(festival));
            given(announcementJpaRepository.countByFestivalIdAndIsPinnedTrue(festivalId))
                    .willReturn(maxPinnedCount);

            // when & then
            assertThatThrownBy(() -> announcementService.createAnnouncement(festivalId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("공지글은 최대 3개까지 고정할 수 있습니다.");
        }
    }

    @Nested
    class getGroupedAnnouncementByFestivalId {

        @Test
        void 성공_고정_분류() {
            // given
            Long festivalId = 1L;
            int expectedPinedSize = 1;
            int expectedUnPinedSize = 2;

            List<Announcement> pinnedAnnouncements = AnnouncementFixture.createList(expectedPinedSize, true);
            List<Announcement> unPinnedAnnouncements = AnnouncementFixture.createList(expectedUnPinedSize, false);

            List<Announcement> allAnnouncements = new ArrayList<>();
            allAnnouncements.addAll(pinnedAnnouncements);
            allAnnouncements.addAll(unPinnedAnnouncements);

            given(announcementJpaRepository.findAllByFestivalId(festivalId))
                    .willReturn(allAnnouncements);

            // when
            AnnouncementGroupedResponses result = announcementService.getGroupedAnnouncementByFestivalId(festivalId);

            // then
            assertSoftly(s -> {
                s.assertThat(result.pinned().responses()).hasSize(1);
                s.assertThat(result.unpinned().responses()).hasSize(2);
            });
        }

        @Test
        void 성공_생성_날짜_시간_역순으로_정렬() {
            // given
            Long festivalId = 1L;

            // pinned 공지
            Announcement announcement1 = AnnouncementFixture.create(true, LocalDateTime.of(2025, 5, 2, 10, 0));
            Announcement announcement2 = AnnouncementFixture.create(true, LocalDateTime.of(2025, 5, 3, 10, 0));
            Announcement announcement3 = AnnouncementFixture.create(true, LocalDateTime.of(2025, 6, 3, 10, 0));

            // unpinned 공지
            Announcement announcement4 = AnnouncementFixture.create(false, LocalDateTime.of(2025, 5, 2, 10, 0));
            Announcement announcement5 = AnnouncementFixture.create(false, LocalDateTime.of(2025, 5, 3, 10, 0));
            Announcement announcement6 = AnnouncementFixture.create(false, LocalDateTime.of(2025, 6, 2, 10, 0));

            given(announcementJpaRepository.findAllByFestivalId(festivalId))
                    .willReturn(List.of(
                            announcement1, announcement2, announcement3,
                            announcement4, announcement5, announcement6
                    ));

            // when
            AnnouncementGroupedResponses result = announcementService.getGroupedAnnouncementByFestivalId(festivalId);

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
            Long festivalId = 1L;

            given(announcementJpaRepository.findAllByFestivalId(festivalId))
                    .willReturn(List.of());

            // when
            AnnouncementGroupedResponses result = announcementService.getGroupedAnnouncementByFestivalId(festivalId);

            // then
            assertSoftly(s -> {
                s.assertThat(result.pinned().responses()).isEmpty();
                s.assertThat(result.unpinned().responses()).isEmpty();
            });
        }
    }

    @Nested
    class updateAnnouncement {

        @Test
        void 성공() {
            // given
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            Announcement announcement = AnnouncementFixture.create(festival);

            given(announcementJpaRepository.findById(announcement.getId()))
                    .willReturn(Optional.of(announcement));

            AnnouncementUpdateRequest request = AnnouncementUpdateRequestFixture.create("new title", "new content");

            // when
            AnnouncementUpdateResponse result = announcementService.updateAnnouncement(
                    festival.getId(),
                    announcement.getId(),
                    request
            );

            // then
            assertSoftly(s -> {
                s.assertThat(result.title()).isEqualTo(request.title());
                s.assertThat(result.content()).isEqualTo(request.content());
            });
        }

        @Test
        void 예외_존재하지_않는_공지사항_ID() {
            // given
            Long invalidAnnouncementId = 0L;
            Festival festival = FestivalFixture.create();
            AnnouncementUpdateRequest request = AnnouncementUpdateRequestFixture.create();

            given(announcementJpaRepository.findById(invalidAnnouncementId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() ->
                    announcementService.updateAnnouncement(festival.getId(), invalidAnnouncementId, request)
            )
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 공지입니다.");
        }

        @Test
        void 예외_다른_축제의_공지일_경우() {
            // given
            Long requestFestivalId = 1L;
            Long otherFestivalId = 999L;
            Festival requestFestival = FestivalFixture.create(requestFestivalId);
            Festival otherFestival = FestivalFixture.create(otherFestivalId);
            Announcement announcement = AnnouncementFixture.create(requestFestival);

            given(announcementJpaRepository.findById(announcement.getId()))
                    .willReturn(Optional.of(announcement));

            AnnouncementUpdateRequest request = AnnouncementUpdateRequestFixture.create();

            // when & then
            assertThatThrownBy(() ->
                    announcementService.updateAnnouncement(otherFestival.getId(), announcement.getId(), request)
            )
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("해당 축제의 공지가 아닙니다.");
        }
    }

    @Nested
    class updateAnnouncementPin {

        @Test
        void 성공() {
            // given
            Long festivalId = 1L;
            Long announcementId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            Announcement announcement = AnnouncementFixture.create(announcementId, false, festival);
            AnnouncementPinUpdateRequest request = AnnouncementPinUpdateRequestFixture.create(true);

            given(announcementJpaRepository.findById(announcementId))
                    .willReturn(Optional.of(announcement));
            given(announcementJpaRepository.countByFestivalIdAndIsPinnedTrue(festivalId))
                    .willReturn(0L);

            // when
            AnnouncementPinUpdateResponse result = announcementService.updateAnnouncementPin(
                    festivalId,
                    announcementId,
                    request
            );

            // then
            assertThat(result.isPinned()).isEqualTo(request.pinned());
        }

        @Test
        void 예외_존재하지_않는_공지사항_ID() {
            // given
            Long festivalId = 1L;
            Long invalidAnnouncementId = 0L;
            AnnouncementPinUpdateRequest request = AnnouncementPinUpdateRequestFixture.create();

            given(announcementJpaRepository.findById(invalidAnnouncementId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() ->
                    announcementService.updateAnnouncementPin(
                            festivalId,
                            invalidAnnouncementId,
                            request
                    )
            )
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 공지입니다.");
        }

        @Test
        void 예외_고정_공지_개수_제한_초과() {
            // given
            Long festivalId = 1L;
            Long announcementId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            Announcement announcement = AnnouncementFixture.create(announcementId, false, festival);
            AnnouncementPinUpdateRequest request = AnnouncementPinUpdateRequestFixture.create(true);

            given(announcementJpaRepository.findById(announcementId))
                    .willReturn(Optional.of(announcement));

            Long pinnedCountLimit = 3L;
            given(announcementJpaRepository.countByFestivalIdAndIsPinnedTrue(festivalId))
                    .willReturn(pinnedCountLimit);

            // when & then
            assertThatThrownBy(() ->
                    announcementService.updateAnnouncementPin(
                            festivalId,
                            announcementId,
                            request
                    )
            )
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("공지글은 최대 3개까지 고정할 수 있습니다.");
        }

        @Test
        void 성공_고정된_공지는_고정_공지_개수_제한_검증_안함() {
            // given
            Long festivalId = 1L;
            Long announcementId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            Announcement announcement = AnnouncementFixture.create(announcementId, true, festival);
            AnnouncementPinUpdateRequest request = AnnouncementPinUpdateRequestFixture.create(true);

            given(announcementJpaRepository.findById(announcementId))
                    .willReturn(Optional.of(announcement));

            // when
            announcementService.updateAnnouncementPin(festivalId, announcementId, request);

            // then
            then(announcementJpaRepository).should(never())
                    .countByFestivalIdAndIsPinnedTrue(festivalId);
        }

        @Test
        void 예외_다른_축제의_공지일_경우() {
            // given
            Long requestFestivalId = 1L;
            Long otherFestivalId = 999L;
            Festival requestFestival = FestivalFixture.create(requestFestivalId);
            Festival otherFestival = FestivalFixture.create(otherFestivalId);
            Announcement announcement = AnnouncementFixture.create(requestFestival);

            given(announcementJpaRepository.findById(announcement.getId()))
                    .willReturn(Optional.of(announcement));

            AnnouncementPinUpdateRequest request = AnnouncementPinUpdateRequestFixture.create();

            // when & then
            assertThatThrownBy(() ->
                    announcementService.updateAnnouncementPin(otherFestival.getId(), announcement.getId(), request)
            )
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("해당 축제의 공지가 아닙니다.");
        }
    }

    @Nested
    class deleteByAnnouncementId {

        @Test
        void 성공() {
            // given
            Long announcementId = 1L;
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            Announcement announcement = AnnouncementFixture.create(announcementId, festival);

            given(announcementJpaRepository.findById(announcementId))
                    .willReturn(Optional.of(announcement));

            // when
            announcementService.deleteAnnouncementByAnnouncementId(festivalId, announcementId);

            // then
            then(announcementJpaRepository).should()
                    .delete(announcement);
        }

        @Test
        void 예외_다른_축제의_공지일_경우() {
            // given
            Long requestFestivalId = 1L;
            Long otherFestivalId = 999L;
            Festival requestFestival = FestivalFixture.create(requestFestivalId);
            Festival otherFestival = FestivalFixture.create(otherFestivalId);
            Announcement announcement = AnnouncementFixture.create(requestFestival);

            given(announcementJpaRepository.findById(announcement.getId()))
                    .willReturn(Optional.of(announcement));

            // when & then
            assertThatThrownBy(() ->
                    announcementService.deleteAnnouncementByAnnouncementId(
                            otherFestival.getId(),
                            announcement.getId()
                    )
            )
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("해당 축제의 공지가 아닙니다.");
        }
    }
}
