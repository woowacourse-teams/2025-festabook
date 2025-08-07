package com.daedan.festabook.data.service

import com.daedan.festabook.data.model.response.OrganizationResponse
import com.daedan.festabook.data.model.response.organization.OrganizationGeographyResponse
import retrofit2.Response
import retrofit2.http.GET

interface FestivalService {
    @GET("festivals")
    suspend fun fetchOrganization(): Response<OrganizationResponse>

    @GET("festivals/geography")
    suspend fun fetchOrganizationGeography(): Response<OrganizationGeographyResponse>
}
