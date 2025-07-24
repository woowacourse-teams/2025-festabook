package com.daedan.festabook.data.service

import com.daedan.festabook.data.model.response.GeographyResponse
import retrofit2.Response
import retrofit2.http.GET

interface OrganizationService {
    @GET("organizations/geography")
    suspend fun fetchOrganizations(): Response<GeographyResponse>
}
