package com.daedan.festabook.data.datasource.remote.placeDetail

import com.daedan.festabook.data.datasource.remote.ApiResult
import com.daedan.festabook.data.model.response.PlaceDetailResponse
import com.daedan.festabook.data.service.PlaceService

class PlaceDetailDataSourceImpl(
    private val placeService: PlaceService,
) : PlaceDetailDataSource {
    override suspend fun fetchPlaceDetail(): ApiResult<PlaceDetailResponse> = ApiResult.toApiResult { placeService.getPlaceDetail() }
}
