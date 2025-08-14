package com.daedan.festabook.place.service;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceImage;
import com.daedan.festabook.place.dto.PlaceImageRequest;
import com.daedan.festabook.place.dto.PlaceImageResponse;
import com.daedan.festabook.place.dto.PlaceImageSequenceUpdateRequest;
import com.daedan.festabook.place.dto.PlaceImageSequenceUpdateResponses;
import com.daedan.festabook.place.infrastructure.PlaceImageJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceJpaRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceImageService {

    private static final int MAX_IMAGE_COUNT = 5;

    private final PlaceJpaRepository placeJpaRepository;
    private final PlaceImageJpaRepository placeImageJpaRepository;

    // TODO: sequence 동시성 해결
    @Transactional
    public PlaceImageResponse addPlaceImage(Long placeId, PlaceImageRequest request) {
        Place place = getPlaceById(placeId);

        int currentMaxSequence = placeImageJpaRepository.findMaxSequenceByPlace(place)
                .orElseGet(() -> 0);
        Integer newSequence = currentMaxSequence + 1;
        validateMaxImageCount(place);

        PlaceImage placeImage = new PlaceImage(place, request.imageUrl(), newSequence);
        PlaceImage savedPlaceImage = placeImageJpaRepository.save(placeImage);

        return PlaceImageResponse.from(savedPlaceImage);
    }

    private void validateMaxImageCount(Place place) {
        Long imageCount = placeImageJpaRepository.countByPlace(place);
        if (imageCount > MAX_IMAGE_COUNT) {
            throw new BusinessException(
                    String.format("플레이스 이미지는 최대 %d개까지 저장할 수 있습니다.", MAX_IMAGE_COUNT),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @Transactional
    public PlaceImageSequenceUpdateResponses updatePlaceImagesSequence(List<PlaceImageSequenceUpdateRequest> requests) {
        // TODO: sequence DTO 값 검증 추가
        List<PlaceImage> placeImages = new ArrayList<>();

        for (PlaceImageSequenceUpdateRequest request : requests) {
            PlaceImage placeImage = getPlaceImageById(request.placeImageId());
            placeImage.updateSequence(request.sequence());
            placeImages.add(placeImage);
        }

        Collections.sort(placeImages);

        return PlaceImageSequenceUpdateResponses.from(placeImages);
    }

    public void deletePlaceImageByPlaceImageId(Long placeImageId) {
        placeImageJpaRepository.deleteById(placeImageId);
    }

    private Place getPlaceById(Long placeId) {
        return placeJpaRepository.findById(placeId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 플레이스입니다.", HttpStatus.NOT_FOUND));
    }

    private PlaceImage getPlaceImageById(Long placeImageId) {
        return placeImageJpaRepository.findById(placeImageId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 플레이스 이미지입니다.", HttpStatus.NOT_FOUND));
    }
}
