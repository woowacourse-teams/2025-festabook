package com.daedan.festabook.organization.infrastructure;

import com.daedan.festabook.organization.domain.OrganizationBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationBookmarkJpaRepository extends JpaRepository<OrganizationBookmark, Long> {
}
