package com.daedan.festabook.data.datasource.remote.place

import com.daedan.festabook.data.datasource.remote.ApiResult
import com.daedan.festabook.data.model.response.PlaceDetailResponse
import com.daedan.festabook.data.model.response.PlaceResponse

interface PlaceDataSource {
    suspend fun fetchPlaceDetail(): ApiResult<PlaceDetailResponse>

    suspend fun fetchPlaces(): ApiResult<List<PlaceResponse>>
}
