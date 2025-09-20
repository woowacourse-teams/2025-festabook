package com.daedan.festabook.lineup.infrastructure;

import com.daedan.festabook.lineup.domain.Lineup;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LineupJpaRepository extends JpaRepository<Lineup, Long> {

    List<Lineup> findAllByFestivalId(Long festivalId);

    boolean existsByFestivalIdAndPerformanceAt(Long festivalId, LocalDateTime performanceAt);
}
