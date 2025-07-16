package com.daedan.festabook.place.service;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceAnnouncement;
import com.daedan.festabook.place.domain.PlaceImage;
import com.daedan.festabook.place.dto.PlacePreviewResponse;
import com.daedan.festabook.place.dto.PlacePreviewResponses;
import com.daedan.festabook.place.dto.PlaceResponse;
import com.daedan.festabook.place.infrastructure.PlaceAnnouncementJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceImageJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceJpaRepository;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private static final int REPRESENTATIVE_IMAGE_SEQUENCE = 1;

    private final PlaceJpaRepository placeJpaRepository;
    private final PlaceImageJpaRepository placeImageJpaRepository;
    private final PlaceAnnouncementJpaRepository placeAnnouncementJpaRepository;

    public PlacePreviewResponses getAllPlaceByOrganizationId(Long organizationId) {
        List<Place> places = placeJpaRepository.findAllByOrganizationId(organizationId);
        List<PlaceImage> representativeImages =
                placeImageJpaRepository.findAllByPlaceInAndSequence(places, REPRESENTATIVE_IMAGE_SEQUENCE);
        Map<Long, PlaceImage> images = representativeImages.stream()
                .collect(Collectors.toMap(
                        image -> image.getPlace().getId(), // TODO: N+1 문제 해결
                        Function.identity()
                ));
        List<PlacePreviewResponse> responses = places.stream()
                .map(place -> PlacePreviewResponse.from(place, images.get(place.getId())))
                .toList();

        return PlacePreviewResponses.from(responses);
    }

    public PlaceResponse getPlaceByPlaceId(Long placeId) {
        Place place = getPlaceById(placeId);
        List<PlaceImage> placeImages = placeImageJpaRepository.findAllByPlaceId(placeId);
        List<PlaceAnnouncement> placeAnnouncements = placeAnnouncementJpaRepository.findAllByPlaceId(placeId);

        return PlaceResponse.from(place, placeImages, placeAnnouncements);
    }

    private Place getPlaceById(Long placeId) {
        return placeJpaRepository.findById(placeId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 플레이스입니다.", HttpStatus.NOT_FOUND));
    }
}
