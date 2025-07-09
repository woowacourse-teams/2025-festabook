package com.daedan.festabook.announcement.domain;

import java.time.LocalDate;
import java.time.LocalTime;

public interface DateTimeGenerator {

    LocalDate generateDate();

    LocalTime generateTime();
}
