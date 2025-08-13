package com.daedan.festabook.place.service;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.place.domain.PlaceAnnouncement;
import com.daedan.festabook.place.dto.PlaceAnnouncementUpdateRequest;
import com.daedan.festabook.place.dto.PlaceAnnouncementUpdateResponse;
import com.daedan.festabook.place.infrastructure.PlaceAnnouncementJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceAnnouncementService {

    private final PlaceJpaRepository placeJpaRepository;
    private final PlaceAnnouncementJpaRepository placeAnnouncementJpaRepository;

    public PlaceAnnouncementUpdateResponse updatePlaceAnnouncement(
            Long placeAnnouncementId,
            PlaceAnnouncementUpdateRequest request
    ) {
        PlaceAnnouncement placeAnnouncement = getPlaceAnnouncementById(placeAnnouncementId);

        placeAnnouncement.updatePlaceAnnouncement(request.title(), request.content());

        return PlaceAnnouncementUpdateResponse.from(placeAnnouncement);
    }

    private PlaceAnnouncement getPlaceAnnouncementById(Long placeAnnouncementId) {
        return placeAnnouncementJpaRepository.findById(placeAnnouncementId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 플레이스 공지입니다.", HttpStatus.NOT_FOUND));
    }
}
