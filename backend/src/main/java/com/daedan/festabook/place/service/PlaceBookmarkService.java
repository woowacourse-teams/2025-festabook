package com.daedan.festabook.place.service;

import com.daedan.festabook.device.domain.Device;
import com.daedan.festabook.device.infrastructure.DeviceJpaRepository;
import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceBookmark;
import com.daedan.festabook.place.domain.PlaceNotificationManager;
import com.daedan.festabook.place.dto.PlaceBookmarkRequest;
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
    private final PlaceNotificationManager placeNotificationManager;

    @Transactional
    public PlaceBookmarkResponse createPlaceBookmark(Long placeId, PlaceBookmarkRequest request) {
        Place place = getPlaceById(placeId);
        Device device = getDeviceById(request.deviceId());
        PlaceBookmark placeBookmark = new PlaceBookmark(place, device);
        placeBookmarkJpaRepository.save(placeBookmark);

        placeNotificationManager.subscribePlaceTopic(placeId, device.getFcmToken());

        return PlaceBookmarkResponse.from(placeBookmark);
    }

    @Transactional
    public void deletePlaceBookmark(Long placeBookmarkId) {
        placeBookmarkJpaRepository.findById(placeBookmarkId)
                .ifPresent(placeBookmark -> {
                    deviceJpaRepository.findById(placeBookmark.getDevice().getId())
                            .ifPresent(device -> {
                                placeNotificationManager.unsubscribePlaceTopic(
                                        placeBookmark.getPlace().getId(),
                                        device.getFcmToken()
                                );
                            });
                });
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
