package com.daedan.festabook.presentation.news.notice.model

import com.daedan.festabook.domain.model.Notice
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class NoticeUiModel(
    val id: Long,
    val title: String,
    val content: String,
    val createdAt: LocalDateTime,
    val isPinned: Boolean = false,
    val isExpanded: Boolean = false,
) {
    val formattedCreatedAt: String
        get() =
            runCatching {
                formatter.format(createdAt)
            }.getOrDefault("")

    companion object {
        private val formatter = DateTimeFormatter.ofPattern("MM/dd HH:mm")
    }
}

fun Notice.toUiModel() =
    NoticeUiModel(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt,
        isPinned = isPinned,
    )
