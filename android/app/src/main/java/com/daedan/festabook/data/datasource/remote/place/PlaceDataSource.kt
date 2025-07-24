package com.daedan.festabook.data.datasource.remote.place

import com.daedan.festabook.data.datasource.remote.ApiResult
import com.daedan.festabook.data.model.response.OrganizationGeographyResponse
import com.daedan.festabook.data.model.response.PlaceDetailResponse
import com.daedan.festabook.data.model.response.PlaceGeographyResponse
import com.daedan.festabook.data.model.response.PlaceResponse

interface PlaceDataSource {
    suspend fun fetchPlaceDetail(placeId: Long): ApiResult<PlaceDetailResponse>

    suspend fun fetchPlaces(): ApiResult<List<PlaceResponse>>

    suspend fun fetchOrganizationGeography(): ApiResult<OrganizationGeographyResponse>

    suspend fun fetchPlaceGeographies(): ApiResult<List<PlaceGeographyResponse>>
}
