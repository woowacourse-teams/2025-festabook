package com.daedan.festabook.organization.infrastructure;

import com.daedan.festabook.organization.domain.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationJpaRepository extends JpaRepository<Organization, Long> {
}
