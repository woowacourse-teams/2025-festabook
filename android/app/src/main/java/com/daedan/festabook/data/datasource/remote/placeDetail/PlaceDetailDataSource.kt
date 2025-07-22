package com.daedan.festabook.data.datasource.remote.placeDetail

import com.daedan.festabook.data.datasource.remote.ApiResult
import com.daedan.festabook.data.model.response.PlaceDetailResponse

interface PlaceDetailDataSource {
    suspend fun fetchPlaceDetail(): ApiResult<PlaceDetailResponse>
}
