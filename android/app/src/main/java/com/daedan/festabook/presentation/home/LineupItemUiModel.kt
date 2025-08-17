package com.daedan.festabook.presentation.home

import android.os.Parcelable
import com.daedan.festabook.domain.model.LineupItem
import kotlinx.parcelize.Parcelize

@Parcelize
data class LineupItemUiModel(
    val id: Long,
    val imageUrl: String,
    val name: String,
) : Parcelable

fun LineupItem.toUiModel(): LineupItemUiModel =
    LineupItemUiModel(
        id = id,
        imageUrl = imageUrl,
        name = name,
    )
