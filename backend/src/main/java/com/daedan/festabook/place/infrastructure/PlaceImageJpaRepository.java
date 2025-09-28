package com.daedan.festabook.place.infrastructure;

import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceImage;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlaceImageJpaRepository extends JpaRepository<PlaceImage, Long> {

    List<PlaceImage> findAllByPlaceIdOrderBySequenceAsc(Long placeId);

    List<PlaceImage> findAllByPlaceInAndSequence(List<Place> places, int sequence);

    List<PlaceImage> findAllByPlaceInOrderBySequenceAsc(Collection<Place> places);

    @Query("SELECT MAX(p.sequence) FROM PlaceImage p WHERE p.place = :place AND p.deleted = false")
    Optional<Integer> findMaxSequenceByPlace(@Param("place") Place place);

    Long countByPlace(Place place);

    void deleteAllByPlaceId(Long placeId);
}
