package com.daedan.festabook.organization.service;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.organization.domain.FestivalImage;
import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.dto.FestivalImageDeleteRequest;
import com.daedan.festabook.organization.dto.FestivalImageRequest;
import com.daedan.festabook.organization.dto.FestivalImageResponse;
import com.daedan.festabook.organization.dto.FestivalImageResponses;
import com.daedan.festabook.organization.dto.FestivalImageSequenceUpdateRequest;
import com.daedan.festabook.organization.infrastructure.FestivalImageJpaRepository;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FestivalImageService {

    private final OrganizationJpaRepository organizationJpaRepository;
    private final FestivalImageJpaRepository festivalImageJpaRepository;

    public FestivalImageResponse addFestivalImage(Long organizationId, FestivalImageRequest request) {
        Organization organization = getOrganizationById(organizationId);

        Integer currentMaxSequence = festivalImageJpaRepository.countByOrganizationId(organizationId);
        Integer nextSequence = currentMaxSequence + 1;

        FestivalImage festivalImage = new FestivalImage(organization, request.imageUrl(), nextSequence);
        FestivalImage savedFestivalImage = festivalImageJpaRepository.save(festivalImage);

        return FestivalImageResponse.from(savedFestivalImage);
    }

    @Transactional
    public FestivalImageResponses updateFestivalImagesSequence(Long organizationId,
                                                               List<FestivalImageSequenceUpdateRequest> requests) {
        List<FestivalImage> existsFestivalImages = festivalImageJpaRepository.findAllByOrganizationId(organizationId);
        List<FestivalImage> festivalImages = new ArrayList<>();

        for (FestivalImageSequenceUpdateRequest request : requests) {
            FestivalImage festivalImage = getFestivalImageById(request.festivalImageId());
            validateFestivalImageOwner(existsFestivalImages, festivalImage);
            festivalImage.updateSequence(request.sequence());
            festivalImages.add(festivalImage);
        }

        festivalImages.sort(sequenceAscending());

        return FestivalImageResponses.from(festivalImages);
    }

    public void removeFestivalImages(List<FestivalImageDeleteRequest> requests) {
        List<Long> festivalImageIds = requests.stream()
                .map(FestivalImageDeleteRequest::festivalImageId)
                .toList();

        festivalImageJpaRepository.deleteAllById(festivalImageIds);
    }

    private void validateFestivalImageOwner(List<FestivalImage> existsFestivalImages, FestivalImage festivalImage) {
        if (!existsFestivalImages.contains(festivalImage)) {
            throw new BusinessException("권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
    }

    private Organization getOrganizationById(Long organizationId) {
        return organizationJpaRepository.findById(organizationId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 조직입니다.", HttpStatus.NOT_FOUND));
    }

    private FestivalImage getFestivalImageById(Long festivalImageId) {
        return festivalImageJpaRepository.findById(festivalImageId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 축제 이미지입니다.", HttpStatus.NOT_FOUND));
    }

    private Comparator<FestivalImage> sequenceAscending() {
        return Comparator.comparing(FestivalImage::getSequence);
    }
}
