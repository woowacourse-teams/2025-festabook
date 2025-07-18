package com.daedan.festabook.domain.model

data class Notice(
    val id: Long,
    val title: String,
    val content: String,
    val isPinned: Boolean,
    val createdAt: String,
)
