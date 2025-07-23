package com.daedan.festabook.organization.service;

import com.daedan.festabook.device.domain.Device;
import com.daedan.festabook.device.infrastructure.DeviceJpaRepository;
import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationBookmark;
import com.daedan.festabook.organization.domain.OrganizationNotificationManager;
import com.daedan.festabook.organization.dto.OrganizationBookmarkRequest;
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
    private final OrganizationNotificationManager organizationNotificationManager;

    @Transactional
    public OrganizationBookmarkResponse createOrganizationBookmark(Long organizationId,
                                                                   OrganizationBookmarkRequest request) {
        validateDuplicatedOrganizationBookmark(organizationId, request.deviceId());

        Organization organization = getOrganizationById(organizationId);
        Device device = getDeviceById(request.deviceId());
        OrganizationBookmark organizationBookmark = new OrganizationBookmark(organization, device);
        OrganizationBookmark savedOrganizationBookmark = organizationBookmarkJpaRepository.save(organizationBookmark);

        organizationNotificationManager.subscribeOrganizationTopic(organizationId, device.getFcmToken());

        return OrganizationBookmarkResponse.from(savedOrganizationBookmark);
    }

    @Transactional
    public void deleteOrganizationBookmark(Long organizationBookmarkId) {
        OrganizationBookmark organizationBookmark = organizationBookmarkJpaRepository.findById(organizationBookmarkId)
                .orElseGet(() -> null);
        if (organizationBookmark == null) {
            return;
        }

        Device device = deviceJpaRepository.findById(organizationBookmark.getDevice().getId())
                .orElseGet(() -> null);
        if (device == null) {
            return;
        }

        organizationBookmarkJpaRepository.deleteById(organizationBookmarkId);
        organizationNotificationManager.unsubscribeOrganizationTopic(
                organizationBookmark.getOrganization().getId(),
                device.getFcmToken()
        );
    }

    private void validateDuplicatedOrganizationBookmark(Long organizationId, Long deviceId) {
        if (organizationBookmarkJpaRepository.existsByOrganizationIdAndDeviceId(organizationId, deviceId)) {
            throw new BusinessException("이미 북마크한 조직입니다.", HttpStatus.BAD_REQUEST);
        }
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
