package com.daedan.festabook.announcement.service;

import com.daedan.festabook.announcement.domain.Announcement;
import com.daedan.festabook.announcement.dto.AnnouncementGroupedResponses;
import com.daedan.festabook.announcement.dto.AnnouncementPinUpdateRequest;
import com.daedan.festabook.announcement.dto.AnnouncementPinUpdateResponse;
import com.daedan.festabook.announcement.dto.AnnouncementRequest;
import com.daedan.festabook.announcement.dto.AnnouncementResponse;
import com.daedan.festabook.announcement.dto.AnnouncementUpdateRequest;
import com.daedan.festabook.announcement.dto.AnnouncementUpdateResponse;
import com.daedan.festabook.announcement.infrastructure.AnnouncementJpaRepository;
import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalNotificationManager;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.global.lock.Lockable;
import com.daedan.festabook.notification.dto.NotificationSendRequest;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private static final int MAX_PINNED_ANNOUNCEMENTS = 3;
    private static final String ANNOUNCEMENT_TITLE_WITH_UNIVERSITY_NAME_FORMAT = "[%s] %s";

    private final AnnouncementJpaRepository announcementJpaRepository;
    private final FestivalJpaRepository festivalJpaRepository;
    private final FestivalNotificationManager notificationManager;

    @Lockable(
            spelKey = "'AnnouncementService'.concat(#festivalId)",
            useMethodScopeLock = false
    )
    @Transactional
    public AnnouncementResponse createAnnouncement(Long festivalId, AnnouncementRequest request) {
        Festival festival = getFestivalById(festivalId);
        Announcement announcement = request.toEntity(festival);
        if (request.isPinned()) {
            validatePinnedLimit(festivalId);
        }

        announcementJpaRepository.save(announcement);
        return AnnouncementResponse.from(announcement);
    }

    public AnnouncementGroupedResponses getGroupedAnnouncementByFestivalId(Long festivalId) {
        List<Announcement> announcements = announcementJpaRepository.findAllByFestivalId(festivalId);

        List<Announcement> pinnedAnnouncements = filterAndSortAnnouncements(announcements, Announcement::isPinned);
        List<Announcement> unpinnedAnnouncements = filterAndSortAnnouncements(announcements, Announcement::isUnpinned);

        return AnnouncementGroupedResponses.from(
                pinnedAnnouncements,
                unpinnedAnnouncements
        );
    }

    @Transactional
    public AnnouncementUpdateResponse updateAnnouncement(
            Long festivalId,
            Long announcementId,
            AnnouncementUpdateRequest request
    ) {
        Announcement announcement = getAnnouncementById(announcementId);
        validateAnnouncementBelongsToFestival(announcement, festivalId);

        announcement.updateTitleAndContent(request.title(), request.content());
        return AnnouncementUpdateResponse.from(announcement);
    }

    @Lockable(
            spelKey = "'AnnouncementService'.concat(#festivalId)",
            useMethodScopeLock = false
    )
    @Transactional
    public AnnouncementPinUpdateResponse updateAnnouncementPin(
            Long festivalId,
            Long announcementId,
            AnnouncementPinUpdateRequest request
    ) {
        Announcement announcement = getAnnouncementById(announcementId);
        validateAnnouncementBelongsToFestival(announcement, festivalId);
        if (announcement.isUnpinned() && request.pinned()) {
            validatePinnedLimit(festivalId);
        }

        announcement.updatePinned(request.pinned());
        return AnnouncementPinUpdateResponse.from(announcement);
    }

    @Transactional
    public void deleteAnnouncementByAnnouncementId(Long festivalId, Long announcementId) {
        Announcement announcement = getAnnouncementById(announcementId);
        validateAnnouncementBelongsToFestival(announcement, festivalId);

        announcementJpaRepository.delete(announcement);
    }

    public void sendAnnouncementNotification(Long festivalId, Long announcementId) {
        Announcement announcement = getAnnouncementById(announcementId);
        Festival festival = getFestivalById(festivalId);
        validateAnnouncementBelongsToFestival(announcement, festival.getId());

        NotificationSendRequest request = NotificationSendRequest.builder()
                .title(formatTitleWithUniversityName(festival, announcement))
                .body(announcement.getContent())
                .putData("announcementId", String.valueOf(announcement.getId()))
                .build();

        notificationManager.sendToFestivalTopic(festivalId, request);
    }

    private static String formatTitleWithUniversityName(Festival festival, Announcement announcement) {
        return String.format(
                ANNOUNCEMENT_TITLE_WITH_UNIVERSITY_NAME_FORMAT,
                festival.getUniversityName(), announcement.getTitle()
        );
    }

    private Announcement getAnnouncementById(Long announcementId) {
        return announcementJpaRepository.findById(announcementId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 공지입니다.", HttpStatus.BAD_REQUEST));
    }

    private Festival getFestivalById(Long festivalId) {
        return festivalJpaRepository.findById(festivalId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 축제입니다.", HttpStatus.BAD_REQUEST));
    }

    private List<Announcement> filterAndSortAnnouncements(
            List<Announcement> announcements,
            Predicate<Announcement> filter
    ) {
        return announcements.stream()
                .filter(filter)
                .sorted(createdAtDescending())
                .toList();
    }

    private Comparator<Announcement> createdAtDescending() {
        return Comparator.comparing(Announcement::getCreatedAt).reversed();
    }

    private void validatePinnedLimit(Long festivalId) {
        Long pinnedCount = announcementJpaRepository.countByFestivalIdAndIsPinnedTrue(festivalId);
        if (pinnedCount >= MAX_PINNED_ANNOUNCEMENTS) {
            throw new BusinessException(
                    String.format("공지글은 최대 %d개까지 고정할 수 있습니다.", MAX_PINNED_ANNOUNCEMENTS),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private void validateAnnouncementBelongsToFestival(Announcement announcement, Long festivalId) {
        if (!announcement.isFestivalIdEqualTo(festivalId)) {
            throw new BusinessException("해당 축제의 공지가 아닙니다.", HttpStatus.FORBIDDEN);
        }
    }
}
