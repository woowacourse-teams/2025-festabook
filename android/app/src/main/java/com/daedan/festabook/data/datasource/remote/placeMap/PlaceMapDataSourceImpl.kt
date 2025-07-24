package com.daedan.festabook.data.datasource.remote.placeMap

import com.daedan.festabook.data.datasource.remote.ApiResult
import com.daedan.festabook.data.model.response.OrganizationGeographyResponse
import com.daedan.festabook.data.model.response.PlaceGeographyResponse
import com.daedan.festabook.data.service.OrganizationService
import com.daedan.festabook.data.service.PlaceService

class PlaceMapDataSourceImpl(
    private val organizationService: OrganizationService,
    private val placeService: PlaceService,
) : PlaceMapDataSource {
    override suspend fun fetchOrganizationGeography(): ApiResult<OrganizationGeographyResponse> =
        ApiResult.toApiResult {
            organizationService.fetchOrganizations()
        }

    override suspend fun fetchPlaceGeographies(): ApiResult<List<PlaceGeographyResponse>> =
        ApiResult.toApiResult {
            placeService.fetchPlaceGeography()
        }
}
