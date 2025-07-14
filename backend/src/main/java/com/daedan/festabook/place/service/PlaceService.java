package com.daedan.festabook.place.service;

import com.daedan.festabook.place.dto.PlaceResponses;
import com.daedan.festabook.place.infrastructure.PlaceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceJpaRepository placeJpaRepository;

    public PlaceResponses getAllPlaceByOrganizationId(Long organizationId) {
        return PlaceResponses.from(placeJpaRepository.findAllByOrganizationId(organizationId));
    }
}
