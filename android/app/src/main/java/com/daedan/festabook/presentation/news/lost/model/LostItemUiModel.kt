package com.daedan.festabook.presentation.news.lost.model

import android.os.Parcelable
import com.daedan.festabook.domain.model.Lost
import com.daedan.festabook.domain.model.LostItemStatus
import kotlinx.parcelize.Parcelize
import java.time.format.DateTimeFormatter

sealed interface LostUiModel {
    @Parcelize
    data class Item(
        val lostItemId: Long,
        val imageUrl: String,
        val storageLocation: String,
        val status: LostItemUiStatus,
        val createdAt: String,
    ) : Parcelable,
        LostUiModel

    data class Guide(
        val description: String = "",
        val isExpanded: Boolean = false,
    ) : LostUiModel
}

fun Lost.Item.toLostItemUiModel(): LostUiModel =
    LostUiModel.Item(
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

fun Lost.Guide.toLostGuideItemUiModel(): LostUiModel = LostUiModel.Guide(description = guide)
