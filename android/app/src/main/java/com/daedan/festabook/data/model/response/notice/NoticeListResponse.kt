package com.daedan.festabook.data.model.response.notice

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NoticeListResponse(
    @SerialName("pinned")
    val pinned: List<NoticeResponse> = emptyList(),
    @SerialName("unpinned")
    val unpinned: List<NoticeResponse> = emptyList(),
)
