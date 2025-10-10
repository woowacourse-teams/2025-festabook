package com.daedan.festabook.place.infrastructure;

import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceAnnouncement;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceAnnouncementJpaRepository extends JpaRepository<PlaceAnnouncement, Long> {

    List<PlaceAnnouncement> findAllByPlaceId(Long placeId);

    List<PlaceAnnouncement> findAllByPlaceIn(Collection<Place> places);

    Integer countByPlace(Place place);

    void deleteAllByPlaceId(Long placeId);
}
