package com.daedan.festabook.announcement.infrastructure;

import com.daedan.festabook.announcement.domain.DateTimeGenerator;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import org.springframework.stereotype.Component;

@Component
public class BasicDateTimeGenerator implements DateTimeGenerator {

    private static final ZoneId SEOUL_ZONE_ID = ZoneId.of("Asia/Seoul");

    @Override
    public LocalDate generateDate() {
        return LocalDate.now(SEOUL_ZONE_ID);
    }

    @Override
    public LocalTime generateTime() {
        return LocalTime.now(SEOUL_ZONE_ID);
    }
}
