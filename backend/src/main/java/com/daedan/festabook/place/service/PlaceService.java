package com.daedan.festabook.place.service;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceAnnouncement;
import com.daedan.festabook.place.domain.PlaceImage;
import com.daedan.festabook.place.dto.PlaceRequest;
import com.daedan.festabook.place.dto.PlaceResponse;
import com.daedan.festabook.place.dto.PlaceResponses;
import com.daedan.festabook.place.infrastructure.PlaceAnnouncementJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceFavoriteJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceImageJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceJpaRepository placeJpaRepository;
    private final PlaceImageJpaRepository placeImageJpaRepository;
    private final FestivalJpaRepository festivalJpaRepository;
    private final PlaceFavoriteJpaRepository placeFavoriteJpaRepository;
    private final PlaceAnnouncementJpaRepository placeAnnouncementJpaRepository;

    public PlaceResponse createPlace(Long festivalId, PlaceRequest request) {
        Festival festival = getFestivalById(festivalId);

        Place notSavedPlace = request.toPlace(festival);
        Place savedPlace = placeJpaRepository.save(notSavedPlace);

        return PlaceResponse.from(savedPlace);
    }

    @Transactional(readOnly = true)
    public PlaceResponses getAllPlaceByFestivalId(Long festivalId) {
        return PlaceResponses.from(
                placeJpaRepository.findAllByFestivalId(festivalId).stream()
                        .map(this::convertPlaceToResponse)
                        .toList()
        );
    }

    @Transactional(readOnly = true)
    public PlaceResponse getPlaceByPlaceId(Long placeId) {
        Place place = getPlaceById(placeId);
        return convertPlaceToResponse(place);
    }

    private PlaceResponse convertPlaceToResponse(Place place) {
        if (place.isMainPlace()) {
            Long placeId = place.getId();
            List<PlaceImage> placeImages = placeImageJpaRepository.findAllByPlaceIdOrderBySequenceAsc(placeId);
            List<PlaceAnnouncement> placeAnnouncements = placeAnnouncementJpaRepository.findAllByPlaceId(placeId);
            return PlaceResponse.from(place, placeImages, placeAnnouncements);
        }

        return PlaceResponse.from(place, List.of(), List.of());
    }

    @Transactional
    public void deleteByPlaceId(Long placeId) {
        placeImageJpaRepository.deleteAllByPlaceId(placeId);
        placeAnnouncementJpaRepository.deleteAllByPlaceId(placeId);
        placeFavoriteJpaRepository.deleteAllByPlaceId(placeId);
        placeJpaRepository.deleteById(placeId);
    }

    // TODO: ExceptionHandler 등록 후 예외 변경
    private Place getPlaceById(Long placeId) {
        return placeJpaRepository.findById(placeId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 플레이스입니다.", HttpStatus.NOT_FOUND));
    }

    private Festival getFestivalById(Long festivalId) {
        return festivalJpaRepository.findById(festivalId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 축제입니다.", HttpStatus.BAD_REQUEST));
    }
}
