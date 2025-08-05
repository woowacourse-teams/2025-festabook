package com.daedan.festabook.announcement.infrastructure;

import com.daedan.festabook.announcement.domain.Announcement;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementJpaRepository extends JpaRepository<Announcement, Long> {

    List<Announcement> findAllByOrganizationId(Long organizationId);

    Long countByOrganizationIdAndIsPinnedTrue(Long organizationId);
}
