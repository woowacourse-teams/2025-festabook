package com.daedan.festabook.data.service

import com.daedan.festabook.data.model.response.organization.OrganizationGeographyResponse
import com.daedan.festabook.data.model.response.OrganizationResponse
import retrofit2.Response
import retrofit2.http.GET

interface OrganizationService {
    @GET("organizations")
    suspend fun fetchOrganization(): Response<OrganizationResponse>

    @GET("organizations/geography")
    suspend fun fetchOrganizationGeography(): Response<OrganizationGeographyResponse>
}
