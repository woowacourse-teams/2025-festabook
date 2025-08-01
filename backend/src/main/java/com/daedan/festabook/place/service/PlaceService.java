package com.daedan.festabook.place.service;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceAnnouncement;
import com.daedan.festabook.place.domain.PlaceDetail;
import com.daedan.festabook.place.domain.PlaceImage;
import com.daedan.festabook.place.dto.PlaceRequest;
import com.daedan.festabook.place.dto.PlaceResponse;
import com.daedan.festabook.place.dto.PlaceResponses;
import com.daedan.festabook.place.dto.PlaceUpdateRequest;
import com.daedan.festabook.place.infrastructure.PlaceAnnouncementJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceDetailJpaRepository;
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
    private final PlaceDetailJpaRepository placeDetailJpaRepository;
    private final OrganizationJpaRepository organizationJpaRepository;
    private final PlaceFavoriteJpaRepository placeFavoriteJpaRepository;
    private final PlaceAnnouncementJpaRepository placeAnnouncementJpaRepository;

    public PlaceResponse createPlace(Long organizationId, PlaceRequest request) {
        Organization organization = getOrganizationById(organizationId);

        Place notSavedPlace = request.toPlace(organization);
        Place savedPlace = placeJpaRepository.save(notSavedPlace);

        return PlaceResponse.from(savedPlace);
    }

    @Transactional(readOnly = true)
    public PlaceResponses getAllPlaceByOrganizationId(Long organizationId) {
        return PlaceResponses.from(
                placeJpaRepository.findAllByOrganizationId(organizationId).stream()
                        .map(this::convertPlaceResponse)
                        .toList()
        );
    }

    @Transactional(readOnly = true)
    public PlaceResponse getPlaceWithDetailByPlaceId(Long placeId) {
        Place place = getPlaceByPlaceId(placeId);
        return convertPlaceResponse(place);
    }

    private PlaceResponse convertPlaceResponse(Place place) {
        if (!placeDetailJpaRepository.existsByPlace(place)) {
            return PlaceResponse.from(place);
        }

        Long placeId = place.getId();
        PlaceDetail placeDetail = getPlaceDetailByPlaceId(placeId);
        List<PlaceImage> placeImages = placeImageJpaRepository.findAllByPlaceIdOrderBySequenceAsc(placeId);
        List<PlaceAnnouncement> placeAnnouncements = placeAnnouncementJpaRepository.findAllByPlaceId(placeId);
        return PlaceResponse.fromWithDetail(place, placeDetail, placeImages, placeAnnouncements);
    }

    @Transactional
    public void deleteByPlaceId(Long placeId) {
        placeDetailJpaRepository.deleteByPlaceId(placeId);
        placeImageJpaRepository.deleteAllByPlaceId(placeId);
        placeAnnouncementJpaRepository.deleteAllByPlaceId(placeId);
        placeFavoriteJpaRepository.deleteAllByPlaceId(placeId);
        placeJpaRepository.deleteById(placeId);
    }

    @Transactional
    public void updatePlace(Long placeId, PlaceUpdateRequest request) {
        Place place = getPlaceByPlaceId(placeId);
        place.updateCategory(request.placeCategory());

        PlaceDetail newPlaceDetail = request.toPlaceDetail(place);
        PlaceDetail existingDetail = placeDetailJpaRepository.findByPlaceId(placeId).orElse(null);

        updatePlaceDetail(existingDetail, newPlaceDetail);
    }

    private void updatePlaceDetail(PlaceDetail existingDetail, PlaceDetail newPlaceDetail) {
        if (existingDetail == null) {
            placeDetailJpaRepository.save(newPlaceDetail);
            return;
        }

        existingDetail.update(newPlaceDetail);
    }

    // TODO: ExceptionHandler 등록 후 예외 변경
    private Place getPlaceByPlaceId(Long placeId) {
        return placeJpaRepository.findById(placeId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 플레이스입니다.", HttpStatus.NOT_FOUND));
    }

    private PlaceDetail getPlaceDetailByPlaceId(Long placeId) {
        return placeDetailJpaRepository.findByPlaceId(placeId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 플레이스 세부 정보입니다.", HttpStatus.NOT_FOUND));
    }

    private Organization getOrganizationById(Long organizationId) {
        return organizationJpaRepository.findById(organizationId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 조직입니다.", HttpStatus.BAD_REQUEST));
    }
}
