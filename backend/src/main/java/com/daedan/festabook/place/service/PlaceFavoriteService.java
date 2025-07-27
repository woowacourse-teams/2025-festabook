package com.daedan.festabook.place.service;

import com.daedan.festabook.device.domain.Device;
import com.daedan.festabook.device.infrastructure.DeviceJpaRepository;
import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceFavorite;
import com.daedan.festabook.place.dto.PlaceFavoriteRequest;
import com.daedan.festabook.place.dto.PlaceFavoriteResponse;
import com.daedan.festabook.place.infrastructure.PlaceFavoriteJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceFavoriteService {

    private final PlaceFavoriteJpaRepository placeFavoriteJpaRepository;
    private final DeviceJpaRepository deviceJpaRepository;
    private final PlaceJpaRepository placeJpaRepository;

    public PlaceFavoriteResponse createPlaceFavorite(Long placeId, PlaceFavoriteRequest request) {
        validateDuplicatedPlaceFavorite(placeId, request.deviceId());

        Place place = getPlaceById(placeId);
        Device device = getDeviceById(request.deviceId());
        PlaceFavorite placeFavorite = new PlaceFavorite(place, device);
        PlaceFavorite savedPlaceFavorite = placeFavoriteJpaRepository.save(placeFavorite);

        return PlaceFavoriteResponse.from(savedPlaceFavorite);
    }

    @Transactional
    public void deletePlaceFavorite(Long placeFavoriteId) {
        PlaceFavorite placeFavorite = placeFavoriteJpaRepository.findById(placeFavoriteId)
                .orElseGet(() -> null);
        if (placeFavorite == null) {
            return;
        }

        Device device = deviceJpaRepository.findById(placeFavorite.getDevice().getId())
                .orElseGet(() -> null);
        if (device == null) {
            return;
        }

        placeFavoriteJpaRepository.deleteById(placeFavoriteId);
    }

    private void validateDuplicatedPlaceFavorite(Long placeId, Long deviceId) {
        if (placeFavoriteJpaRepository.existsByPlaceIdAndDeviceId(placeId, deviceId)) {
            throw new BusinessException("이미 즐겨찾기한 플레이스입니다.", HttpStatus.BAD_REQUEST);
        }
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
