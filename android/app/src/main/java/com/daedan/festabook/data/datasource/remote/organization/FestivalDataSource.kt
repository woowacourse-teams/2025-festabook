package com.daedan.festabook.data.datasource.remote.organization

import com.daedan.festabook.data.datasource.remote.ApiResult
import com.daedan.festabook.data.model.response.OrganizationResponse

interface FestivalDataSource {
    suspend fun fetchFestival(): ApiResult<OrganizationResponse>
}
