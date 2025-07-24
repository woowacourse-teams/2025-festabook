package com.daedan.festabook.data.service

import com.daedan.festabook.data.model.request.OrganizationBookmarkRequest
import com.daedan.festabook.data.model.response.OrganizationBookmarkResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

interface OrganizationBookmarkService {
    @POST("/organizations/{organizationId}/bookmarks")
    suspend fun bookmarkOrganization(
        @Path("organizationId") id: Long,
        @Body request: OrganizationBookmarkRequest,
    ): Response<OrganizationBookmarkResponse>

    @DELETE("/organizations/bookmarks/{organizationBookmarkId}")
    suspend fun deleteOrganizationBookmark(
        @Path("organizationBookmarkId") id: Long,
    ): Response<Unit>
}
