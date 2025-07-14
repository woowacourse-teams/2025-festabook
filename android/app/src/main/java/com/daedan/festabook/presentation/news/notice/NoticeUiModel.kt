package com.daedan.festabook.presentation.news.notice

data class NoticeUiModel(
    val title: String,
    val description: String,
    val createdAt: String,
    val isPinned: Boolean = false,
    val isExpanded: Boolean = false,
)
