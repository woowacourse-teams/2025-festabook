package com.daedan.festabook.presentation.news.lost.model

import com.daedan.festabook.domain.model.LostItem

data class LostItemUiModel(
    val imageId: Long,
    val imageUrl: String,
)

fun LostItem.toUiModel(): LostItemUiModel =
    LostItemUiModel(
        imageId = imageId,
        imageUrl = imageUrl,
    )
