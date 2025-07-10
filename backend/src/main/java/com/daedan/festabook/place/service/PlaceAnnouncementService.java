package com.daedan.festabook.place.service;

import com.daedan.festabook.place.domain.PlaceAnnouncement;
import com.daedan.festabook.place.dto.PlaceAnnouncementResponses;
import com.daedan.festabook.place.infrastructure.PlaceAnnouncementJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceAnnouncementService {

    private final PlaceAnnouncementJpaRepository placeAnnouncementJpaRepository;

    public PlaceAnnouncementResponses findAllPlaceAnnouncementByPlaceId(Long placeId) {
        List<PlaceAnnouncement> placeAnnouncements = placeAnnouncementJpaRepository.findAllByPlaceId(placeId);
        return PlaceAnnouncementResponses.from(placeAnnouncements);
    }
}
