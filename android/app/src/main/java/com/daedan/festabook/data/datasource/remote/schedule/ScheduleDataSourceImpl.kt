package com.daedan.festabook.data.datasource.remote.schedule

import com.daedan.festabook.data.datasource.remote.ApiResult
import com.daedan.festabook.data.model.response.schedule.ScheduleDateResponse
import com.daedan.festabook.data.model.response.schedule.ScheduleEventResponse
import com.daedan.festabook.data.service.ScheduleService

class ScheduleDataSourceImpl(
    private val scheduleService: ScheduleService,
) : ScheduleDataSource {
    override suspend fun fetchScheduleEventsById(eventDateId: Long): ApiResult<List<ScheduleEventResponse>> =
        ApiResult.toApiResult { scheduleService.fetchScheduleEventsById(eventDateId) }

    override suspend fun fetchScheduleDates(): ApiResult<List<ScheduleDateResponse>> =
        ApiResult.toApiResult { scheduleService.fetchScheduleDates() }
}
