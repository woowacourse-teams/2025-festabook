package com.daedan.festabook.festival.infrastructure;

import com.daedan.festabook.festival.domain.FestivalNotification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FestivalNotificationJpaRepository extends JpaRepository<FestivalNotification, Long> {

    @Query(value = """
            SELECT EXISTS(
                SELECT 1
                FROM festival_notification fn
                WHERE fn.festival_id = :festivalId
                AND fn.device_id = :deviceId
                AND fn.deleted = 0
            )
            """, nativeQuery = true)
    int getExistsFlagByFestivalIdAndDeviceId(
            @Param("festivalId") Long festivalId,
            @Param("deviceId") Long deviceId
    );

    @Query("""
                SELECT FN
                FROM FestivalNotification FN
                JOIN FETCH FN.festival F
                WHERE FN.device.id = :deviceId
            """)
    List<FestivalNotification> findAllWithFestivalByDeviceId(Long deviceId);
}
