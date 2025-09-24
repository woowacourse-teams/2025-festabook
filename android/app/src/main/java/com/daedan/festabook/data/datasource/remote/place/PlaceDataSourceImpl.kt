package com.daedan.festabook.data.datasource.remote.place

import com.daedan.festabook.data.datasource.remote.ApiResult
import com.daedan.festabook.data.model.response.festival.FestivalGeographyResponse
import com.daedan.festabook.data.model.response.place.PlaceDetailResponse
import com.daedan.festabook.data.model.response.place.PlaceGeographyResponse
import com.daedan.festabook.data.model.response.place.PlaceResponse
import com.daedan.festabook.data.model.response.place.TimeTagResponse
import com.daedan.festabook.data.service.FestivalService
import com.daedan.festabook.data.service.PlaceService

class PlaceDataSourceImpl(
    private val placeService: PlaceService,
    private val festivalService: FestivalService,
) : PlaceDataSource {
    override suspend fun fetchPlaces(): ApiResult<List<PlaceResponse>> = ApiResult.toApiResult { placeService.fetchPlaces() }

    override suspend fun fetchTimeTag(): ApiResult<List<TimeTagResponse>> = ApiResult.toApiResult { placeService.fetchTimeTag() }

    override suspend fun fetchPlaceDetail(placeId: Long): ApiResult<PlaceDetailResponse> =
        ApiResult.toApiResult { placeService.fetchPlaceDetail(placeId) }

    override suspend fun fetchOrganizationGeography(): ApiResult<FestivalGeographyResponse> =
        ApiResult.toApiResult {
            festivalService.fetchOrganizationGeography()
        }

    override suspend fun fetchPlaceGeographies(): ApiResult<List<PlaceGeographyResponse>> =
        ApiResult.toApiResult {
            placeService.fetchPlaceGeographies()
        }
}
