package com.daedan.festabook.data.service

import com.daedan.festabook.data.model.response.PlaceDetailResponse
import com.daedan.festabook.data.model.response.PlaceGeographyResponse
import com.daedan.festabook.data.model.response.PlaceResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PlaceService {
    @GET("places/previews")
    suspend fun fetchPlaces(): Response<List<PlaceResponse>>

    @GET("places/{placeId}")
    suspend fun fetchPlaceDetail(
        @Path("placeId") id: Long,
    ): Response<PlaceDetailResponse>

    @GET("places/geographies")
    suspend fun fetchPlaceGeographies(): Response<List<PlaceGeographyResponse>>
}
