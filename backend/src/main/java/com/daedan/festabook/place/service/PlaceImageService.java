package com.daedan.festabook.place.service;

import com.daedan.festabook.place.dto.PlaceImageResponses;
import com.daedan.festabook.place.infrastructure.PlaceImageJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceImageService {

    private final PlaceImageJpaRepository placeImageJpaRepository;

    public PlaceImageResponses getAllPlaceImageByPlaceId(Long placeId) {
        return PlaceImageResponses.from(placeImageJpaRepository.findAllByPlaceId(placeId));
    }
}
