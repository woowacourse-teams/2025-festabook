package com.daedan.festabook.announcement.service;

import com.daedan.festabook.announcement.domain.Announcement;
import com.daedan.festabook.announcement.dto.AnnouncementRequest;
import com.daedan.festabook.announcement.dto.AnnouncementResponse;
import com.daedan.festabook.announcement.dto.AnnouncementResponses;
import com.daedan.festabook.announcement.infrastructure.AnnouncementJpaRepository;
import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.notification.constants.TopicConstants;
import com.daedan.festabook.notification.dto.NotificationRequest;
import com.daedan.festabook.notification.service.NotificationService;
import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementJpaRepository announcementJpaRepository;
    private final OrganizationJpaRepository organizationJpaRepository;
    private final NotificationService notificationService;

    @Transactional
    public AnnouncementResponse createAnnouncement(Long organizationId, AnnouncementRequest request) {
        Organization organization = getOrganizationById(organizationId);
        Announcement announcement = request.toEntity(organization);
        announcementJpaRepository.save(announcement);

        NotificationRequest notificationRequest = new NotificationRequest(
                TopicConstants.getOrganizationTopicById(organizationId),
                request.title(),
                request.content()
        );
        notificationService.sendToTopic(notificationRequest);

        return AnnouncementResponse.from(announcement);
    }

    public AnnouncementResponses getAllAnnouncementByOrganizationId(Long organizationId) {
        return AnnouncementResponses.from(announcementJpaRepository.findAllByOrganizationId(organizationId));
    }

    private Organization getOrganizationById(Long organizationId) {
        // TODO: 커스텀 예외 설정
        return organizationJpaRepository.findById(organizationId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 조직입니다.", HttpStatus.BAD_REQUEST));
    }
}
