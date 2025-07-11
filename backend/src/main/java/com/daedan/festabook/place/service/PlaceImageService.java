package com.daedan.festabook.place.service;

import com.daedan.festabook.place.domain.PlaceImage;
import com.daedan.festabook.place.dto.PlaceImageResponses;
import com.daedan.festabook.place.infrastructure.PlaceImageJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceImageService {

    private final PlaceImageJpaRepository placeImageJpaRepository;

    @Transactional(readOnly = true)
    public PlaceImageResponses findAllPlaceImageByPlaceId(Long placeId) {
        List<PlaceImage> placeImages = placeImageJpaRepository.findAllByPlaceId(placeId);
        return PlaceImageResponses.from(placeImages);
    }
}
