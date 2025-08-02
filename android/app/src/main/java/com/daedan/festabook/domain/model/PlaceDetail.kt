package com.daedan.festabook.domain.model

import java.time.LocalTime

data class PlaceDetail(
    val id: Long,
    val place: Place,
    private val _notices: List<Notice>,
    val host: String?,
    val startTime: LocalTime?,
    val endTime: LocalTime?,
    private val _images: List<PlaceDetailImage>,
) {
    val notices: List<Notice> =
        _notices
            .sortedByDescending { it.createdAt }
            .take(3)
    val images: List<PlaceDetailImage> = _images.sortedBy { it.sequence }
}
