package com.daedan.festabook.place.service;

import com.daedan.festabook.festival.domain.Coordinate;
import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.dto.PlaceCoordinateRequest;
import com.daedan.festabook.place.dto.PlaceCoordinateResponse;
import com.daedan.festabook.place.dto.PlaceGeographyResponses;
import com.daedan.festabook.place.infrastructure.PlaceJpaRepository;
import com.daedan.festabook.timetag.domain.PlaceTimeTag;
import com.daedan.festabook.timetag.domain.TimeTag;
import com.daedan.festabook.timetag.infrastructure.PlaceTimeTagJpaRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceGeographyService {

    private final PlaceJpaRepository placeJpaRepository;
    private final PlaceTimeTagJpaRepository placeTimeTagJpaRepository;

    @Transactional(readOnly = true)
    public PlaceGeographyResponses getAllPlaceGeographyByFestivalId(Long festivalId) {
        List<Place> places = placeJpaRepository.findAllByFestivalId(festivalId);
        Map<Long, List<TimeTag>> timeTagsMap = mapTimeTagsToIds(places);

        return PlaceGeographyResponses.from(places, timeTagsMap);
    }

    @Transactional
    public PlaceCoordinateResponse updatePlaceCoordinate(
            Long festivalId,
            Long placeId,
            PlaceCoordinateRequest request
    ) {
        Place place = getPlaceById(placeId);
        validatePlaceBelongsToFestival(place, festivalId);
        Coordinate coordinate = request.toCoordinate();

        place.updateCoordinate(coordinate);
        return PlaceCoordinateResponse.from(place);
    }

    private Place getPlaceById(Long placeId) {
        return placeJpaRepository.findById(placeId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 플레이스입니다.", HttpStatus.NOT_FOUND));
    }

    private void validatePlaceBelongsToFestival(Place place, Long festivalId) {
        if (!place.isFestivalIdEqualTo(festivalId)) {
            throw new BusinessException("해당 축제의 플레이스가 아닙니다.", HttpStatus.FORBIDDEN);
        }
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
