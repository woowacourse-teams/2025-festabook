package com.daedan.festabook.event.infrastructure;

import com.daedan.festabook.event.domain.EventDate;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventDateJpaRepository extends JpaRepository<EventDate, Long> {

    List<EventDate> findAllByFestivalId(Long festivalId);

    boolean existsByFestivalIdAndDate(Long festivalId, LocalDate date);
}
