package com.daedan.festabook.domain.model

import java.time.LocalDateTime

data class LineupItem(
    val id: Long,
    val imageUrl: String,
    val name: String,
    val performanceAt: LocalDateTime,
)
