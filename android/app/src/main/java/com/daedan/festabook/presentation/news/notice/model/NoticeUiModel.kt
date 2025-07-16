package com.daedan.festabook.presentation.news.notice.model

data class NoticeUiModel(
    val id: Long,
    val title: String,
    val description: String,
    val createdAt: String,
    val isPinned: Boolean = false,
    val isExpanded: Boolean = false,
)
