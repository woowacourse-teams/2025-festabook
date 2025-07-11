package com.daedan.festabook.place.service;

import com.daedan.festabook.place.dto.PlaceAnnouncementResponses;
import com.daedan.festabook.place.infrastructure.PlaceAnnouncementJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceAnnouncementService {

    private final PlaceAnnouncementJpaRepository placeAnnouncementJpaRepository;

    public PlaceAnnouncementResponses getAllPlaceAnnouncementByPlaceId(Long placeId) {
        return PlaceAnnouncementResponses.from(placeAnnouncementJpaRepository.findAllByPlaceId(placeId));
    }
}
