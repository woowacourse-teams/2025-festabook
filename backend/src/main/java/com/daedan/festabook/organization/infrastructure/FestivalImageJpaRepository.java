package com.daedan.festabook.organization.infrastructure;

import com.daedan.festabook.organization.domain.FestivalImage;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FestivalImageJpaRepository extends JpaRepository<FestivalImage, Long> {

    List<FestivalImage> findAllByOrganizationIdOrderBySequenceAsc(Long organizationId);

    Set<FestivalImage> findAllByOrganizationId(Long organizationId);

    @Query("SELECT MAX(f.sequence) FROM FestivalImage f WHERE f.organization.id = :organizationId")
    Optional<Integer> findMaxSequenceByOrganizationId(@Param("organizationId") Long organizationId);
}
