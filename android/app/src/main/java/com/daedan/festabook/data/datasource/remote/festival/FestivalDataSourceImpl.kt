package com.daedan.festabook.data.datasource.remote.festival

import com.daedan.festabook.data.datasource.remote.ApiResult
import com.daedan.festabook.data.model.response.festival.FestivalResponse
import com.daedan.festabook.data.service.FestivalService

class FestivalDataSourceImpl(
    private val festivalService: FestivalService,
) : FestivalDataSource {
    override suspend fun fetchFestival(): ApiResult<FestivalResponse> =
        ApiResult.toApiResult {
            festivalService.fetchOrganization()
        }
}
