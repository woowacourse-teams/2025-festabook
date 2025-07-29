package com.daedan.festabook.place.infrastructure;

import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceDetail;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceDetailJpaRepository extends JpaRepository<PlaceDetail, Long> {

    Optional<PlaceDetail> findByPlaceId(Long placeId);

    List<PlaceDetail> findAllByPlaceIn(List<Place> places);

    boolean existsByPlace(Place place);
}
