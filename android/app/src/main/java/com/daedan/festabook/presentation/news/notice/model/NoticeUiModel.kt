package com.daedan.festabook.presentation.news.notice.model

import com.daedan.festabook.domain.model.Notice

data class NoticeUiModel(
    val id: Long,
    val title: String,
    val content: String,
    val createdAt: String,
    val isPinned: Boolean = false,
    val isExpanded: Boolean = false,
)

fun Notice.toUiModel() =
    NoticeUiModel(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt,
        isPinned = isPinned,
    )
