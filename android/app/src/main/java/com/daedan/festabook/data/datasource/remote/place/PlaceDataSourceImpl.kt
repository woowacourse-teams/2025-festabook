package com.daedan.festabook.data.datasource.remote.place

import com.daedan.festabook.data.datasource.remote.ApiResult
import com.daedan.festabook.data.model.response.OrganizationGeographyResponse
import com.daedan.festabook.data.model.response.PlaceDetailResponse
import com.daedan.festabook.data.model.response.PlaceGeographyResponse
import com.daedan.festabook.data.model.response.PlaceResponse
import com.daedan.festabook.data.service.OrganizationService
import com.daedan.festabook.data.service.PlaceService

class PlaceDataSourceImpl(
    private val placeService: PlaceService,
    private val organizationService: OrganizationService,
) : PlaceDataSource {
    override suspend fun fetchPlaces(): ApiResult<List<PlaceResponse>> = ApiResult.toApiResult { placeService.fetchPlaces() }

    override suspend fun fetchPlaceDetail(placeId: Long): ApiResult<PlaceDetailResponse> =
        ApiResult.toApiResult { placeService.fetchPlaceDetail(placeId) }

    override suspend fun fetchOrganizationGeography(): ApiResult<OrganizationGeographyResponse> =
        ApiResult.toApiResult {
            organizationService.fetchOrganizationGeography()
        }

    override suspend fun fetchPlaceGeographies(): ApiResult<List<PlaceGeographyResponse>> =
        ApiResult.toApiResult {
            placeService.fetchPlaceGeographies()
        }
}
