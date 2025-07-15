package com.daedan.festabook.place.service;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceAnnouncement;
import com.daedan.festabook.place.domain.PlaceImage;
import com.daedan.festabook.place.dto.PlaceListResponses;
import com.daedan.festabook.place.dto.PlaceResponse;
import com.daedan.festabook.place.infrastructure.PlaceAnnouncementJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceImageJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceJpaRepository placeJpaRepository;
    private final PlaceAnnouncementJpaRepository placeAnnouncementJpaRepository;
    private final PlaceImageJpaRepository placeImageJpaRepository;

    public PlaceListResponses getAllPlaceByOrganizationId(Long organizationId) {
        List<Place> places = placeJpaRepository.findAllByOrganizationId(organizationId);
        List<Long> placeIds = places.stream()
                .map(Place::getId)
                .toList();
        List<PlaceImage> images = placeImageJpaRepository.findAllByPlaceIdIn((placeIds));

        return PlaceListResponses.from(places, images);
    }

    public PlaceResponse getPlaceById(Long placeId) {
        Place place = findPlaceByPlaceId(placeId);
        List<PlaceImage> placeImages = placeImageJpaRepository.findAllByPlaceId(placeId);
        List<PlaceAnnouncement> placeAnnouncements = placeAnnouncementJpaRepository.findAllByPlaceId(placeId);

        return PlaceResponse.from(place, placeImages, placeAnnouncements);
    }

    private Place findPlaceByPlaceId(Long placeId) {
        return placeJpaRepository.findById(placeId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 플레이스입니다.", HttpStatus.NOT_FOUND));
    }
}
