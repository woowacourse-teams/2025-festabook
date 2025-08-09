package com.daedan.festabook.domain.model

data class LostItem(
    val lostItemId: Long,
    val imageUrl: String,
    val storageLocation: String,
    val status: LostItemStatus,
    val createdAt: String,
)
