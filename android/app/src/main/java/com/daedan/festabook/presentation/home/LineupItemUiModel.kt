package com.daedan.festabook.presentation.home

import com.daedan.festabook.domain.model.LineupItem
import java.time.LocalDateTime

data class LineupItemUiModel(
    val id: Long,
    val imageUrl: String,
    val name: String,
    val performanceAt: LocalDateTime,
)

fun LineupItem.toUiModel(): LineupItemUiModel =
    LineupItemUiModel(
        id = id,
        imageUrl = imageUrl,
        name = name,
        performanceAt = performanceAt,
    )
