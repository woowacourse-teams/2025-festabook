package com.daedan.festabook.data.repository

import com.daedan.festabook.data.datasource.remote.adapter.ApiResult
import com.daedan.festabook.data.datasource.remote.schedule.ScheduleDataSource
import com.daedan.festabook.data.model.toDomain
import com.daedan.festabook.domain.model.ScheduleDate
import com.daedan.festabook.domain.model.ScheduleEvent
import com.daedan.festabook.domain.repository.ScheduleRepository

class ScheduleRepositoryImpl(
    private val scheduleDataSource: ScheduleDataSource,
) : ScheduleRepository {
    override suspend fun fetchAllScheduleDates(): Result<List<ScheduleDate>> {
        val response = scheduleDataSource.fetchScheduleDates().toResult()
        return response.map { scheduleDateResponses -> scheduleDateResponses.map { it.toDomain() } }
    }

    override suspend fun fetchScheduleEventsById(eventDateId: Long): Result<List<ScheduleEvent>> {
        val response = scheduleDataSource.fetchScheduleEventsById(eventDateId).toResult()
        return response.map { scheduleEventResponses -> scheduleEventResponses.map { it.toDomain() } }
    }
}

fun <T> ApiResult<T>.toResult(): Result<T> =
    when (this) {
        is ApiResult.Success -> Result.success(data)
        is ApiResult.ClientError -> Result.failure(Exception("Client error: $code $message"))
        is ApiResult.ServerError -> Result.failure(Exception("Server error: $code $message"))
        is ApiResult.NetworkError -> Result.failure(throwable)
        is ApiResult.UnknownError -> Result.failure(Exception("Unknown error"))
    }
