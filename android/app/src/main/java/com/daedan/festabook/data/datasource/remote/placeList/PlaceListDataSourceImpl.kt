package com.daedan.festabook.data.datasource.remote.placeList

import com.daedan.festabook.data.datasource.remote.ApiResult
import com.daedan.festabook.data.model.response.PlaceResponse
import com.daedan.festabook.data.service.PlaceService

class PlaceListDataSourceImpl(
    private val placeService: PlaceService,
) : PlaceListDataSource {
    override suspend fun fetchPlaces(): ApiResult<List<PlaceResponse>> = ApiResult.toApiResult { placeService.getPlaces() }
}
