package com.daedan.festabook.domain.model

import java.time.LocalDateTime

data class LostItem(
    val lostItemId: Long,
    val imageUrl: String,
    val storageLocation: String,
    val status: LostItemStatus,
    val createdAt: LocalDateTime,
)

fun String.toLocalDateTime() = LocalDateTime.parse(this)
