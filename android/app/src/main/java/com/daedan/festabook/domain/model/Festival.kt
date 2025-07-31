package com.daedan.festabook.domain.model

data class Festival(
    val festivalName: String,
    val festivalImages: List<Poster>,
    val startDate: String,
    val endDate: String,
)
