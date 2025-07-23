package com.daedan.festabook.domain.model

import java.time.LocalDateTime

data class PlaceDetail(
    val id: Long,
    val place: Place,
    val notices: List<Notice>,
    val host: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val images: List<PlaceDetailImage>,
)
