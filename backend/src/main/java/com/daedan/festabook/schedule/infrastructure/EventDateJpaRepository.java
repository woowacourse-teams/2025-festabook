package com.daedan.festabook.schedule.infrastructure;

import com.daedan.festabook.schedule.domain.EventDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventDateJpaRepository extends JpaRepository<EventDate, Long> {
}
