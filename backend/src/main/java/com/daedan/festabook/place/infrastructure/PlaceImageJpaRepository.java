package com.daedan.festabook.place.infrastructure;

import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceImageJpaRepository extends JpaRepository<PlaceImage, Long> {

    List<PlaceImage> findAllByPlaceIdOrderBySequenceAsc(Long placeId);

    List<PlaceImage> findAllByPlaceInAndSequence(List<Place> places, int sequence);

    void deleteAllByPlaceId(Long placeId);
}
