package com.daedan.festabook.event.infrastructure;

import com.daedan.festabook.event.domain.Event;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventJpaRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByEventDateId(Long eventDateId);

    void deleteAllByEventDateId(Long eventDateId);
}
