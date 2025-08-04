package com.daedan.festabook.data.datasource.remote.organization

import com.daedan.festabook.data.datasource.remote.ApiResult
import com.daedan.festabook.data.model.response.OrganizationResponse

interface OrganizationDataSource {
    suspend fun fetchOrganization(): ApiResult<OrganizationResponse>
}
