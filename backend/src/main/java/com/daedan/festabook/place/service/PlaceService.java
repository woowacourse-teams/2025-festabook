package com.daedan.festabook.place.service;

import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.dto.PlaceResponses;
import com.daedan.festabook.place.infrastructure.PlaceJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceJpaRepository placeJpaRepository;

    // TODO: 학교 연결
    public PlaceResponses findAllPlace() {
        List<Place> places = placeJpaRepository.findAll();
        return PlaceResponses.from(places);
    }
}
