package com.daedan.festabook.data.datasource.remote.organization

import com.daedan.festabook.data.datasource.remote.ApiResult
import com.daedan.festabook.data.model.response.festival.OrganizationResponse

interface FestivalDataSource {
    suspend fun fetchFestival(): ApiResult<OrganizationResponse>
}
