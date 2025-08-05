package com.daedan.festabook.presentation.news.lost.model

import android.os.Parcelable
import com.daedan.festabook.domain.model.LostItem
import kotlinx.parcelize.Parcelize

@Parcelize
data class LostItemUiModel(
    val imageId: Long = -1L,
    val imageUrl: String = "",
    val storageLocation: String = "",
) : Parcelable

fun LostItem.toUiModel(): LostItemUiModel =
    LostItemUiModel(
        imageId = imageId,
        imageUrl = imageUrl,
        storageLocation = storageLocation,
    )
