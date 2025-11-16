package com.daedan.festabook.festival.service;

import com.daedan.festabook.device.domain.Device;
import com.daedan.festabook.device.infrastructure.DeviceJpaRepository;
import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalNotification;
import com.daedan.festabook.festival.dto.FestivalNotificationReadResponses;
import com.daedan.festabook.festival.dto.FestivalNotificationRequest;
import com.daedan.festabook.festival.dto.FestivalNotificationResponse;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.festival.infrastructure.FestivalNotificationJpaRepository;
import com.daedan.festabook.global.exception.BusinessException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Profile({"dev", "local", "test"})
public class TestFestivalNotificationService {

    private final FestivalNotificationJpaRepository festivalNotificationJpaRepository;
    private final DeviceJpaRepository deviceJpaRepository;
    private final FestivalJpaRepository festivalJpaRepository;

    @Transactional
    public FestivalNotificationResponse subscribeFestivalNotification(
            Long festivalId,
            FestivalNotificationRequest request
    ) {
        FestivalNotification festivalNotification = createFestivalNotification(festivalId, request);
        FestivalNotification savedFestivalNotification = saveFestivalNotification(festivalNotification);
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

        Long festivalId = festivalNotification.getFestival().getId();
    }

    private FestivalNotification createFestivalNotification(Long festivalId, FestivalNotificationRequest request) {
        Festival festival = getFestivalById(festivalId);
        Device device = getDeviceById(request.deviceId());
        return new FestivalNotification(festival, device);
    }

    private FestivalNotification saveFestivalNotification(FestivalNotification festivalNotification) {
        try {
            return festivalNotificationJpaRepository.save(festivalNotification);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("이미 알림을 구독한 축제입니다.", HttpStatus.BAD_REQUEST);
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
