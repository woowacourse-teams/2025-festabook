package com.daedan.festabook.organization.service;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.dto.OrganizationGeographicResponse;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationJpaRepository organizationJpaRepository;

    public OrganizationGeographicResponse getOrganizationGeographicById(Long id) {
        Organization organization = organizationJpaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("조직이 존재하지 않습니다.", HttpStatus.NOT_FOUND));

        return OrganizationGeographicResponse.from(organization);
    }
}
