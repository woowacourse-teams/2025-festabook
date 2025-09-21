package com.daedan.festabook.timetag.infrastructure;

import com.daedan.festabook.timetag.domain.PlaceTimeTag;
import com.daedan.festabook.timetag.domain.TimeTag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceTimeTagJpaRepository extends JpaRepository<PlaceTimeTag, Long> {

    List<PlaceTimeTag> findAllByPlaceId(Long placeId);

    void deleteAllByPlaceIdAndTimeTagIdIn(Long placeId, List<Long> deleteTimeTagIds);

    boolean existsByTimeTag(TimeTag timeTag);
}
