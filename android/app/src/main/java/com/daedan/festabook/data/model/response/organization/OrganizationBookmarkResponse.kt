package com.daedan.festabook.data.model.response.organization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrganizationBookmarkResponse(
    @SerialName("id")
    val organizationBookmarkId: Long,
)
