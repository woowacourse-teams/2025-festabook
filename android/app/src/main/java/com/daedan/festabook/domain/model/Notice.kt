package com.daedan.festabook.domain.model

import java.time.LocalDateTime

data class Notice(
    val title: String,
    val description: String,
    val createdAt: LocalDateTime,
    val isPinned: Boolean,
)
