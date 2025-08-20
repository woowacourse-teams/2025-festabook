package com.daedan.festabook.presentation.home

import com.daedan.festabook.domain.model.LineupItem

data class LineupItemUiModel(
    val id: Long,
    val imageUrl: String,
    val name: String,
)

fun LineupItem.toUiModel(): LineupItemUiModel =
    LineupItemUiModel(
        id = id,
        imageUrl = imageUrl,
        name = name,
    )
