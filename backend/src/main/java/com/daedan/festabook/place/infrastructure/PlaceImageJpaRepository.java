package com.daedan.festabook.place.infrastructure;

import com.daedan.festabook.place.domain.PlaceImage;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceImageJpaRepository extends JpaRepository<PlaceImage, Long> {

    List<PlaceImage> findAllByPlaceId(Long placeId);

    List<PlaceImage> findAllByPlaceIdIn(Collection<Long> placeIds);
}
