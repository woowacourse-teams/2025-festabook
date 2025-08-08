package com.daedan.festabook.data.datasource.remote.organization

import com.daedan.festabook.data.datasource.remote.ApiResult
import com.daedan.festabook.data.model.response.festival.OrganizationResponse
import com.daedan.festabook.data.service.FestivalService

class FestivalDataSourceImpl(
    private val festivalService: FestivalService,
) : FestivalDataSource {
    override suspend fun fetchFestival(): ApiResult<OrganizationResponse> =
        ApiResult.toApiResult {
            festivalService.fetchOrganization()
        }
}
