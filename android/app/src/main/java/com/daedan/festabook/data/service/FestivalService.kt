package com.daedan.festabook.data.service

import com.daedan.festabook.data.model.response.festival.FestivalGeographyResponse
import com.daedan.festabook.data.model.response.festival.FestivalResponse
import retrofit2.Response
import retrofit2.http.GET

interface FestivalService {
    @GET("festivals")
    suspend fun fetchOrganization(): Response<FestivalResponse>

    @GET("festivals/geography")
    suspend fun fetchOrganizationGeography(): Response<FestivalGeographyResponse>
}
