package com.daedan.festabook.data.repository

import com.daedan.festabook.data.datasource.remote.schedule.ScheduleDataSource
import com.daedan.festabook.data.model.response.schedule.toDomain
import com.daedan.festabook.data.util.toResult
import com.daedan.festabook.domain.model.ScheduleDate
import com.daedan.festabook.domain.model.ScheduleEvent
import com.daedan.festabook.domain.repository.ScheduleRepository

class ScheduleRepositoryImpl(
    private val scheduleDataSource: ScheduleDataSource,
) : ScheduleRepository {
    override suspend fun fetchAllScheduleDates(): Result<List<ScheduleDate>> {
        val response = scheduleDataSource.fetchScheduleDates().toResult()
        return response.mapCatching { scheduleDateResponses -> scheduleDateResponses.map { it.toDomain() } }
    }

    override suspend fun fetchScheduleEventsById(eventDateId: Long): Result<List<ScheduleEvent>> {
        val response = scheduleDataSource.fetchScheduleEventsById(eventDateId).toResult()
        return response.mapCatching { scheduleEventResponses -> scheduleEventResponses.map { it.toDomain() } }
    }
}
