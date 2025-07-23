package com.daedan.festabook.data.service

import com.daedan.festabook.data.model.response.PlaceDetailResponse
import com.daedan.festabook.data.model.response.PlaceResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PlaceService {
    @GET("places/previews")
    suspend fun getPlaces(): Response<List<PlaceResponse>>

    @GET("places/{placeId}")
    suspend fun getPlaceDetail(
        @Path("placeId") id: Long,
    ): Response<PlaceDetailResponse>
}
