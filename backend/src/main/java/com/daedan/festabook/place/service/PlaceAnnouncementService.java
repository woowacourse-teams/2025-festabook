package com.daedan.festabook.place.service;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceAnnouncement;
import com.daedan.festabook.place.dto.PlaceAnnouncementRequest;
import com.daedan.festabook.place.dto.PlaceAnnouncementResponse;
import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.place.domain.PlaceAnnouncement;
import com.daedan.festabook.place.dto.PlaceAnnouncementUpdateRequest;
import com.daedan.festabook.place.dto.PlaceAnnouncementUpdateResponse;
import com.daedan.festabook.place.infrastructure.PlaceAnnouncementJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceAnnouncementService {

    private static final int PLACE_ANNOUNCEMENT_MAX_COUNT = 3;

    private final PlaceJpaRepository placeJpaRepository;
    private final PlaceAnnouncementJpaRepository placeAnnouncementJpaRepository;

    public PlaceAnnouncementResponse createPlaceAnnouncement(Long placeId, PlaceAnnouncementRequest request) {
        Place place = getPlaceById(placeId);

        validatePlaceAnnouncementMaxCount(place);

        PlaceAnnouncement placeAnnouncement = new PlaceAnnouncement(place, request.title(), request.content());
        PlaceAnnouncement savedPlaceAnnouncement = placeAnnouncementJpaRepository.save(placeAnnouncement);

        return PlaceAnnouncementResponse.from(savedPlaceAnnouncement);
    }

    @Transactional
    public PlaceAnnouncementUpdateResponse updatePlaceAnnouncement(
            Long placeAnnouncementId,
            PlaceAnnouncementUpdateRequest request
    ) {
        PlaceAnnouncement placeAnnouncement = getPlaceAnnouncementById(placeAnnouncementId);

        placeAnnouncement.updatePlaceAnnouncement(request.title(), request.content());

        return PlaceAnnouncementUpdateResponse.from(placeAnnouncement);
    }


    public void deleteByPlaceAnnouncementId(Long placeAnnouncementId) {
        placeAnnouncementJpaRepository.deleteById(placeAnnouncementId);
    }

    private Place getPlaceById(Long placeId) {
        return placeJpaRepository.findById(placeId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 플레이스입니다.", HttpStatus.NOT_FOUND));
    }

    private PlaceAnnouncement getPlaceAnnouncementById(Long placeAnnouncementId) {
        return placeAnnouncementJpaRepository.findById(placeAnnouncementId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 플레이스 공지입니다.", HttpStatus.NOT_FOUND));
    }

    private void validatePlaceAnnouncementMaxCount(Place place) {
        Integer placeAnnouncementCount = placeAnnouncementJpaRepository.countByPlace(place);
        if (placeAnnouncementCount >= PLACE_ANNOUNCEMENT_MAX_COUNT) {
            throw new BusinessException(
                    String.format("플레이스 공지사항은 %d개까지 작성이 가능합니다.", PLACE_ANNOUNCEMENT_MAX_COUNT),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}
