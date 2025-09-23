package com.daedan.festabook.place.service;

import com.daedan.festabook.global.infrastructure.ShuffleManager;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceImage;
import com.daedan.festabook.place.dto.PlacePreviewResponses;
import com.daedan.festabook.place.infrastructure.PlaceImageJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceJpaRepository;
import com.daedan.festabook.timetag.domain.PlaceTimeTag;
import com.daedan.festabook.timetag.domain.TimeTag;
import com.daedan.festabook.timetag.infrastructure.PlaceTimeTagJpaRepository;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlacePreviewService {

    private static final int REPRESENTATIVE_IMAGE_SEQUENCE = 1;

    private final PlaceJpaRepository placeJpaRepository;
    private final PlaceImageJpaRepository placeImageJpaRepository;
    private final ShuffleManager shuffleManager;
    private final PlaceTimeTagJpaRepository placeTimeTagJpaRepository;

    @Transactional(readOnly = true)
    public PlacePreviewResponses getAllPreviewPlaceByFestivalIdSortByRandom(Long festivalId) {
        List<Place> places = getMainPlaces(festivalId);
        List<Place> shuffledPlaces = shuffleManager.getShuffledList(places);
        Map<Long, PlaceImage> placeImages = mapPlaceImagesToIds(shuffledPlaces);
        Map<Long, List<TimeTag>> timeTagsMap = mapTimeTagsToIds(shuffledPlaces);

        return PlacePreviewResponses.from(shuffledPlaces, placeImages, timeTagsMap);
    }

    private List<Place> getMainPlaces(Long festivalId) {
        return placeJpaRepository.findAllByFestivalId(festivalId).stream()
                .filter(Place::isMainPlace)
                .toList();
    }

    private Map<Long, PlaceImage> mapPlaceImagesToIds(List<Place> places) {
        return placeImageJpaRepository.findAllByPlaceInAndSequence(places, REPRESENTATIVE_IMAGE_SEQUENCE).stream()
                .collect(Collectors.toMap(
                        image -> image.getPlace().getId(), // TODO: N+1 문제 해결
                        Function.identity()
                ));
    }

    private Map<Long, List<TimeTag>> mapTimeTagsToIds(List<Place> places) {
        // TODO: N+1 문제 해결
        List<PlaceTimeTag> placeTimeTags = placeTimeTagJpaRepository.findAllByPlaceIn(places);

        return placeTimeTags.stream()
                .collect(Collectors.groupingBy(
                        placeTimeTag -> placeTimeTag.getPlace().getId(),
                        Collectors.mapping(
                                PlaceTimeTag::getTimeTag,
                                Collectors.toList()
                        )
                ));
    }
}
