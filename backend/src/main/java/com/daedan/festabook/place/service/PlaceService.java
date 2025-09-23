package com.daedan.festabook.place.service;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceAnnouncement;
import com.daedan.festabook.place.domain.PlaceImage;
import com.daedan.festabook.place.dto.EtcPlaceUpdateRequest;
import com.daedan.festabook.place.dto.EtcPlaceUpdateResponse;
import com.daedan.festabook.place.dto.MainPlaceUpdateRequest;
import com.daedan.festabook.place.dto.MainPlaceUpdateResponse;
import com.daedan.festabook.place.dto.PlaceCreateRequest;
import com.daedan.festabook.place.dto.PlaceCreateResponse;
import com.daedan.festabook.place.dto.PlaceResponse;
import com.daedan.festabook.place.dto.PlaceResponses;
import com.daedan.festabook.place.infrastructure.PlaceAnnouncementJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceFavoriteJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceImageJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceJpaRepository;
import com.daedan.festabook.timetag.domain.PlaceTimeTag;
import com.daedan.festabook.timetag.domain.TimeTag;
import com.daedan.festabook.timetag.infrastructure.PlaceTimeTagJpaRepository;
import com.daedan.festabook.timetag.infrastructure.TimeTagJpaRepository;
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
    private final PlaceTimeTagJpaRepository placeTimeTagJpaRepository;
    private final TimeTagJpaRepository timeTagJpaRepository;

    public PlaceCreateResponse createPlace(Long festivalId, PlaceCreateRequest request) {
        Festival festival = getFestivalById(festivalId);

        Place place = request.toPlace(festival);
        Place savedPlace = placeJpaRepository.save(place);

        return PlaceCreateResponse.from(savedPlace);
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

    @Transactional
    public MainPlaceUpdateResponse updateMainPlace(Long festivalId, Long placeId, MainPlaceUpdateRequest request) {
        Place place = getPlaceById(placeId);
        validatePlaceBelongsToFestival(place, festivalId);

        place.updatePlace(
                request.placeCategory(),
                request.title(),
                request.description(),
                request.location(),
                request.host(),
                request.startTime(),
                request.endTime()
        );

        updateTimeTags(festivalId, place, request.timeTags());

        // 실제 저장된 시간 태그 조회
        List<Long> timeTagIds = getResponseTimeTagIds(placeId);
        return MainPlaceUpdateResponse.from(place, timeTagIds);
    }

    private void updateTimeTags(Long festivalId, Place place, List<Long> timeTagIds) {
        // 중복 id 제거
        List<Long> distinctRequestTimeTagIds = timeTagIds.stream()
                .distinct()
                .toList();

        // 기존 시간 태그 ID 조회
        List<Long> existingTimeTagIds = getExistingTimeTagIdsByPlaceId(place.getId());

        // 삭제할 시간 태그 ID 목록 찾기
        List<Long> deleteTimeTagIds = getDeleteTimeTagIds(existingTimeTagIds, distinctRequestTimeTagIds);
        placeTimeTagJpaRepository.deleteAllByPlaceIdAndTimeTagIdIn(place.getId(), deleteTimeTagIds);

        // 추가할 시간 태그 ID 목록 찾기
        List<TimeTag> addTimeTags = getAddTimeTags(existingTimeTagIds, distinctRequestTimeTagIds);
        validateTimeTagsBelongsToFestival(addTimeTags, festivalId);

        List<PlaceTimeTag> addPlaceTimeTags = createAddPlaceTimeTags(place, addTimeTags);
        placeTimeTagJpaRepository.saveAll(addPlaceTimeTags);
    }

    private void validateTimeTagsBelongsToFestival(List<TimeTag> addTimeTags, Long festivalId) {
        addTimeTags.forEach(addTimeTag -> {
            if (!addTimeTag.isFestivalIdEqualTo(festivalId)) {
                throw new BusinessException("해당 축제의 시간 태그가 아닙니다.", HttpStatus.FORBIDDEN);
            }
        });
    }

    private List<Long> getDeleteTimeTagIds(List<Long> existingTimeTagIds, List<Long> requestTimeTagIds) {
        return existingTimeTagIds.stream()
                .filter(timeTagId -> !requestTimeTagIds.contains(timeTagId))
                .toList();
    }

    private List<TimeTag> getAddTimeTags(List<Long> existingTimeTagIds, List<Long> requestTimeTagIds) {
        List<Long> addTimeTagIds = requestTimeTagIds.stream()
                .filter(timeTagId -> !existingTimeTagIds.contains(timeTagId))
                .toList();

        return timeTagJpaRepository.findAllById(addTimeTagIds);
    }

    private List<PlaceTimeTag> createAddPlaceTimeTags(Place place, List<TimeTag> addTimeTags) {
        return addTimeTags.stream()
                .map(timeTag -> new PlaceTimeTag(place, timeTag))
                .toList();
    }

    private List<Long> getResponseTimeTagIds(final Long placeId) {
        return placeTimeTagJpaRepository.findAllByPlaceId(placeId).stream()
                .map(placeTimeTag -> placeTimeTag.getTimeTag().getId())
                .toList();
    }

    private List<Long> getExistingTimeTagIdsByPlaceId(Long placeId) {
        return placeTimeTagJpaRepository.findAllByPlaceId(placeId).stream()
                .map(placeTimeTag -> placeTimeTag.getTimeTag().getId())
                .toList();
    }

    @Transactional
    public EtcPlaceUpdateResponse updateEtcPlace(Long festivalId, Long placeId, EtcPlaceUpdateRequest request) {
        Place place = getPlaceById(placeId);
        validatePlaceBelongsToFestival(place, festivalId);

        place.updatePlace(request.title());
        updateTimeTags(festivalId, place, request.timeTags());

        // 실제 저장된 시간 태그 조회
        List<Long> timeTagIds = getResponseTimeTagIds(placeId);
        return EtcPlaceUpdateResponse.from(place, timeTagIds);
    }

    @Transactional
    public void deleteByPlaceId(Long festivalId, Long placeId) {
        Place place = getPlaceById(placeId);
        validatePlaceBelongsToFestival(place, festivalId);

        placeImageJpaRepository.deleteAllByPlaceId(placeId);
        placeAnnouncementJpaRepository.deleteAllByPlaceId(placeId);
        placeFavoriteJpaRepository.deleteAllByPlaceId(placeId);
        placeJpaRepository.deleteById(placeId);
    }

    private Place getPlaceById(Long placeId) {
        return placeJpaRepository.findById(placeId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 플레이스입니다.", HttpStatus.NOT_FOUND));
    }

    private Festival getFestivalById(Long festivalId) {
        return festivalJpaRepository.findById(festivalId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 축제입니다.", HttpStatus.BAD_REQUEST));
    }

    private PlaceResponse convertPlaceToResponse(Place place) {
        if (place.isMainPlace()) {
            Long placeId = place.getId();
            List<PlaceImage> placeImages = placeImageJpaRepository.findAllByPlaceIdOrderBySequenceAsc(placeId);
            List<PlaceAnnouncement> placeAnnouncements = placeAnnouncementJpaRepository.findAllByPlaceId(placeId);
            List<TimeTag> timeTags = placeTimeTagJpaRepository.findAllByPlaceId(placeId).stream()
                    .map(PlaceTimeTag::getTimeTag)
                    .toList();
            return PlaceResponse.from(place, placeImages, placeAnnouncements, timeTags);
        }

        List<TimeTag> timeTags = placeTimeTagJpaRepository.findAllByPlaceId(place.getId()).stream()
                .map(PlaceTimeTag::getTimeTag)
                .toList();
        return PlaceResponse.from(place, List.of(), List.of(), timeTags);
    }

    private void validatePlaceBelongsToFestival(Place place, Long festivalId) {
        if (!place.isFestivalIdEqualTo(festivalId)) {
            throw new BusinessException("해당 축제의 플레이스가 아닙니다.", HttpStatus.FORBIDDEN);
        }
    }
}
