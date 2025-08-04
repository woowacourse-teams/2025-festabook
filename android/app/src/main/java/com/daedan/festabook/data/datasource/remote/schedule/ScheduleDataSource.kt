package com.daedan.festabook.data.datasource.remote.schedule

import com.daedan.festabook.data.datasource.remote.ApiResult
import com.daedan.festabook.data.model.response.ScheduleDateResponse
import com.daedan.festabook.data.model.response.ScheduleEventResponse

interface ScheduleDataSource {
    suspend fun fetchScheduleDates(): ApiResult<List<ScheduleDateResponse>>

    suspend fun fetchScheduleEventsById(eventDateId: Long): ApiResult<List<ScheduleEventResponse>>
}
