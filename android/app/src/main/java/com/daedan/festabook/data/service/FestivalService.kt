package com.daedan.festabook.data.service

import com.daedan.festabook.data.model.response.UniversityResponse
import com.daedan.festabook.data.model.response.festival.FestivalGeographyResponse
import com.daedan.festabook.data.model.response.festival.FestivalResponse
import com.daedan.festabook.data.model.response.lostitem.LostGuideItemResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FestivalService {
    @GET("festivals")
    suspend fun fetchOrganization(): Response<FestivalResponse>

    @GET("festivals/geography")
    suspend fun fetchOrganizationGeography(): Response<FestivalGeographyResponse>

    @GET("festivals/universities")
    suspend fun findUniversitiesByName(
        @Query("universityName") universityName: String,
    ): Response<List<UniversityResponse>>

    @GET("festivals/lost-item-guide")
    suspend fun fetchLostGuideItem(): Response<LostGuideItemResponse>
}
