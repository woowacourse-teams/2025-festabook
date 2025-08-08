package com.daedan.festabook.festival.infrastructure;

import com.daedan.festabook.festival.domain.Festival;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FestivalJpaRepository extends JpaRepository<Festival, Long> {
}
