package com.daedan.festabook.data.datasource.remote.placeMap

import com.daedan.festabook.data.datasource.remote.ApiResult
import com.daedan.festabook.data.model.response.GeographyResponse
import com.daedan.festabook.data.service.OrganizationService

class PlaceMapDataSourceImpl(
    private val organizationService: OrganizationService,
) : PlaceMapDataSource {
    override suspend fun fetchPlacesGeography(): ApiResult<GeographyResponse> =
        ApiResult.toApiResult {
            organizationService.fetchOrganizations()
        }
}
