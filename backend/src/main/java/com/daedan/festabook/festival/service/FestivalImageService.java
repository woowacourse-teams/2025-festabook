package com.daedan.festabook.festival.service;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalImage;
import com.daedan.festabook.festival.dto.FestivalImageRequest;
import com.daedan.festabook.festival.dto.FestivalImageResponse;
import com.daedan.festabook.festival.dto.FestivalImageResponses;
import com.daedan.festabook.festival.dto.FestivalImageSequenceUpdateRequest;
import com.daedan.festabook.festival.infrastructure.FestivalImageJpaRepository;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FestivalImageService {

    private final FestivalJpaRepository festivalJpaRepository;
    private final FestivalImageJpaRepository festivalImageJpaRepository;

    @Transactional
    public FestivalImageResponse addFestivalImage(Long festivalId, FestivalImageRequest request) {
        Festival festival = getFestivalById(festivalId);

        Integer currentMaxSequence = festivalImageJpaRepository.findMaxSequenceByFestivalId(festivalId)
                .orElseGet(() -> 0);
        Integer newSequence = currentMaxSequence + 1;

        FestivalImage festivalImage = new FestivalImage(festival, request.imageUrl(), newSequence);
        FestivalImage savedFestivalImage = festivalImageJpaRepository.save(festivalImage);

        return FestivalImageResponse.from(savedFestivalImage);
    }

    @Transactional
    public FestivalImageResponses updateFestivalImagesSequence(List<FestivalImageSequenceUpdateRequest> requests) {
        // TODO: sequence DTO 값 검증 추가
        List<FestivalImage> festivalImages = new ArrayList<>();

        for (FestivalImageSequenceUpdateRequest request : requests) {
            FestivalImage festivalImage = getFestivalImageById(request.festivalImageId());
            festivalImage.updateSequence(request.sequence());
            festivalImages.add(festivalImage);
        }

        Collections.sort(festivalImages);

        return FestivalImageResponses.from(festivalImages);
    }

    public void removeFestivalImage(Long festivalImageId) {
        festivalImageJpaRepository.deleteById(festivalImageId);
    }

    private Festival getFestivalById(Long festivalId) {
        return festivalJpaRepository.findById(festivalId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 축제입니다.", HttpStatus.NOT_FOUND));
    }

    private FestivalImage getFestivalImageById(Long festivalImageId) {
        return festivalImageJpaRepository.findById(festivalImageId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 축제 이미지입니다.", HttpStatus.NOT_FOUND));
    }
}
