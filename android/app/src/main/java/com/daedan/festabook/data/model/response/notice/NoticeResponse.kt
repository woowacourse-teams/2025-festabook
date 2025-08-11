package com.daedan.festabook.data.model.response.notice

import com.daedan.festabook.domain.model.Notice
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class NoticeResponse(
    @SerialName("announcementId")
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

fun NoticeResponse.toDomain() =
    Notice(
        id = id,
        title = title,
        content = content,
        isPinned = isPinned,
        createdAt = LocalDateTime.parse(createdAt),
    )
