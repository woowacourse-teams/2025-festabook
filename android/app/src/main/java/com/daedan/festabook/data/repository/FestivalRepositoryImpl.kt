package com.daedan.festabook.data.repository

import com.daedan.festabook.data.datasource.remote.organization.OrganizationDataSource
import com.daedan.festabook.data.model.response.toDomain
import com.daedan.festabook.data.util.toResult
import com.daedan.festabook.domain.model.Organization
import com.daedan.festabook.domain.repository.FestivalRepository

class FestivalRepositoryImpl(
    private val organizationDataSource: OrganizationDataSource,
) : FestivalRepository {
    override suspend fun getFestivalInfo(): Result<Organization> {
        val response = organizationDataSource.fetchOrganization().toResult()
        return response.mapCatching { it.toDomain() }
    }
}
