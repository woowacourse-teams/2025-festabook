package com.daedan.festabook.data.datasource.remote.placeMap

import com.daedan.festabook.data.datasource.remote.ApiResult
import com.daedan.festabook.data.model.response.OrganizationGeographyResponse
import com.daedan.festabook.data.model.response.PlaceGeographyResponse

interface PlaceMapDataSource {
    suspend fun fetchOrganizationGeography(): ApiResult<OrganizationGeographyResponse>

    suspend fun fetchPlaceGeographies(): ApiResult<List<PlaceGeographyResponse>>
}
