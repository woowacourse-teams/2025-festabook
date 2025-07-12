package com.daedan.festabook.schedule.infrastructure;

import com.daedan.festabook.schedule.domain.EventDay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventDayJpaRepository extends JpaRepository<EventDay, Long> {
}
