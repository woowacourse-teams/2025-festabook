package com.daedan.festabook.organization.service;

import com.daedan.festabook.event.domain.EventDate;
import com.daedan.festabook.event.infrastructure.EventDateJpaRepository;
import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.organization.domain.FestivalImage;
import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.dto.FestivalImageResponses;
import com.daedan.festabook.organization.dto.OrganizationGeographyResponse;
import com.daedan.festabook.organization.dto.OrganizationResponse;
import com.daedan.festabook.organization.infrastructure.FestivalImageJpaRepository;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationJpaRepository organizationJpaRepository;
    private final FestivalImageJpaRepository festivalImageJpaRepository;
    private final EventDateJpaRepository eventDateJpaRepository;

    public OrganizationGeographyResponse getOrganizationGeographyByOrganizationId(Long organizationId) {
        Organization organization = getOrganizationById(organizationId);

        return OrganizationGeographyResponse.from(organization);
    }

    public OrganizationResponse getOrganizationByOrganizationId(Long organizationId) {
        Organization organization = getOrganizationById(organizationId);
        List<FestivalImage> festivalImages =
                festivalImageJpaRepository.findAllByOrganizationIdOrderBySequenceAsc(organizationId);
        List<EventDate> eventDates = eventDateJpaRepository.findAllByOrganizationIdOrderByDateAsc(organizationId);

        return new OrganizationResponse(
                organization.getId(),
                organization.getUniversityName(),
                FestivalImageResponses.from(festivalImages),
                organization.getFestivalName(),
                eventDates.isEmpty() ? null : eventDates.getFirst().getDate(),
                eventDates.isEmpty() ? null : eventDates.getLast().getDate()
        );
    }

    private Organization getOrganizationById(Long organizationId) {
        return organizationJpaRepository.findById(organizationId)
                .orElseThrow(() -> new BusinessException("조직이 존재하지 않습니다.", HttpStatus.NOT_FOUND));
    }
}
