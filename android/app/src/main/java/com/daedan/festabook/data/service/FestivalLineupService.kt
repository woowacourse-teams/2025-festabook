package com.daedan.festabook.data.service

import com.daedan.festabook.data.model.response.lineup.LineupResponse
import retrofit2.Response
import retrofit2.http.GET

interface FestivalLineupService {
    @GET("lineups")
    suspend fun fetchLineup(): Response<List<LineupResponse>>
}
