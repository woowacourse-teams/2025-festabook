package com.daedan.festabook.place.service;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceImage;
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

    private final PlaceJpaRepository placeJpaRepository;
    private final PlaceImageJpaRepository placeImageJpaRepository;

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

    private Place getPlaceById(Long placeId) {
        return placeJpaRepository.findById(placeId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 플레이스입니다.", HttpStatus.NOT_FOUND));
    }

    private PlaceImage getPlaceImageById(Long placeImageId) {
        return placeImageJpaRepository.findById(placeImageId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 플레이스 이미지입니다.", HttpStatus.NOT_FOUND));
    }
}
