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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceImageService {

    private static final Long MAX_IMAGE_COUNT = 5L;

    private final PlaceJpaRepository placeJpaRepository;
    private final PlaceImageJpaRepository placeImageJpaRepository;

    // TODO: sequence 동시성 해결
    @Transactional
    public PlaceImageResponse addPlaceImage(Long festivalId, Long placeId, PlaceImageRequest request) {
        Place place = getPlaceById(placeId);
        validatePlaceBelongsToFestival(place, festivalId);

        int currentMaxSequence = placeImageJpaRepository.findMaxSequenceByPlace(place)
                .orElseGet(() -> 0);
        Integer newSequence = currentMaxSequence + 1;
        validateMaxImageCount(place);

        PlaceImage placeImage = new PlaceImage(place, request.imageUrl(), newSequence);
        PlaceImage savedPlaceImage = placeImageJpaRepository.save(placeImage);

        return PlaceImageResponse.from(savedPlaceImage);
    }

    // TODO: sequence DTO 값 검증 추가
    @Transactional
    public PlaceImageSequenceUpdateResponses updatePlaceImagesSequence(
            Long festivalId,
            List<PlaceImageSequenceUpdateRequest> requests
    ) {
        Map<Long, Integer> sequenceMap = requests.stream()
                .collect(Collectors.toMap(
                        PlaceImageSequenceUpdateRequest::placeImageId,
                        PlaceImageSequenceUpdateRequest::sequence
                ));

        List<PlaceImage> placeImages = placeImageJpaRepository.findAllById(sequenceMap.keySet());

        validateAllPlaceImagesExist(placeImages, sequenceMap);
        validateAllBelongsToFestival(placeImages, festivalId);

        for (PlaceImage placeImage : placeImages) {
            placeImage.updateSequence(sequenceMap.get(placeImage.getId()));
        }

        Collections.sort(placeImages);

        return PlaceImageSequenceUpdateResponses.from(placeImages);
    }

    @Transactional
    public void deletePlaceImageByPlaceImageId(Long festivalId, Long placeImageId) {
        PlaceImage placeImage = getPlaceImageById(placeImageId);
        validatePlaceImageBelongsToFestival(placeImage, festivalId);

        placeImageJpaRepository.deleteById(placeImageId);
    }

    private void validateMaxImageCount(Place place) {
        Long imageCount = placeImageJpaRepository.countByPlace(place);
        if (imageCount >= MAX_IMAGE_COUNT) {
            throw new BusinessException(
                    String.format("플레이스 이미지는 최대 %d개까지 저장할 수 있습니다.", MAX_IMAGE_COUNT),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private Place getPlaceById(Long placeId) {
        return placeJpaRepository.findById(placeId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 플레이스입니다.", HttpStatus.NOT_FOUND));
    }

    private PlaceImage getPlaceImageById(Long placeImageId) {
        return placeImageJpaRepository.findById(placeImageId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 플레이스 이미지입니다.", HttpStatus.NOT_FOUND));
    }

    private void validatePlaceImageBelongsToFestival(PlaceImage placeImage, Long festivalId) {
        if (!placeImage.isFestivalIdEqualTo(festivalId)) {
            throw new BusinessException("해당 축제의 플레이스 이미지가 아닙니다.", HttpStatus.FORBIDDEN);
        }
    }

    private void validatePlaceBelongsToFestival(Place place, Long festivalId) {
        if (!place.isFestivalIdEqualTo(festivalId)) {
            throw new BusinessException("해당 축제의 플레이스가 아닙니다.", HttpStatus.FORBIDDEN);
        }
    }

    private void validateAllPlaceImagesExist(List<PlaceImage> placeImages, Map<Long, Integer> sequenceMap) {
        if (placeImages.size() != sequenceMap.size()) {
            throw new BusinessException("존재하지 않는 플레이스 이미지가 있습니다.", HttpStatus.NOT_FOUND);
        }
    }

    private void validateAllBelongsToFestival(List<PlaceImage> placeImages, Long festivalId) {
        boolean anyMismatch = placeImages.stream()
                .anyMatch(placeImage -> !placeImage.isFestivalIdEqualTo(festivalId));

        if (anyMismatch) {
            throw new BusinessException("해당 축제의 플레이스 이미지가 아닙니다.", HttpStatus.FORBIDDEN);
        }
    }
}
