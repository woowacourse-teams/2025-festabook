package com.daedan.festabook.festival.infrastructure;

import com.daedan.festabook.festival.domain.Lineup;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LineupRepository extends JpaRepository<Lineup, Long> {

    List<Lineup> findByFestivalId(Long festivalId);
}
