package com.daedan.festabook.place.service;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceImage;
import com.daedan.festabook.place.dto.PlaceImageRequest;
import com.daedan.festabook.place.dto.PlaceImageResponse;
import com.daedan.festabook.place.infrastructure.PlaceImageJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceImageService {

    private static final int MAX_IMAGE_SEQUENCE = 5;

    private final PlaceJpaRepository placeJpaRepository;
    private final PlaceImageJpaRepository placeImageJpaRepository;

    // TODO: sequence 동시성 해결
    @Transactional
    public PlaceImageResponse addPlaceImage(Long placeId, PlaceImageRequest request) {
        Place place = getPlaceById(placeId);

        int currentMaxSequence = placeImageJpaRepository.findMaxSequenceByPlace(place)
                .orElseGet(() -> 0);
        Integer newSequence = currentMaxSequence + 1;
        validateMaxImageSequence(newSequence);

        PlaceImage placeImage = new PlaceImage(place, request.imageUrl(), newSequence);
        PlaceImage savedPlaceImage = placeImageJpaRepository.save(placeImage);

        return PlaceImageResponse.from(savedPlaceImage);
    }

    private void validateMaxImageSequence(int newSequence) {
        if (newSequence > MAX_IMAGE_SEQUENCE) {
            throw new BusinessException(
                    String.format("플레이스 이미지는 최대 %d개까지 저장할 수 있습니다.", MAX_IMAGE_SEQUENCE),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public void deletePlaceImageByPlaceImageId(Long placeImageId) {
        placeImageJpaRepository.deleteById(placeImageId);
    }

    private Place getPlaceById(Long placeId) {
        return placeJpaRepository.findById(placeId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 플레이스입니다.", HttpStatus.NOT_FOUND));
    }
}
