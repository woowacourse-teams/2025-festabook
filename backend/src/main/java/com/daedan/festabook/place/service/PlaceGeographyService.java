package com.daedan.festabook.place.service;

import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.dto.PlaceGeographyResponses;
import com.daedan.festabook.place.infrastructure.PlaceJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceGeographyService {

    private final PlaceJpaRepository placeJpaRepository;

    public PlaceGeographyResponses getAllPlaceGeographyByOrganizationId(Long organizationId) {
        List<Place> places = placeJpaRepository.findAllByOrganizationId(organizationId);
        return PlaceGeographyResponses.from(places);
    }
}
