package com.daedan.festabook.place.infrastructure;

import com.daedan.festabook.place.domain.PlaceFavorite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceFavoriteJpaRepository extends JpaRepository<PlaceFavorite, Long> {

    boolean existsByPlaceIdAndDeviceId(Long placeId, Long deviceId);

    void deleteAllByPlaceId(Long placeId);
}
