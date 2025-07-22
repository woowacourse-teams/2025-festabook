package com.daedan.festabook.data.datasource.remote.placeList

import com.daedan.festabook.data.datasource.remote.ApiResult
import com.daedan.festabook.data.model.response.PlaceResponse

interface PlaceListDataSource {
    suspend fun fetchPlaces(): ApiResult<List<PlaceResponse>>
}
