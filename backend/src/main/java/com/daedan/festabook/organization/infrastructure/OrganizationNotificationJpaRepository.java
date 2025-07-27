package com.daedan.festabook.organization.infrastructure;

import com.daedan.festabook.organization.domain.OrganizationNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationNotificationJpaRepository extends JpaRepository<OrganizationNotification, Long> {

    boolean existsByOrganizationIdAndDeviceId(Long organizationId, Long deviceId);
}
