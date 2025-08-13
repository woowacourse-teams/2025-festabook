package com.daedan.festabook.data.model.response.lostitem

import com.daedan.festabook.domain.model.LostItem
import com.daedan.festabook.domain.model.LostItemStatus
import com.daedan.festabook.domain.model.toLocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LostItemResponse(
    @SerialName("lostItemId")
    val lostItemId: Long,
    @SerialName("imageUrl")
    val imageUrl: String,
    @SerialName("storageLocation")
    val storageLocation: String,
    @SerialName("status")
    @Serializable(with = LostItemStatusSerializer::class)
    val status: LostItemStatus,
    @SerialName("createdAt")
    val createdAt: String,
)

fun LostItemResponse.toDomain(): LostItem =
    LostItem(
        lostItemId = lostItemId,
        imageUrl = imageUrl,
        storageLocation = storageLocation,
        status = status,
        createdAt = createdAt.toLocalDateTime(),
    )
