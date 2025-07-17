package com.daedan.festabook.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NoticeResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("title")
    val title: String,
    @SerialName("content")
    val content: String,
    @SerialName("isPinned")
    val isPinned: Boolean,
    @SerialName("createdAt")
    val createdAt: String,
)
