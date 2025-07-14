package com.daedan.festabook.domain.model

data class Notice(
    val title: String,
    val description: String,
    val createdAt: String,
    val isPinned: Boolean,
)
