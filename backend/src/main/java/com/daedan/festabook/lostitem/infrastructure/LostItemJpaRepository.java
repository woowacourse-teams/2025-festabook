package com.daedan.festabook.lostitem.infrastructure;

import com.daedan.festabook.lostitem.domain.LostItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LostItemJpaRepository extends JpaRepository<LostItem, Long> {
    List<LostItem> findAllByOrganizationId(Long organizationId);
}
