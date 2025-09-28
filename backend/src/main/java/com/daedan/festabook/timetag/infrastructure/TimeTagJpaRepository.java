package com.daedan.festabook.timetag.infrastructure;

import com.daedan.festabook.timetag.domain.TimeTag;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeTagJpaRepository extends JpaRepository<TimeTag, Long> {

    List<TimeTag> findAllByFestivalId(Long festivalId);

    List<TimeTag> findAllByIdIn(Collection<Long> ids);
}
