package com.daedan.festabook.domain.model

import timber.log.Timber
import java.time.LocalDateTime

data class LostItem(
    val lostItemId: Long,
    val imageUrl: String,
    val storageLocation: String,
    val status: LostItemStatus,
    val createdAt: LocalDateTime,
)

fun String.toLocalDateTime(): LocalDateTime =
    runCatching {
        LocalDateTime.parse(this)
    }.onFailure {
        Timber.e(it, "LostItem: 날짜 파싱 실패:${it.message}")
    }.getOrElse {
        LocalDateTime.MIN
    }
