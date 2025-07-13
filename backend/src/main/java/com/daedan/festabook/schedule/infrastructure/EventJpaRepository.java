package com.daedan.festabook.schedule.infrastructure;

import com.daedan.festabook.schedule.domain.Event;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventJpaRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByEventDateId(Long eventDateId);
}
