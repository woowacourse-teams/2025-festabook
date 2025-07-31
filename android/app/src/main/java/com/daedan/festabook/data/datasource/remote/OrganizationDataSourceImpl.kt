package com.daedan.festabook.data.datasource.remote

import com.daedan.festabook.data.model.response.OrganizationResponse
import com.daedan.festabook.data.service.OrganizationService

class OrganizationDataSourceImpl(
    private val organizationService: OrganizationService,
) : OrganizationDataSource {
    override suspend fun fetchOrganization(): ApiResult<OrganizationResponse> =
        ApiResult.toApiResult {
            organizationService.fetchOrganization()
        }
}
