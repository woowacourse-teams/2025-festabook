package com.daedan.festabook.festival.infrastructure;

import com.daedan.festabook.festival.domain.FestivalNotification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FestivalNotificationJpaRepository extends JpaRepository<FestivalNotification, Long> {

    @Query("""
                SELECT fn
                FROM FestivalNotification fn
                JOIN FETCH fn.festival f
                WHERE fn.device.id = :deviceId
            """)
    List<FestivalNotification> findAllWithFestivalByDeviceId(Long deviceId);

    Long countByFestivalIdAndDeviceId(Long festivalId, Long deviceId);

    @Query(value = """
            SELECT EXISTS(
                SELECT 1
                FROM festival_notification fn
                WHERE fn.festival_id = :festivalId
                AND fn.active_device_id = :deviceId
            )
            """, nativeQuery = true)
    int getExistsFlagByFestivalIdAndDeviceId(Long festivalId, Long deviceId);
}
