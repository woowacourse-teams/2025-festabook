package com.daedan.festabook.data.model.response.lostitem

import com.daedan.festabook.domain.model.Lost
import com.daedan.festabook.domain.model.LostItemStatus
import com.daedan.festabook.domain.model.toLocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import timber.log.Timber

@Serializable
data class LostItemResponse(
    @SerialName("lostItemId")
    val lostItemId: Long,
    @SerialName("imageUrl")
    val imageUrl: String,
    @SerialName("storageLocation")
    val storageLocation: String,
    @Serializable(with = LostItemStatusSerializer::class)
    @SerialName("pickupStatus")
    val pickupStatus: LostItemStatus,
    @SerialName("createdAt")
    val createdAt: String,
)

fun LostItemResponse.toDomain(): Lost.Item =
    Lost.Item(
        lostItemId = lostItemId,
        imageUrl = imageUrl,
        storageLocation = storageLocation,
        status =
            pickupStatus.also { if (it == LostItemStatus.UNKNOWN) Timber.e("${::toDomain.name}: 알 수 없는 분실물 상태") },
        createdAt = createdAt.toLocalDateTime(),
    )
