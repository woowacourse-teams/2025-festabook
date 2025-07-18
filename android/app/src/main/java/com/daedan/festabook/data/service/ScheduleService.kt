package com.daedan.festabook.data.service

import com.daedan.festabook.data.model.response.ScheduleDateResponse
import com.daedan.festabook.data.model.response.ScheduleEventResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ScheduleService {
    @GET("schedules/{eventDateId}")
    suspend fun fetchScheduleEventsById(
        @Path("eventDateId") eventDateId: Long,
    ): Response<List<ScheduleEventResponse>>

    @GET("schedules")
    suspend fun fetchScheduleDates(): Response<List<ScheduleDateResponse>>
}
