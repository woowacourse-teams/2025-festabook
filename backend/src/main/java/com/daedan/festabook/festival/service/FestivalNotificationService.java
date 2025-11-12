package com.daedan.festabook.festival.service;

import com.daedan.festabook.device.domain.Device;
import com.daedan.festabook.device.infrastructure.DeviceJpaRepository;
import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalNotification;
import com.daedan.festabook.festival.domain.FestivalNotificationManager;
import com.daedan.festabook.festival.dto.FestivalNotificationReadResponses;
import com.daedan.festabook.festival.dto.FestivalNotificationRequest;
import com.daedan.festabook.festival.dto.FestivalNotificationResponse;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.festival.infrastructure.FestivalNotificationJpaRepository;
import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.global.exception.UniqueDuplicateDataException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FestivalNotificationService {

    private final FestivalNotificationJpaRepository festivalNotificationJpaRepository;
    private final DeviceJpaRepository deviceJpaRepository;
    private final FestivalJpaRepository festivalJpaRepository;
    private final FestivalNotificationManager festivalNotificationManager;

    @Transactional
    public FestivalNotificationResponse subscribeFestivalNotification(
            Long festivalId,
            FestivalNotificationRequest request
    ) {
        FestivalNotification festivalNotification = createFestivalNotification(festivalId, request);
        FestivalNotification savedFestivalNotification = saveFestivalNotificationOrFailDuplicated(festivalNotification);

        String fcmToken = savedFestivalNotification.getDevice().getFcmToken();
        festivalNotificationManager.subscribeFestivalTopic(festivalId, fcmToken);

        return FestivalNotificationResponse.from(savedFestivalNotification);
    }

    @Transactional
    public FestivalNotificationResponse subscribeAndroidFestivalNotification(
            Long festivalId,
            FestivalNotificationRequest request
    ) {
        FestivalNotification festivalNotification = createFestivalNotification(festivalId, request);
        FestivalNotification savedFestivalNotification = saveFestivalNotificationOrFailDuplicated(festivalNotification);

        String fcmToken = savedFestivalNotification.getDevice().getFcmToken();
        festivalNotificationManager.subscribeAndroidFestivalTopic(festivalId, fcmToken);

        return FestivalNotificationResponse.from(savedFestivalNotification);
    }

    @Transactional
    public FestivalNotificationResponse subscribeIosFestivalNotification(
            Long festivalId,
            FestivalNotificationRequest request
    ) {
        FestivalNotification festivalNotification = createFestivalNotification(festivalId, request);
        FestivalNotification savedFestivalNotification = saveFestivalNotificationOrFailDuplicated(festivalNotification);

        String fcmToken = savedFestivalNotification.getDevice().getFcmToken();
        festivalNotificationManager.subscribeIosFestivalTopic(festivalId, fcmToken);

        return FestivalNotificationResponse.from(savedFestivalNotification);
    }

    @Transactional(readOnly = true)
    public FestivalNotificationReadResponses getAllFestivalNotificationByDeviceId(Long deviceId) {
        Device device = getDeviceById(deviceId);
        List<FestivalNotification> festivalNotifications = festivalNotificationJpaRepository.findAllWithFestivalByDeviceId(
                device.getId()
        );

        return FestivalNotificationReadResponses.from(festivalNotifications);
    }

    @Transactional
    public void unsubscribeFestivalNotification(Long festivalNotificationId) {
        FestivalNotification festivalNotification = festivalNotificationJpaRepository
                .findById(festivalNotificationId)
                .orElse(null);
        if (festivalNotification == null) {
            return;
        }

        Device device = deviceJpaRepository.findById(festivalNotification.getDevice().getId())
                .orElse(null);
        if (device == null) {
            return;
        }

        festivalNotificationJpaRepository.deleteById(festivalNotificationId);
        festivalNotificationManager.unsubscribeFestivalTopic(
                festivalNotification.getFestival().getId(),
                device.getFcmToken()
        );
    }

    private FestivalNotification createFestivalNotification(Long festivalId, FestivalNotificationRequest request) {
        Festival festival = getFestivalById(festivalId);
        Device device = getDeviceById(request.deviceId());
        return new FestivalNotification(festival, device);
    }

    private FestivalNotification saveFestivalNotificationOrFailDuplicated(FestivalNotification festivalNotification) {
        try {
            return festivalNotificationJpaRepository.save(festivalNotification);
        } catch (DataIntegrityViolationException e) {
            throw new UniqueDuplicateDataException(FestivalNotification.class, e.getMessage());
        }
    }

    private Device getDeviceById(Long deviceId) {
        return deviceJpaRepository.findById(deviceId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 디바이스입니다.", HttpStatus.BAD_REQUEST));
    }

    private Festival getFestivalById(Long festivalId) {
        return festivalJpaRepository.findById(festivalId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 축제입니다.", HttpStatus.BAD_REQUEST));
    }
}
