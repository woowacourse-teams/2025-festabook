package com.daedan.festabook.data.datasource.remote.schedule

import com.daedan.festabook.data.datasource.remote.adapter.ApiResult
import com.daedan.festabook.data.model.ScheduleDateResponse
import com.daedan.festabook.data.model.ScheduleEventResponse

interface ScheduleDataSource {
    suspend fun fetchScheduleDates(): ApiResult<List<ScheduleDateResponse>>

    suspend fun fetchScheduleEventsById(eventDateId: Long): ApiResult<List<ScheduleEventResponse>>
}
