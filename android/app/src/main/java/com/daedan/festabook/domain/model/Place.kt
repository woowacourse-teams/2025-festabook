package com.daedan.festabook.domain.model

data class Place(
    val id: Long,
    val imageUrl: String?,
    val category: PlaceCategory,
    val title: String?,
    val description: String?,
    val location: String?,
    val timeTags: List<TimeTag>,
)
