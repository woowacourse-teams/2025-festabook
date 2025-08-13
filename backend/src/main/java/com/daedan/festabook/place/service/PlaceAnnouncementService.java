package com.daedan.festabook.place.service;

import com.daedan.festabook.place.infrastructure.PlaceAnnouncementJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceAnnouncementService {

    private final PlaceJpaRepository placeJpaRepository;
    private final PlaceAnnouncementJpaRepository placeAnnouncementJpaRepository;

    public void deleteByPlaceAnnouncementId(Long placeAnnouncementId) {
        placeAnnouncementJpaRepository.deleteById(placeAnnouncementId);
    }
}
