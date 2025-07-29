package com.daedan.festabook.announcement.service;

import com.daedan.festabook.announcement.controller.AnnouncementPinUpdateRequest;
import com.daedan.festabook.announcement.domain.Announcement;
import com.daedan.festabook.announcement.dto.AnnouncementGroupedResponses;
import com.daedan.festabook.announcement.dto.AnnouncementRequest;
import com.daedan.festabook.announcement.dto.AnnouncementResponse;
import com.daedan.festabook.announcement.dto.AnnouncementUpdateRequest;
import com.daedan.festabook.announcement.infrastructure.AnnouncementJpaRepository;
import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.notification.dto.NotificationMessage;
import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationNotificationManager;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
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

    private final AnnouncementJpaRepository announcementJpaRepository;
    private final OrganizationJpaRepository organizationJpaRepository;
    private final OrganizationNotificationManager notificationManager;

    @Transactional
    public AnnouncementResponse createAnnouncement(Long organizationId, AnnouncementRequest request) {
        if (request.isPinned()) {
            validatePinnedLimit(organizationId);
        }

        Organization organization = getOrganizationById(organizationId);
        Announcement announcement = request.toEntity(organization);
        announcementJpaRepository.save(announcement);

        NotificationMessage notificationMessage = new NotificationMessage(
                request.title(),
                request.content()
        );
        notificationManager.sendToOrganizationTopic(organizationId, notificationMessage);

        return AnnouncementResponse.from(announcement);
    }

    public AnnouncementGroupedResponses getGroupedAnnouncementByOrganizationId(Long organizationId) {
        List<Announcement> announcements = announcementJpaRepository.findAllByOrganizationId(organizationId);

        List<Announcement> pinnedAnnouncements = filterAndSortAnnouncements(announcements, Announcement::isPinned);
        List<Announcement> unpinnedAnnouncements = filterAndSortAnnouncements(announcements, Announcement::isUnpinned);

        return AnnouncementGroupedResponses.from(
                pinnedAnnouncements,
                unpinnedAnnouncements
        );
    }

    @Transactional
    public AnnouncementResponse updateAnnouncement(Long announcementId, AnnouncementUpdateRequest request) {
        Announcement announcement = getAnnouncementById(announcementId);
        announcement.updateTitleAndContent(request.title(), request.content());
        return AnnouncementResponse.from(announcement);
    }

    @Transactional
    public void updateAnnouncementPin(Long announcementId, AnnouncementPinUpdateRequest request) {
        Announcement announcement = getAnnouncementById(announcementId);
        announcement.updatePinned(request.pinned());
    }

    public void deleteAnnouncementByAnnouncementId(Long announcementId) {
        announcementJpaRepository.deleteById(announcementId);
    }

    private Announcement getAnnouncementById(Long announcementId) {
        return announcementJpaRepository.findById(announcementId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 공지입니다.", HttpStatus.BAD_REQUEST));
    }

    private Organization getOrganizationById(Long organizationId) {
        return organizationJpaRepository.findById(organizationId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 조직입니다.", HttpStatus.BAD_REQUEST));
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

    private void validatePinnedLimit(Long organizationId) {
        Long pinnedCount = announcementJpaRepository.countByOrganizationIdAndIsPinnedTrue(organizationId);
        if (pinnedCount >= MAX_PINNED_ANNOUNCEMENTS) {
            throw new BusinessException(
                    String.format("공지글은 최대 %d개까지 고정할 수 있습니다.", MAX_PINNED_ANNOUNCEMENTS),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private Comparator<Announcement> createdAtDescending() {
        return Comparator.comparing(Announcement::getCreatedAt).reversed();
    }
}
