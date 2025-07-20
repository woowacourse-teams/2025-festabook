package com.daedan.festabook.place.service;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.place.domain.PlaceAnnouncement;
import com.daedan.festabook.place.domain.PlaceDetail;
import com.daedan.festabook.place.domain.PlaceImage;
import com.daedan.festabook.place.dto.PlaceResponse;
import com.daedan.festabook.place.infrastructure.PlaceAnnouncementJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceDetailJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceImageJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceImageJpaRepository placeImageJpaRepository;
    private final PlaceDetailJpaRepository placeDetailJpaRepository;
    private final PlaceAnnouncementJpaRepository placeAnnouncementJpaRepository;

    @Transactional(readOnly = true)
    public PlaceResponse getPlaceByPlaceId(Long placeId) {
        PlaceDetail placeDetail = getPlaceDetailById(placeId);
        List<PlaceImage> placeImages = placeImageJpaRepository.findAllByPlaceIdOrderBySequenceAsc(placeId);
        List<PlaceAnnouncement> placeAnnouncements = placeAnnouncementJpaRepository.findAllByPlaceId(placeId);

        return PlaceResponse.from(placeDetail.getPlace(), placeDetail, placeImages, placeAnnouncements);
    }

    // TODO: ExceptionHandler 등록 후 예외 변경
    private PlaceDetail getPlaceDetailById(Long placeId) {
        return placeDetailJpaRepository.findById(placeId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 플레이스 세부 정보입니다.", HttpStatus.NOT_FOUND));
    }
}
