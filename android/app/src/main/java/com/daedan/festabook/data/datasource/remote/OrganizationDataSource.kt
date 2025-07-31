package com.daedan.festabook.data.datasource.remote

import com.daedan.festabook.data.model.response.OrganizationResponse

interface OrganizationDataSource {
    suspend fun fetchOrganization(): ApiResult<OrganizationResponse>
}
