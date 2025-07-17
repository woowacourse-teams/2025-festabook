package com.daedan.festabook.place.service;

import com.daedan.festabook.device.domain.Device;
import com.daedan.festabook.device.infrastructure.DeviceJpaRepository;
import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.notification.constants.TopicConstants;
import com.daedan.festabook.notification.service.NotificationService;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceBookmark;
import com.daedan.festabook.place.dto.PlaceBookmarkResponse;
import com.daedan.festabook.place.infrastructure.PlaceBookmarkJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceBookmarkService {

    private final PlaceBookmarkJpaRepository placeBookmarkJpaRepository;
    private final DeviceJpaRepository deviceJpaRepository;
    private final PlaceJpaRepository placeJpaRepository;
    private final NotificationService notificationService;

    @Transactional
    public PlaceBookmarkResponse createPlaceBookmark(Long placeId, Long deviceId) {
        Place place = getPlaceById(placeId);
        Device device = getDeviceById(deviceId);
        PlaceBookmark placeBookmark = new PlaceBookmark(place, device);

        placeBookmarkJpaRepository.save(placeBookmark);

        notificationService.subscribeTopic(device.getFcmToken(), TopicConstants.getPlaceTopicById(placeId));

        return PlaceBookmarkResponse.from(placeBookmark);
    }

    @Transactional
    public void deletePlaceBookmark(Long deviceId, Long placeId) {
        Device device = getDeviceById(deviceId);

        placeBookmarkJpaRepository.deleteByPlaceIdAndDeviceId(placeId, deviceId);

        notificationService.unsubscribeTopic(device.getFcmToken(), TopicConstants.getPlaceTopicById(placeId));
    }

    private Device getDeviceById(Long deviceId) {
        return deviceJpaRepository.findById(deviceId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 디바이스입니다.", HttpStatus.BAD_REQUEST));
    }

    private Place getPlaceById(Long placeId) {
        return placeJpaRepository.findById(placeId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 플레이스입니다.", HttpStatus.BAD_REQUEST));
    }
}
