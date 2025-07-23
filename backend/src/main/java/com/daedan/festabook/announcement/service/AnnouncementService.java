package com.daedan.festabook.announcement.service;

import com.daedan.festabook.announcement.domain.Announcement;
import com.daedan.festabook.announcement.dto.AnnouncementGroupedResponses;
import com.daedan.festabook.announcement.dto.AnnouncementRequest;
import com.daedan.festabook.announcement.dto.AnnouncementResponse;
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

    private final AnnouncementJpaRepository announcementJpaRepository;
    private final OrganizationJpaRepository organizationJpaRepository;
    private final OrganizationNotificationManager notificationManager;

    @Transactional
    public AnnouncementResponse createAnnouncement(Long organizationId, AnnouncementRequest request) {
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

    private Organization getOrganizationById(Long organizationId) {
        // TODO: 커스텀 예외 설정
        return organizationJpaRepository.findById(organizationId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 조직입니다.", HttpStatus.BAD_REQUEST));
    }

    private List<Announcement> filterAndSortAnnouncements(
            List<Announcement> announcements,
            Predicate<Announcement> filter
    ) {
        return announcements.stream()
                .filter(filter)
                .sorted(getCreatedAtDescendingComparator())
                .toList();
    }

    private Comparator<Announcement> getCreatedAtDescendingComparator() {
        return Comparator.comparing(Announcement::getCreatedAt).reversed();
    }
}
