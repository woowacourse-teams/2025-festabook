package com.daedan.festabook.festival.infrastructure;

import com.daedan.festabook.festival.domain.FestivalImage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FestivalImageJpaRepository extends JpaRepository<FestivalImage, Long> {

    List<FestivalImage> findAllByFestivalIdOrderBySequenceAsc(Long festivalId);

    @Query("SELECT MAX(f.sequence) FROM FestivalImage f WHERE f.festival.id = :festivalId")
    Optional<Integer> findMaxSequenceByFestivalId(@Param("festivalId") Long festivalId);
}
