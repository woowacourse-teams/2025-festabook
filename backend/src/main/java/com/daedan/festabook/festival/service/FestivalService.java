package com.daedan.festabook.festival.service;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalImage;
import com.daedan.festabook.festival.dto.FestivalGeographyResponse;
import com.daedan.festabook.festival.dto.FestivalInformationResponse;
import com.daedan.festabook.festival.dto.FestivalInformationUpdateRequest;
import com.daedan.festabook.festival.dto.FestivalResponse;
import com.daedan.festabook.festival.infrastructure.FestivalImageJpaRepository;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
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

    @Transactional
    public FestivalInformationResponse updateFestivalInformation(Long festivalId,
                                                                 FestivalInformationUpdateRequest request) {
        Festival festival = getFestivalById(festivalId);
        festival.updateFestival(request.festivalName(), request.startDate(), request.endDate());
        return FestivalInformationResponse.from(festival);
    }

    private Festival getFestivalById(Long festivalId) {
        return festivalJpaRepository.findById(festivalId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 축제입니다.", HttpStatus.NOT_FOUND));
    }
}
