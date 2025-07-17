package com.daedan.festabook.data.api

import com.daedan.festabook.data.model.ScheduleDateResponse
import com.daedan.festabook.data.model.ScheduleEventResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ScheduleApi {
    @GET("schedules/{eventDateId}")
    suspend fun fetchScheduleEventsById(
        @Path("eventDateId") eventDateId: Long,
    ): Response<List<ScheduleEventResponse>>

    @GET("schedules")
    suspend fun fetchScheduleDates(): Response<List<ScheduleDateResponse>>
}
