package com.daedan.festabook.domain.repository

import com.daedan.festabook.domain.model.ScheduleEvent

interface ScheduleRepository {
    val dummyScheduleEvents: List<ScheduleEvent>
}
