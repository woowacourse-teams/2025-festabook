package com.daedan.festabook.place.service;

import com.daedan.festabook.place.domain.PlaceAnnouncement;
import com.daedan.festabook.place.dto.PlaceAnnouncementResponses;
import com.daedan.festabook.place.infrastructure.PlaceAnnouncementJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceAnnouncementService {

    private final PlaceAnnouncementJpaRepository placeAnnouncementJpaRepository;

    @Transactional(readOnly = true)
    public PlaceAnnouncementResponses findAllPlaceAnnouncementByPlaceId(Long placeId) {
        List<PlaceAnnouncement> placeAnnouncements = placeAnnouncementJpaRepository.findAllByPlaceId(placeId);
        return PlaceAnnouncementResponses.from(placeAnnouncements);
    }
}
