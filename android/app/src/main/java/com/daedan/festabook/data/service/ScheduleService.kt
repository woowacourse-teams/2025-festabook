package com.daedan.festabook.data.service

import com.daedan.festabook.data.model.response.schedule.ScheduleDateResponse
import com.daedan.festabook.data.model.response.schedule.ScheduleEventResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ScheduleService {
    @GET("event-dates/{eventDateId}/events")
    suspend fun fetchScheduleEventsById(
        @Path("eventDateId") eventDateId: Long,
    ): Response<List<ScheduleEventResponse>>

    @GET("event-dates")
    suspend fun fetchScheduleDates(): Response<List<ScheduleDateResponse>>
}
