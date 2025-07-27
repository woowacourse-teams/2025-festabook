package com.daedan.festabook.organization.service;

import com.daedan.festabook.device.domain.Device;
import com.daedan.festabook.device.infrastructure.DeviceJpaRepository;
import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationNotification;
import com.daedan.festabook.organization.domain.OrganizationNotificationManager;
import com.daedan.festabook.organization.dto.OrganizationNotificationRequest;
import com.daedan.festabook.organization.dto.OrganizationNotificationResponse;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import com.daedan.festabook.organization.infrastructure.OrganizationNotificationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrganizationNotificationService {

    private final OrganizationNotificationJpaRepository organizationNotificationJpaRepository;
    private final DeviceJpaRepository deviceJpaRepository;
    private final OrganizationJpaRepository organizationJpaRepository;
    private final OrganizationNotificationManager organizationNotificationManager;

    @Transactional
    public OrganizationNotificationResponse subscribeOrganizationNotification(Long organizationId,
                                                                              OrganizationNotificationRequest request) {
        validateDuplicatedOrganizationNotification(organizationId, request.deviceId());

        Organization organization = getOrganizationById(organizationId);
        Device device = getDeviceById(request.deviceId());
        OrganizationNotification organizationNotification = new OrganizationNotification(organization, device);
        OrganizationNotification savedOrganizationNotification = organizationNotificationJpaRepository.save(
                organizationNotification);

        organizationNotificationManager.subscribeOrganizationTopic(organizationId, device.getFcmToken());

        return OrganizationNotificationResponse.from(savedOrganizationNotification);
    }

    @Transactional
    public void unsubscribeOrganizationNotification(Long organizationNotificationId) {
        OrganizationNotification organizationNotification = organizationNotificationJpaRepository.findById(
                        organizationNotificationId)
                .orElseGet(() -> null);
        if (organizationNotification == null) {
            return;
        }

        Device device = deviceJpaRepository.findById(organizationNotification.getDevice().getId())
                .orElseGet(() -> null);
        if (device == null) {
            return;
        }

        organizationNotificationJpaRepository.deleteById(organizationNotificationId);
        organizationNotificationManager.unsubscribeOrganizationTopic(
                organizationNotification.getOrganization().getId(),
                device.getFcmToken()
        );
    }

    private void validateDuplicatedOrganizationNotification(Long organizationId, Long deviceId) {
        if (organizationNotificationJpaRepository.existsByOrganizationIdAndDeviceId(organizationId, deviceId)) {
            throw new BusinessException("이미 알림을 구독한 조직입니다.", HttpStatus.BAD_REQUEST);
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
