package com.daedan.festabook.domain.model

import timber.log.Timber
import java.time.LocalDateTime

sealed interface Lost {
    data class Item(
        val lostItemId: Long,
        val imageUrl: String,
        val storageLocation: String,
        val status: LostItemStatus,
        val createdAt: LocalDateTime,
    ) : Lost

    data class Guide(
        val guide: String,
    ) : Lost
}

fun String.toLocalDateTime(): LocalDateTime =
    runCatching {
        LocalDateTime.parse(this)
    }.onFailure {
        Timber.e(it, "LostItem: 날짜 파싱 실패:${it.message}")
    }.getOrElse {
        LocalDateTime.MIN
    }
