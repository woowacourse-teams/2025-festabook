package com.daedan.festabook.domain.repository

import com.daedan.festabook.domain.model.ScheduleDate
import com.daedan.festabook.domain.model.ScheduleEvent

interface ScheduleRepository {
    suspend fun fetchAllScheduleDates(): Result<List<ScheduleDate>>

    suspend fun fetchScheduleEventsById(eventDateId: Long): Result<List<ScheduleEvent>>
}
