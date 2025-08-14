package com.daedan.festabook.data.service

import com.daedan.festabook.data.model.request.FestivalNotificationRequest
import com.daedan.festabook.data.model.response.festival.FestivalNotificationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

interface FestivalNotificationService {
    @POST("/festivals/{festivalId}/notifications")
    suspend fun bookmarkOrganization(
        @Path("organizationId") id: Long,
        @Body request: FestivalNotificationRequest,
    ): Response<FestivalNotificationResponse>

    @DELETE("/festivals/notifications/{festivalNotificationId}")
    suspend fun deleteOrganizationBookmark(
        @Path("organizationBookmarkId") id: Long,
    ): Response<Unit>
}
