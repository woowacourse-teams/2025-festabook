package com.daedan.festabook.place.service;

import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceImage;
import com.daedan.festabook.place.dto.PlaceEtcPreviewResponses;
import com.daedan.festabook.place.dto.PlaceMainPreviewResponses;
import com.daedan.festabook.place.infrastructure.PlaceImageJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceJpaRepository;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlacePreviewService {

    private static final int REPRESENTATIVE_IMAGE_SEQUENCE = 1;

    private final PlaceJpaRepository placeJpaRepository;
    private final PlaceImageJpaRepository placeImageJpaRepository;

    public PlaceMainPreviewResponses getAllPreviewMainPlaceByFestivalId(Long festivalId) {
        List<Place> mainPlaces = getMainPlaces(festivalId);

        Map<Long, PlaceImage> placeImages = mapPlaceImagesToIds(mainPlaces);

        return PlaceMainPreviewResponses.from(mainPlaces, placeImages);
    }

    public PlaceEtcPreviewResponses getAllPreviewEtcPlaceByFestivalId(Long festivalId) {
        List<Place> etcPlaces = getEtcPlaces(festivalId);
        return PlaceEtcPreviewResponses.from(etcPlaces);
    }

    private List<Place> getMainPlaces(Long festivalId) {
        return placeJpaRepository.findAllByFestivalId(festivalId).stream()
                .filter(Place::isMainPlace)
                .toList();
    }

    private List<Place> getEtcPlaces(Long festivalId) {
        return placeJpaRepository.findAllByFestivalId(festivalId).stream()
                .filter(Place::isEtcPlace)
                .toList();
    }

    private Map<Long, PlaceImage> mapPlaceImagesToIds(List<Place> places) {
        return placeImageJpaRepository.findAllByPlaceInAndSequence(places, REPRESENTATIVE_IMAGE_SEQUENCE).stream()
                .collect(Collectors.toMap(
                        image -> image.getPlace().getId(), // TODO: N+1 문제 해결
                        Function.identity()
                ));
    }
}
