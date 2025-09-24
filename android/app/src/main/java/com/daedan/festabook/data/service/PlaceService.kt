package com.daedan.festabook.data.service

import com.daedan.festabook.data.model.response.place.PlaceDetailResponse
import com.daedan.festabook.data.model.response.place.PlaceGeographyResponse
import com.daedan.festabook.data.model.response.place.PlaceResponse
import com.daedan.festabook.data.model.response.place.TimeTagResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PlaceService {
    @GET("time-tags")
    suspend fun fetchTimeTag(): Response<List<TimeTagResponse>>

    @GET("places/previews")
    suspend fun fetchPlaces(): Response<List<PlaceResponse>>

    @GET("places/{placeId}")
    suspend fun fetchPlaceDetail(
        @Path("placeId") id: Long,
    ): Response<PlaceDetailResponse>

    @GET("places/geographies")
    suspend fun fetchPlaceGeographies(): Response<List<PlaceGeographyResponse>>
}
