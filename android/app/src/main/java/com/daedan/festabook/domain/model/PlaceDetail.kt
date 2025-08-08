package com.daedan.festabook.domain.model

import java.time.LocalTime

data class PlaceDetail(
    val id: Long,
    val place: Place,
    private val notices: List<Notice>,
    val host: String?,
    val startTime: LocalTime?,
    val endTime: LocalTime?,
    private val images: List<PlaceDetailImage>,
) {
    val sortedNotices: List<Notice> =
        notices
            .sortedByDescending { it.createdAt }
            .take(3)
    val sortedImages: List<PlaceDetailImage> = images.sortedBy { it.sequence }
}
