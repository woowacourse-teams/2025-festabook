package com.daedan.festabook.festival.infrastructure;

import com.daedan.festabook.festival.domain.FestivalNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FestivalNotificationJpaRepository extends JpaRepository<FestivalNotification, Long> {

    boolean existsByFestivalIdAndDeviceId(Long festivalId, Long deviceId);
}
