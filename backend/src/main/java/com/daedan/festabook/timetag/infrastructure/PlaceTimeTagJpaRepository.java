package com.daedan.festabook.timetag.infrastructure;

import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.timetag.domain.PlaceTimeTag;
import com.daedan.festabook.timetag.domain.TimeTag;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlaceTimeTagJpaRepository extends JpaRepository<PlaceTimeTag, Long> {

    List<PlaceTimeTag> findAllByPlaceId(Long placeId);

    void deleteAllByPlaceIdAndTimeTagIdIn(Long placeId, List<Long> deleteTimeTagIds);

    boolean existsByTimeTag(TimeTag timeTag);

    List<PlaceTimeTag> findAllByPlaceIn(Collection<Place> places);

    @Query("SELECT ptt FROM PlaceTimeTag ptt JOIN FETCH ptt.place JOIN FETCH ptt.timeTag WHERE ptt.place IN :places")
    List<PlaceTimeTag> findAllByPlaceInWithTimeTag(@Param("places") Collection<Place> places);
}
