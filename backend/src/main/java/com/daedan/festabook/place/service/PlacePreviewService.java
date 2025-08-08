package com.daedan.festabook.place.service;

import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceDetail;
import com.daedan.festabook.place.domain.PlaceImage;
import com.daedan.festabook.place.dto.PlacePreviewResponses;
import com.daedan.festabook.place.infrastructure.PlaceDetailJpaRepository;
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
    private final PlaceDetailJpaRepository placeDetailJpaRepository;

    public PlacePreviewResponses getAllPreviewPlaceByFestivalId(Long festivalId) {
        List<Place> places = getPlacesHasDetail(festivalId);

        Map<Long, PlaceImage> placeImages = mapPlaceImagesToIds(places);
        Map<Long, PlaceDetail> placeDetails = mapPlaceDetailsToIds(places);

        return PlacePreviewResponses.from(places, placeDetails, placeImages);
    }

    private List<Place> getPlacesHasDetail(Long festivalId) {
        return placeJpaRepository.findAllByFestivalId(festivalId).stream()
                .filter(Place::hasDetail)
                .toList();
    }

    private Map<Long, PlaceImage> mapPlaceImagesToIds(List<Place> places) {
        return placeImageJpaRepository.findAllByPlaceInAndSequence(places, REPRESENTATIVE_IMAGE_SEQUENCE).stream()
                .collect(Collectors.toMap(
                        image -> image.getPlace().getId(), // TODO: N+1 문제 해결
                        Function.identity()
                ));
    }

    private Map<Long, PlaceDetail> mapPlaceDetailsToIds(List<Place> places) {
        return placeDetailJpaRepository.findAllByPlaceIn(places).stream()
                .collect(Collectors.toMap(
                        detail -> detail.getPlace().getId(), // TODO: N+1 문제 해결
                        Function.identity()
                ));
    }
}
