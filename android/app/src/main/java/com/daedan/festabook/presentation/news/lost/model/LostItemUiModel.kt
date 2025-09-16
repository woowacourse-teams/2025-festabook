package com.daedan.festabook.presentation.news.lost.model

import android.os.Parcelable
import com.daedan.festabook.domain.model.LostItem
import com.daedan.festabook.domain.model.LostItemStatus
import kotlinx.parcelize.Parcelize
import java.time.format.DateTimeFormatter

@Parcelize
data class LostItemUiModel(
    val lostItemId: Long,
    val imageUrl: String,
    val storageLocation: String,
    val status: LostItemUiStatus,
    val createdAt: String,
) : Parcelable

fun LostItem.toUiModel(): LostItemUiModel =
    LostItemUiModel(
        lostItemId = lostItemId,
        imageUrl = imageUrl,
        storageLocation = storageLocation,
        status =
            when (status) {
                LostItemStatus.PENDING -> LostItemUiStatus.PENDING
                LostItemStatus.COMPLETED -> LostItemUiStatus.COMPLETED
                LostItemStatus.UNKNOWN -> LostItemUiStatus.PENDING
            },
        createdAt = createdAt.format(DateTimeFormatter.ofPattern("yyyy.MM.dd  HH:mm")),
    )
