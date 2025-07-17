package com.daedan.festabook.data.datasource.remote.schedule

import com.daedan.festabook.data.api.ScheduleApi
import com.daedan.festabook.data.datasource.remote.adapter.ApiResult
import com.daedan.festabook.data.model.ScheduleDateResponse
import com.daedan.festabook.data.model.ScheduleEventResponse

class ScheduleDataSourceImpl(
    private val scheduleApi: ScheduleApi,
) : ScheduleDataSource {
    override suspend fun fetchScheduleEventsById(eventDateId: Long): ApiResult<List<ScheduleEventResponse>> =
        ApiResult.toApiResult { scheduleApi.fetchScheduleEventsById(eventDateId) }

    override suspend fun fetchScheduleDates(): ApiResult<List<ScheduleDateResponse>> =
        ApiResult.toApiResult { scheduleApi.fetchScheduleDates() }
}
