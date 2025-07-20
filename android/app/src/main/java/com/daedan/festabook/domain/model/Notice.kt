package com.daedan.festabook.domain.model

import java.time.LocalDateTime

data class Notice(
    val id: Long,
    val title: String,
    val content: String,
    val isPinned: Boolean,
    val createdAt: LocalDateTime,
)
