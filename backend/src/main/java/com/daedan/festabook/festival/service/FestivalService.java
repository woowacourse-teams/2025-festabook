package com.daedan.festabook.festival.service;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalImage;
import com.daedan.festabook.festival.dto.FestivalCreateRequest;
import com.daedan.festabook.festival.dto.FestivalCreateResponse;
import com.daedan.festabook.festival.dto.FestivalGeographyResponse;
import com.daedan.festabook.festival.dto.FestivalInformationResponse;
import com.daedan.festabook.festival.dto.FestivalInformationUpdateRequest;
import com.daedan.festabook.festival.dto.FestivalLostItemGuideResponse;
import com.daedan.festabook.festival.dto.FestivalLostItemGuideUpdateRequest;
import com.daedan.festabook.festival.dto.FestivalLostItemGuideUpdateResponse;
import com.daedan.festabook.festival.dto.FestivalResponse;
import com.daedan.festabook.festival.dto.FestivalUniversityResponses;
import com.daedan.festabook.festival.infrastructure.FestivalImageJpaRepository;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.global.exception.BusinessException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FestivalService {

    private final FestivalJpaRepository festivalJpaRepository;
    private final FestivalImageJpaRepository festivalImageJpaRepository;

    public FestivalCreateResponse createFestival(FestivalCreateRequest request) {
        Festival festival = request.toEntity();
        festivalJpaRepository.save(festival);

        return FestivalCreateResponse.from(festival);
    }

    public FestivalGeographyResponse getFestivalGeographyByFestivalId(Long festivalId) {
        Festival festival = getFestivalById(festivalId);
        return FestivalGeographyResponse.from(festival);
    }

    public FestivalResponse getFestivalByFestivalId(Long festivalId) {
        Festival festival = getFestivalById(festivalId);
        List<FestivalImage> festivalImages =
                festivalImageJpaRepository.findAllByFestivalIdOrderBySequenceAsc(festivalId);

        return FestivalResponse.from(festival, festivalImages);
    }

    public FestivalUniversityResponses getUniversitiesByUniversityName(String universityName) {
        List<Festival> festivals = festivalJpaRepository.findByUniversityNameContainingAndUserVisibleTrue(
                universityName
        );
        return FestivalUniversityResponses.from(festivals);
    }

    public FestivalLostItemGuideResponse getFestivalLostItemGuide(Long festivalId) {
        Festival festival = getFestivalById(festivalId);
        return FestivalLostItemGuideResponse.from(festival);
    }

    @Transactional
    public FestivalInformationResponse updateFestivalInformation(
            Long festivalId,
            FestivalInformationUpdateRequest request
    ) {
        Festival festival = getFestivalById(festivalId);
        festival.updateFestival(request.festivalName(), request.startDate(), request.endDate(), request.userVisible());
        return FestivalInformationResponse.from(festival);
    }

    @Transactional
    public FestivalLostItemGuideUpdateResponse updateFestivalLostItemGuide(
            Long festivalId,
            FestivalLostItemGuideUpdateRequest request
    ) {
        Festival festival = getFestivalById(festivalId);
        festival.updateFestival(request.lostItemGuide());
        return FestivalLostItemGuideUpdateResponse.from(festival);
    }

    private Festival getFestivalById(Long festivalId) {
        return festivalJpaRepository.findById(festivalId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 축제입니다.", HttpStatus.NOT_FOUND));
    }
}
