package com.daedan.festabook.domain.model

import java.time.LocalTime

data class PlaceDetail(
    val id: Long,
    val place: Place,
    val notices: List<Notice>,
    val host: String?,
    val startTime: LocalTime?,
    val endTime: LocalTime?,
    val images: List<PlaceDetailImage>,
) {
    init {
        host?.let {
            require(it.length <= 100)
        }
        require((startTime == null) == (endTime == null))
    }
}
