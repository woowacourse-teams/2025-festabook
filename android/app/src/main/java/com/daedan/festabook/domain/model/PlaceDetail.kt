package com.daedan.festabook.domain.model

data class PlaceDetail(
    val id: Long,
    val place: Place,
    val notices: List<Notice>,
    val host: String,
    val startTime: String,
    val endTime: String,
    val images: List<PlaceDetailImage>,
)
