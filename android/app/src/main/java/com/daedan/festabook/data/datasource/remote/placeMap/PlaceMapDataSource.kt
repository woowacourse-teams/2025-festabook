package com.daedan.festabook.data.datasource.remote.placeMap

import com.daedan.festabook.data.datasource.remote.ApiResult
import com.daedan.festabook.data.model.response.GeographyResponse

interface PlaceMapDataSource {
    suspend fun fetchPlacesGeography(): ApiResult<GeographyResponse>
}
