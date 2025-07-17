package com.daedan.festabook.organization.service;

import com.daedan.festabook.device.domain.Device;
import com.daedan.festabook.device.infrastructure.DeviceJpaRepository;
import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.notification.constants.TopicConstants;
import com.daedan.festabook.notification.service.NotificationService;
import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationBookmark;
import com.daedan.festabook.organization.dto.OrganizationBookmarkResponse;
import com.daedan.festabook.organization.infrastructure.OrganizationBookmarkJpaRepository;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrganizationBookmarkService {

    private final OrganizationBookmarkJpaRepository organizationBookmarkJpaRepository;
    private final DeviceJpaRepository deviceJpaRepository;
    private final OrganizationJpaRepository organizationJpaRepository;
    private final NotificationService notificationService;

    @Transactional
    public OrganizationBookmarkResponse createOrganizationBookmark(Long organizationId, Long deviceId) {
        Organization organization = getOrganizationById(organizationId);
        Device device = getDeviceById(deviceId);
        OrganizationBookmark organizationBookmark = new OrganizationBookmark(organization, device);

        organizationBookmarkJpaRepository.save(organizationBookmark);

        notificationService.subscribeTopic(
                device.getFcmToken(),
                TopicConstants.getOrganizationTopicById(organizationId)
        );

        return OrganizationBookmarkResponse.from(organizationBookmark);
    }

    @Transactional
    public void deleteOrganizationBookmark(Long deviceId, Long organizationId) {
        Device device = getDeviceById(deviceId);

        organizationBookmarkJpaRepository.deleteByOrganizationIdAndDeviceId(organizationId, deviceId);

        notificationService.unsubscribeTopic(
                device.getFcmToken(),
                TopicConstants.getOrganizationTopicById(organizationId)
        );
    }

    private Device getDeviceById(Long deviceId) {
        return deviceJpaRepository.findById(deviceId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 디바이스입니다.", HttpStatus.BAD_REQUEST));
    }

    private Organization getOrganizationById(Long organizationId) {
        return organizationJpaRepository.findById(organizationId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 조직입니다.", HttpStatus.BAD_REQUEST));
    }
}
