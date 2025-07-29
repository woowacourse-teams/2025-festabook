package com.daedan.festabook.organization.infrastructure;

import com.daedan.festabook.organization.domain.FestivalImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FestivalImageJpaRepository extends JpaRepository<FestivalImage, Long> {
    List<FestivalImage> findAllByOrganizationId(Long organizationId);
}
