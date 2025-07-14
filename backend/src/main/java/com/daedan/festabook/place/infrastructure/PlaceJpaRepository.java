package com.daedan.festabook.place.infrastructure;

import com.daedan.festabook.place.domain.Place;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceJpaRepository extends JpaRepository<Place, Long> {

    List<Place> findAllByOrganizationId(Long organizationId);
}
