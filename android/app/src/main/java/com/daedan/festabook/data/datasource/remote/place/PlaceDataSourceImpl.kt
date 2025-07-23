package com.daedan.festabook.data.datasource.remote.place

import com.daedan.festabook.data.datasource.remote.ApiResult
import com.daedan.festabook.data.model.response.PlaceDetailResponse
import com.daedan.festabook.data.model.response.PlaceResponse
import com.daedan.festabook.data.service.PlaceService

class PlaceDataSourceImpl(
    private val placeService: PlaceService,
) : PlaceDataSource {
    override suspend fun fetchPlaces(): ApiResult<List<PlaceResponse>> = ApiResult.toApiResult { placeService.getPlaces() }

    override suspend fun fetchPlaceDetail(): ApiResult<PlaceDetailResponse> = ApiResult.toApiResult { placeService.getPlaceDetail() }
}
